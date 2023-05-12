package com.example.todoappforgafur.data.local

import androidx.room.*
import com.example.todoappforgafur.data.models.Todo

@Dao
interface TodoDao {

    @Query("SELECT * FROM todo")
    suspend fun getAllTodos(): MutableList<Todo>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addTodo(todo: Todo)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateTodo(todo: Todo)

    @Delete
    suspend fun deleteTodo(todo: Todo)
}