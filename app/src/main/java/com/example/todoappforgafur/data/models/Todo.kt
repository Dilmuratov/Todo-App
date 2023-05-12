package com.example.todoappforgafur.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "todo")
data class Todo(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    var title: String,
    var description: String = "",
    var time: String = "",
    var isCompleted: Boolean = false,
    var deadline: String = "" //inMinute
)
