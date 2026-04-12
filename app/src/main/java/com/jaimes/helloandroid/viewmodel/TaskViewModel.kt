package com.jaimes.helloandroid.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.jaimes.helloandroid.data.task.Task
import com.jaimes.helloandroid.data.task.TaskRepository

class TaskViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = TaskRepository(application.applicationContext)

    private val _tasks = MutableLiveData<List<Task>>()
    val tasks: LiveData<List<Task>> = _tasks

    private var nextId: Int = 1

    init {
        loadTasks()
    }

    private fun loadTasks() {
        val list = repository.getAllTasks()
        nextId = (list.maxOfOrNull { it.id } ?: 0) + 1
        _tasks.value = list
    }

    fun addTask(title: String, description: String, hasReminder: Boolean) {
        val task = Task(id = nextId++, title = title,
            description = description, hasReminder = hasReminder)
        repository.addTask(task)
        _tasks.value = repository.getAllTasks()
    }

    fun updateTask(task: Task) {
        repository.updateTask(task)
        _tasks.value = repository.getAllTasks()
    }

    fun deleteTask(taskId: Int) {
        repository.deleteTask(taskId)
        _tasks.value = repository.getAllTasks()
    }
}