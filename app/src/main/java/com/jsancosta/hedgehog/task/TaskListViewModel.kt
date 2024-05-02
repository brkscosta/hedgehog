package com.jsancosta.hedgehog.task

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlin.random.Random

data class Task(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val isDone: Boolean = false,
)

class TaskListViewModel : ViewModel() {
    private val _tasks = MutableStateFlow(listOf<Task>())
    val tasks = _tasks.asStateFlow()
    private val _taskAddedFlow = MutableStateFlow(false)
    val taskAdded = _taskAddedFlow.asStateFlow()

    init {
        loadTasks()
    }

    fun setTaskAsDone(task: Task, isDone: Boolean) {
        _tasks.update { tasks ->
            tasks.map { currentTask ->
                if (currentTask.title == task.title) {
                    currentTask.copy(isDone = isDone)
                } else {
                    currentTask
                }
            }.sortedBy { it.isDone }
        }
    }

    fun deleteTask(task: Task) {
        _tasks.update { tasks ->
            tasks.filter { it.title != task.title }
        }
    }

    fun updateTask(id: String, values: Map<String, String>) {
        _tasks.update { tasks ->
            tasks.map { currentTask ->
                if (currentTask.id == id) {
                    currentTask.copy(
                        title = values["title"] ?: currentTask.title,
                        description = values["description"] ?: currentTask.description
                    )
                } else {
                    currentTask
                }
            }
        }
    }

    fun addTask(values: Map<String, String>) {
        _tasks.update { tasks ->
            tasks + Task(
                id = generateId(),
                title = values["title"] ?: "",
                description = values["description"] ?: "",
            )
        }
        _taskAddedFlow.value = true
    }

    private fun loadTasks() {
        _tasks.value = (1..10).map {
            Task(generateId(), "Task $it", "Subtitle $it", false)
        }
    }

    private fun generateId(): String {
        val charPool: List<Char> = ('a'..'z') + ('A'..'Z') + ('0'..'9')
        return (1..6).map { Random.nextInt(0, charPool.size) }.map(charPool::get)
            .joinToString("")
    }
}