package com.nxdmn.xpense.data.repositories

import com.nxdmn.xpense.data.models.ExpenseModel
import com.nxdmn.xpense.data.dataSources.ExpenseDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class ExpenseRepository(
    private val expenseLocalDataSource: ExpenseDataSource, // Local data source
) {
    val expenseListFlow: Flow<List<ExpenseModel>> = expenseLocalDataSource.getAllAsFlow()

    // Mutex to make writes to cached values thread-safe.
    private val getAllExpensesMutex = Mutex()

    // Cache of the expenseList
    private var expenseList: List<ExpenseModel> = emptyList()

    suspend fun getAllExpenses(refresh: Boolean = false): List<ExpenseModel> {
        if (refresh || expenseList.isEmpty()) {
            val data = expenseLocalDataSource.findAll()
            // Thread-safe write to expenseList
            getAllExpensesMutex.withLock {
                this.expenseList = data
            }
        }

        return getAllExpensesMutex.withLock { this.expenseList }
    }

    suspend fun getExpense(id: Long): ExpenseModel? {
        return if (expenseList.isNotEmpty()) expenseList.find { it.id == id }
        else expenseLocalDataSource.find(id)
    }

    suspend fun updateExpense(expense: ExpenseModel) = expenseLocalDataSource.update(expense)

    suspend fun createExpense(expense: ExpenseModel) = expenseLocalDataSource.create(expense)

    suspend fun deleteExpense(expense: ExpenseModel) = expenseLocalDataSource.delete(expense)
}