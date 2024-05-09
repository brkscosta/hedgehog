package com.jsancosta.hedgehog

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import com.jsancosta.hedgehog.task.TaskListScreen
import com.jsancosta.hedgehog.task.TaskListViewModel
import com.jsancosta.hedgehog.ui.theme.HeadhogTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            HeadhogTheme {

                val viewModel = viewModel<TaskListViewModel>()
                val tasks = viewModel.tasks.collectAsState().value

                TaskListScreen("Hedgehog",
                    tasks,
                    onSaveClick = { title, description, cb ->
                        val result = viewModel.addTask(
                            mapOf(
                                "title" to title,
                                "description" to description
                            )
                        )
                        cb(result)
                    },
                    onTaskCheckedChange = { task, isChecked ->
                        viewModel.setTaskAsDone(
                            task,
                            isChecked
                        )
                    },
                    onDelete = { task ->
                        viewModel.deleteTask(task)
                    },
                    onEdit = { id, title, description ->
                        viewModel.updateTask(
                            id,
                            mapOf("title" to title, "description" to description)
                        )
                    })
            }
        }
    }
}
