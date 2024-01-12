package com.gzl.todo.user

import android.Manifest
import android.annotation.SuppressLint
import android.content.ContentValues
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.launch
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import coil.compose.AsyncImage
import com.gzl.todo.R
import com.gzl.todo.data.Api
import com.gzl.todo.data.UserViewModel
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

class UserActivity : AppCompatActivity() {
    // propriété: une URI dans le dossier partagé "Images"
    private val captureUri by lazy {
        contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, ContentValues())
    }

    private val viewModel : UserViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            var bitmap: Bitmap? by remember { mutableStateOf(null) }
            var uri: Uri? by remember { mutableStateOf(null) }

            val scope = rememberCoroutineScope()

            // launcher
            val takePicture = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { success ->
                if (success) {
                    uri = captureUri
                    scope.launch {
                        viewModel.UpdateAvatar(uri!!.toRequestBody())
                    }
                }
            }

            val pickPhoto = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) {
                uri = it
                scope.launch {
                    viewModel.UpdateAvatar(uri!!.toRequestBody())
                }
            }

            val getPermission = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.RequestPermission()) {
                pickPhoto.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
            }

            Column {
                AsyncImage(
                    modifier = Modifier.fillMaxHeight(.2f),
                    model = bitmap ?: uri,
                    contentDescription = null
                )
                Button(
                    onClick = {
                        takePicture.launch(captureUri)
                    },
                    content = { Text("Take picture") }
                )
                Button(
                    onClick = {
                        getPermission.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                    },
                    content = { Text("Pick photo") }
                )
            }
        }
    }
    private fun Bitmap.toRequestBody(): MultipartBody.Part {
        val tmpFile = File.createTempFile("avatar", "jpg")
        tmpFile.outputStream().use { // *use* se charge de faire open et close
            this.compress(Bitmap.CompressFormat.JPEG, 100, it) // *this* est le bitmap ici
        }
        return MultipartBody.Part.createFormData(
            name = "avatar",
            filename = "avatar.jpg",
            body = tmpFile.readBytes().toRequestBody()
        )
    }

    @SuppressLint("Recycle")
    private fun Uri.toRequestBody(): MultipartBody.Part {
        val fileInputStream = contentResolver.openInputStream(this)!!
        val fileBody = fileInputStream.readBytes().toRequestBody()
        return MultipartBody.Part.createFormData(
            name = "avatar",
            filename = "avatar.jpg",
            body = fileBody
        )
    }
}
