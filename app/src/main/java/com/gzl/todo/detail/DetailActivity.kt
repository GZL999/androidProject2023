package com.gzl.todo.detail

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gzl.todo.detail.ui.theme.TodoGonzaloTheme
import com.gzl.todo.list.Task
import java.util.UUID

class DetailActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val initialTask = intent.getSerializableExtra("task") as? Task

            Detail(initialTask = initialTask) { task ->
                val resultIntent = Intent().apply {
                    putExtra("task", task)
                }
                setResult(RESULT_OK, resultIntent)
                finish()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Detail(initialTask: Task? = null, onValidate: (Task) -> Unit) {
    var task by remember { mutableStateOf(initialTask ?: Task(id = UUID.randomUUID().toString(), title = "", description = "")) }

    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Title
        Text(
            text = "Add a new task to the list",
            fontSize = 20.sp,
            color = Color.Black,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        )

        // OutlinedTextField for Title
        OutlinedTextField(
            value = task.title,
            onValueChange = { newTitle ->
                task = task.copy(title = newTitle)
            },
            label = { Text("Title") },
            modifier = Modifier.fillMaxWidth()
        )

        // OutlinedTextField for Description
        OutlinedTextField(
            value = task.description,
            onValueChange = { newDescription ->
                task = task.copy(description = newDescription)
            },
            label = { Text("Description") },
            modifier = Modifier.fillMaxWidth()
        )

        // Button for validation
        Button(
            onClick = {
                onValidate(task)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Validate")
        }
    }

}

@Preview(showBackground = true)
@Composable
fun DetailPreview() {
    TodoGonzaloTheme {
        Detail { }
    }
}
