package com.nxdmn.xpense

import android.app.Application
import android.content.Context
import com.nxdmn.xpense.data.dataSources.json.CategoryJSONDataSource
import com.nxdmn.xpense.data.dataSources.json.ExpenseJSONDataSource
import com.nxdmn.xpense.data.dataSources.room.AppDatabase
import com.nxdmn.xpense.data.dataSources.room.CategoryRoomDataSource
import com.nxdmn.xpense.data.dataSources.room.ExpenseRoomDataSource
import com.nxdmn.xpense.data.dataStores.UserPrefsDataStore
import com.nxdmn.xpense.data.repositories.CategoryRepository
import com.nxdmn.xpense.data.repositories.ExpenseRepository

class MainApplication : Application() {
    lateinit var appContainer: AppContainer

    override fun onCreate() {
        super.onCreate()

        appContainer = AppContainer(this)
    }
}

class AppContainer(val context: Context) {
    // DataStore
    val userPrefsDataStore = UserPrefsDataStore(context)

    // ROOM
    val appDatabase = AppDatabase.getDatabase(context)
    val expenseRepository = ExpenseRepository(ExpenseRoomDataSource(appDatabase.expenseDao()))
    val categoryRepository = CategoryRepository(CategoryRoomDataSource(appDatabase.categoryDao()))

    // JSON
//    val expenseRepository = ExpenseRepository(ExpenseJSONDataSource(context))
//    val categoryRepository = CategoryRepository(CategoryJSONDataSource(context))
}