package com.nxdmn.xpense.data.repositories

import com.nxdmn.xpense.data.models.ExpenseModel
import com.nxdmn.xpense.data.dataSources.ExpenseDataSource
import kotlinx.coroutines.flow.Flow

class ExpenseRepository(
    private val expenseLocalDataSource: ExpenseDataSource, // Local data source
) {
    val expenseListFlow: Flow<List<ExpenseModel>> = expenseLocalDataSource.getAllAsFlow()

    suspend fun getExpense(id: Long): ExpenseModel? = expenseLocalDataSource.get(id)

    suspend fun updateExpense(expense: ExpenseModel) = expenseLocalDataSource.update(expense)

    suspend fun createExpense(expense: ExpenseModel) = expenseLocalDataSource.create(expense)

    suspend fun deleteExpense(id: Long) = expenseLocalDataSource.delete(id)
}