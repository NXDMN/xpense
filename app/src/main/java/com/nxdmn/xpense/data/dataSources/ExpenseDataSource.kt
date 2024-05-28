package com.nxdmn.xpense.data.dataSources

import com.nxdmn.xpense.data.models.ExpenseModel

interface ExpenseDataSource {
    suspend fun findAll(): List<ExpenseModel>
    suspend fun create(expense: ExpenseModel)
    suspend fun update(expense: ExpenseModel)
    suspend fun delete(expense: ExpenseModel)
}