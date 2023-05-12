package com.example.todoappforgafur.presentation

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.todoappforgafur.data.local.TodoDataBase
import com.example.todoappforgafur.data.models.Todo
import com.example.todoappforgafur.domain.MainRepository

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val repository =
        MainRepository(TodoDataBase.getInstance(application).getTodoDao())

    private val _liveData = MutableLiveData<List<Todo>>()
    val liveData: LiveData<List<Todo>> = _liveData

    suspend fun getAllTodos() {
        _liveData.value = repository.getAllTodos()
    }

    suspend fun addTodo(todo: Todo) = repository.addTodo(todo)

    suspend fun updateTodo(todo: Todo) = repository.updateTodo(todo)

    suspend fun deleteTodo(todo: Todo) = repository.deleteTodo(todo)
}