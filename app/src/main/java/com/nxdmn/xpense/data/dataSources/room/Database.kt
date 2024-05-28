package com.nxdmn.xpense.data.dataSources.room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.nxdmn.xpense.data.models.ExpenseEntity

@Database(entities = [ExpenseEntity::class], version = 1)
abstract class Database : RoomDatabase() {

    abstract fun expenseDao(): ExpenseDao
}