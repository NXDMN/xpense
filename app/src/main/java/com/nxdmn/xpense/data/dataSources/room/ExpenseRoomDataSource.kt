package com.nxdmn.xpense.data.dataSources.room

import com.nxdmn.xpense.data.models.ExpenseModel
import com.nxdmn.xpense.data.dataSources.ExpenseDataSource
import com.nxdmn.xpense.data.models.asEntity
import com.nxdmn.xpense.data.relations.asModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ExpenseRoomDataSource(private val dao: ExpenseDao) : ExpenseDataSource {
    override fun getAllAsFlow(): Flow<List<ExpenseModel>> {
        return dao.getExpenseWithCategoryAsFlow().map { list -> list.map { it.asModel() } }
    }

    override suspend fun findAll(): List<ExpenseModel> {
        return dao.getExpenseWithCategory().map { it.asModel() }
    }

    override suspend fun find(id: Long): ExpenseModel? {
        return dao.getExpenseWithCategoryById(id)?.asModel()
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