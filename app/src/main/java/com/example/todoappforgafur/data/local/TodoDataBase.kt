package com.example.todoappforgafur.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.todoappforgafur.data.models.Todo

@Database(entities = [Todo::class], version = 3)
abstract class TodoDataBase : RoomDatabase() {

    abstract fun getTodoDao(): TodoDao

    companion object {
        private const val DATABASE_NAME = "db_name"

        fun getInstance(context: Context): TodoDataBase {
            return Room.databaseBuilder(
                context,
                TodoDataBase::class.java,
                DATABASE_NAME
            ).fallbackToDestructiveMigration().build()
        }
    }
}