package com.gzl.todo.list

import java.io.Serializable

data class Task(val id: String, val title: String, val description: String = "Placeholder description") : Serializable
{
}