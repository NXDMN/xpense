package com.nxdmn.xpense

import android.app.Application
import com.nxdmn.xpense.data.dataSources.json.CategoryJSONDataSource
import com.nxdmn.xpense.data.dataSources.json.ExpenseJSONDataSource
import com.nxdmn.xpense.data.dataSources.room.AppDatabase
import com.nxdmn.xpense.data.dataSources.room.CategoryRoomDataSource
import com.nxdmn.xpense.data.dataSources.room.ExpenseRoomDataSource
import com.nxdmn.xpense.data.dataStores.UserPrefsDataStore
import com.nxdmn.xpense.data.repositories.CategoryRepository
import com.nxdmn.xpense.data.repositories.ExpenseRepository

class MainApplication : Application() {
    lateinit var expenseRepository: ExpenseRepository
    lateinit var categoryRepository: CategoryRepository
    lateinit var userPrefsDataStore: UserPrefsDataStore

    override fun onCreate() {
        super.onCreate()

        // DataStore
        userPrefsDataStore = UserPrefsDataStore(this)

        // ROOM
        val appDatabase = AppDatabase.getDatabase(this)
        expenseRepository = ExpenseRepository(ExpenseRoomDataSource(appDatabase.expenseDao()))
        categoryRepository = CategoryRepository(CategoryRoomDataSource(appDatabase.categoryDao()))

        // JSON
//        expenseRepository = ExpenseRepository(ExpenseJSONDataSource(this))
//        categoryRepository = CategoryRepository(CategoryJSONDataSource(this))
    }
}