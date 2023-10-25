package com.example.a2

import androidx.compose.foundation.clickable
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val defaultTasks = List(20) { index ->
                Task(id = index, name = "Tarea ${index + 1}", isCompleted = false)
            }
            val tasks = remember { mutableStateListOf<Task>() }
            tasks.addAll(defaultTasks)
            MyApp(tasks)
        }
    }
}

data class Task(val id: Int, val name: String, var isCompleted: Boolean)

@Composable
fun TaskItem(task: Task, onUpdateTask: (Task) -> Unit) {
    var editing by remember { mutableStateOf(false) }
    var editedName by remember { mutableStateOf(task.name) }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Checkbox(
            checked = task.isCompleted,
            onCheckedChange = { updated ->
                onUpdateTask(task.copy(isCompleted = updated))
            }
        )

        Spacer(modifier = Modifier.width(8.dp))

        if (editing) {
            BasicTextField(
                value = editedName,
                onValueChange = { updatedName ->
                    editedName = updatedName
                },
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = androidx.compose.ui.text.input.ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        editing = false
                        if (editedName.isNotBlank()) {
                            onUpdateTask(task.copy(name = editedName))
                        }
                    }
                ),
                modifier = Modifier
                    .weight(1f)
                    .background(Color.White)
            )
        } else {
            Text(
                text = editedName,
                modifier = Modifier.weight(1f).clickable { editing = true }
            )
        }
    }
}

@Composable
fun TaskList(tasks: List<Task>, onUpdateTask: (Task) -> Unit) {
    Column {
        for (task in tasks) {
            TaskItem(task = task, onUpdateTask = onUpdateTask)
        }
    }
}

@Composable
fun MyApp(tasks: MutableList<Task>) {
    var newTaskName by remember { mutableStateOf("") }

    LazyColumn(
        modifier = Modifier.fillMaxSize()
    ) {
        item {
            Text("Lista de Tareas")
            Spacer(modifier = Modifier.height(16.dp))

            TaskList(tasks = tasks) { updatedTask ->
                // Actualiza la tarea en la lista
                val index = tasks.indexOfFirst { it.id == updatedTask.id }
                if (index != -1) {
                    tasks[index] = updatedTask
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            BasicTextField(
                value = newTaskName,
                onValueChange = { newTaskName = it },
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = androidx.compose.ui.text.input.ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        if (newTaskName.isNotBlank()) {
                            val newTask = Task(
                                id = tasks.size, // Assign a new unique ID
                                name = newTaskName,
                                isCompleted = false
                            )
                            tasks.add(newTask)
                            newTaskName = ""
                        }
                    }
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(8.dp))
        }
    }

    Spacer(modifier = Modifier.height(16.dp))

    Button(
        onClick = {
            // Eliminar las tareas completadas
            tasks.removeAll { it.isCompleted }
        }
    ) {
        Text("Borrar Tareas Completadas")
    }
}


@Preview
@Composable
fun MyAppPreview() {
    val tasks = List(20) { index ->
        Task(id = index, name = "Tarea ${index + 1}", isCompleted = false)
    }
    MyApp(tasks = tasks.toMutableList())
}
