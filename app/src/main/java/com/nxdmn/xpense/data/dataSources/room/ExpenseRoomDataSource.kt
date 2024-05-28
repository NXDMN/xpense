package com.nxdmn.xpense.data.dataSources.room

import android.content.Context
import androidx.room.Room
import com.nxdmn.xpense.data.models.ExpenseModel
import com.nxdmn.xpense.data.dataSources.ExpenseDataSource
import com.nxdmn.xpense.data.models.asEntity
import com.nxdmn.xpense.data.models.asModel

class ExpenseRoomDataSource(private val context: Context) : ExpenseDataSource {

    private var dao: ExpenseDao

    init {
        val database = Room.databaseBuilder(context, Database::class.java, "room_sample.db")
            .fallbackToDestructiveMigration(false)
            .build()
        dao = database.expenseDao()
    }

    override suspend fun findAll(): List<ExpenseModel> {
        return dao.findAll().map { it -> it.asModel() }
    }

    override suspend fun create(expense: ExpenseModel) {
        dao.create(expense.asEntity())
    }

    override suspend fun update(expense: ExpenseModel) {
        dao.update(expense.asEntity())
    }

    override suspend fun delete(expense: ExpenseModel) {
        dao.delete(expense.asEntity())
    }
}