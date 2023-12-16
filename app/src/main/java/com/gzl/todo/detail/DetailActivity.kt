package com.gzl.todo.detail

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.gzl.todo.detail.ui.theme.TodoGonzaloTheme
import com.gzl.todo.list.Task
import java.util.UUID

class DetailActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // Retrieve the task to be edited or null if adding a new task
            val initialTask = intent.getSerializableExtra("task") as? Task

            Detail(initialTask = initialTask) { task ->
                // Handle the validation of the task (either new or edited)
                val resultIntent = Intent().apply {
                    putExtra("task", task)
                }
                setResult(RESULT_OK, resultIntent)
                finish()
            }
        }
    }
}

@Composable
fun Detail(initialTask: Task? = null, onValidate: (Task) -> Unit) {
    // Task state with default empty values
    var task by remember { mutableStateOf(initialTask ?: Task(id = UUID.randomUUID().toString(), title = "", description = "")) }

    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // TextField for the task title
        OutlinedTextField(
            value = task.title,
            onValueChange = { newTitle ->
                task = task.copy(title = newTitle)
            },
            label = { Text("Title") }
        )

        // TextField for the task description
        OutlinedTextField(
            value = task.description,
            onValueChange = { newDescription ->
                task = task.copy(description = newDescription)
            },
            label = { Text("Description") }
        )

        Button(onClick = {
            // Return the task object when the validate button is clicked
            onValidate(task)
        }) {
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