package com.nxdmn.xpense.data.dataSources.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.nxdmn.xpense.data.models.ExpenseEntity
import kotlin.concurrent.Volatile

@Database(entities = [ExpenseEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {

    abstract fun expenseDao(): ExpenseDao

    companion object {
        @Volatile
        private var Instance: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase = Instance ?: synchronized(this) {
            Room.databaseBuilder(context, AppDatabase::class.java, "app_database")
                .fallbackToDestructiveMigration(false)
                .build()
                .also { Instance = it }
        }
    }
}