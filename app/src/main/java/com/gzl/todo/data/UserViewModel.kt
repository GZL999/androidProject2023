package com.gzl.todo.data

import androidx.lifecycle.ViewModel
import com.gzl.todo.data.Api
import okhttp3.MultipartBody

class UserViewModel : ViewModel() {

    suspend fun UpdateAvatar(uriToRequest : MultipartBody.Part) {
        Api.userWebService.updateAvatar(uriToRequest)
    }
}