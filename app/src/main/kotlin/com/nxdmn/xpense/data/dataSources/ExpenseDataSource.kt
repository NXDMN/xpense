package com.nxdmn.xpense.data.dataSources

import com.nxdmn.xpense.data.models.ExpenseModel
import kotlinx.coroutines.flow.Flow

interface ExpenseDataSource {
    fun getAllAsFlow(): Flow<List<ExpenseModel>>
    suspend fun getAll(): List<ExpenseModel>
    suspend fun get(id: Long): ExpenseModel?
    suspend fun create(expense: ExpenseModel)
    suspend fun update(expense: ExpenseModel)
    suspend fun delete(id: Long)
}