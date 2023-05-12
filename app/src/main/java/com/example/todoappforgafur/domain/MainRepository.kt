package com.example.todoappforgafur.domain

import com.example.todoappforgafur.data.local.TodoDao
import com.example.todoappforgafur.data.models.Todo

class MainRepository(val dao: TodoDao) {

    suspend fun getAllTodos() = dao.getAllTodos()

    suspend fun addTodo(todo: Todo) = dao.addTodo(todo)

    suspend fun updateTodo(todo: Todo) = dao.updateTodo(todo)

    suspend fun deleteTodo(todo: Todo) = dao.deleteTodo(todo)
}