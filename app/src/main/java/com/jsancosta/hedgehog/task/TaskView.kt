package com.jsancosta.hedgehog.task

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Checkbox
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.jsancosta.hedgehog.ui.shared.GenericDialog
import com.jsancosta.hedgehog.ui.shared.SwipeToDeleteContainer
import com.jsancosta.hedgehog.ui.theme.HeadhogTheme
import kotlinx.coroutines.launch

@Composable
fun TaskListScreen(
    name: String,
    viewModel: TaskListViewModel = viewModel(),
) {
    val showDialog = remember { mutableStateOf(false) }
    val task by remember { mutableStateOf(Task()) }
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    if (showDialog.value) {
        EditOrAddTaskDialog(
            "Add Task",
            task,
            onClose = {
                showDialog.value = false
            },
            onSaveClick = { title, description ->
                showDialog.value = false
                viewModel.addTask(
                    mapOf(
                        "title" to title,
                        "description" to description
                    )
                )
                coroutineScope.launch {
                    viewModel.taskAdded.collect {
                        if (it) {
                            snackbarHostState.showSnackbar(
                                message = "Task Added with success",
                                withDismissAction = true,
                                duration = SnackbarDuration.Short,
                            )
                        }
                    }
                }
            }
        )
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    showDialog.value = true
                }
            ) {
                Icon(
                    Icons.Filled.Add,
                    contentDescription = "Add task",
                    modifier = Modifier.size(32.dp)
                )
            }
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
        ) {
            Column {
                TaskHeader(name)
                TaskList(viewModel.tasks.collectAsState().value,
                    onTaskCheckedChange = { task, isChecked ->
                        viewModel.setTaskAsDone(task, isChecked)
                    },
                    onDelete = { task ->
                        viewModel.deleteTask(task)
                    },
                    onEdit = { id, title, description ->
                        viewModel.updateTask(
                            id,
                            mapOf("title" to title, "description" to description)
                        )
                    }
                )
            }
        }
    }
}

@Composable
fun TaskItem(
    task: Task,
    onCheckedChange: (Boolean) -> Unit,
    onTaskClick: (Task) -> Unit,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp, bottom = 8.dp),
        horizontalArrangement = Arrangement.Start
    ) {
        Checkbox(
            checked = task.isDone, onCheckedChange = onCheckedChange
        )
        Surface(modifier = Modifier.weight(1f), onClick = {
            onTaskClick(task)
        }) {
            Column {
                Text(
                    text = task.title,
                    style = MaterialTheme.typography.titleLarge,
                )
                Text(
                    text = task.description,
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
        }
    }
}

@Composable
fun TaskHeader(name: String = "") {
    Column(
        horizontalAlignment = Alignment.Start,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Hello, $name!",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.align(Alignment.CenterVertically)
            )
        }
    }
}

@Composable
fun EditOrAddTaskDialog(
    actionTitle: String,
    task: Task,
    onSaveClick: (title: String, description: String) -> Unit,
    onClose: () -> Unit
) {
    val title = remember { mutableStateOf(task.title) }
    val description = remember { mutableStateOf(task.description) }

    GenericDialog(
        actionTitle,
        onClose = onClose,
    ) {
        TaskContent(title, description, onSaveClick)
    }
}

@Composable
private fun TaskContent(
    title: MutableState<String>,
    description: MutableState<String>,
    onSaveClick: (title: String, description: String) -> Unit
) {
    OutlinedTextField(
        value = title.value,
        onValueChange = { title.value = it },
        label = { Text("Title") },
        singleLine = true,
        modifier = Modifier.fillMaxWidth(),
        textStyle = MaterialTheme.typography.bodyLarge,
        keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences)
    )

    OutlinedTextField(
        value = description.value,
        onValueChange = { description.value = it },
        label = { Text("Description") },
        singleLine = false,
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp),
        textStyle = MaterialTheme.typography.bodyLarge,
        keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences)
    )

    Button(
        modifier = Modifier
            .fillMaxWidth()
            .padding(0.dp, 10.dp),
        onClick = {
            onSaveClick(title.value, description.value)
        }, colors = ButtonColors(
            contentColor = MaterialTheme.colorScheme.onPrimary,
            containerColor = MaterialTheme.colorScheme.primary,
            disabledContentColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f),
            disabledContainerColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f),
        ),
        enabled = title.value.isNotEmpty()
    ) {
        Text("Save", style = MaterialTheme.typography.headlineSmall)
    }
}

@Composable
fun TaskList(
    tasks: List<Task>,
    onTaskCheckedChange: (Task, Boolean) -> Unit,
    onDelete: (Task) -> Unit,
    onEdit: (id: String, newTitle: String, newDescription: String) -> Unit,
) {
    val openDialog = remember { mutableStateOf(false) }
    val currentTask = remember { mutableStateOf(Task()) }

    if (openDialog.value) {
        EditOrAddTaskDialog(
            "Edit Task",
            task = currentTask.value,
            onClose = {
                openDialog.value = false
            }, onSaveClick = { title, description ->
                openDialog.value = false
                onEdit(currentTask.value.id, title, description)
            }
        )
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
    ) {
        items(tasks, key = {
            it.id
        }) { task ->
            SwipeToDeleteContainer(
                item = task,
                onDelete = onDelete,
                enableSwipeEndToStart = true,
                animationDuration = 900,
            ) { taskItem ->
                TaskItem(task = taskItem, onCheckedChange = { isChecked ->
                    onTaskCheckedChange(taskItem, isChecked)
                }, onTaskClick = {
                    openDialog.value = true
                    currentTask.value = it
                })
            }
        }
    }
}

@Preview(showBackground = true, device = "id:pixel_6", apiLevel = 31)
@Composable
fun TaskListScreenPreview() {
    HeadhogTheme {
        TaskListScreen("Joanã")
    }
}

@Preview(device = "id:pixel_7_pro", apiLevel = 31, showSystemUi = false)
@Composable
fun DialogPreview() {
    HeadhogTheme {
        EditOrAddTaskDialog(
            "Edit Task",
            Task("", "Test", "teste", false),
            onClose = {},
            onSaveClick = { _, _ -> }
        )
    }
}