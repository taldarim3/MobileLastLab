package com.example.kotlinapp.data

import android.app.Application
import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.kotlinapp.data.models.UserDao
import com.example.kotlinapp.data.models.UserModel


@Database(entities = [UserModel::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao

    companion object {
        private var instance: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase{
            return if(instance != null)
                instance as AppDatabase
            else{
                instance = Room.databaseBuilder(
                    context,
                    AppDatabase::class.java, "database-name"
                ).build()

                instance as AppDatabase
            }
        }
    }
}
