package com.nxdmn.xpense.data.dataSources

import com.nxdmn.xpense.data.models.CategoryModel
import kotlinx.coroutines.flow.Flow

interface CategoryDataSource {
    fun getAllAsFlow(): Flow<List<CategoryModel>>
    suspend fun getAll(): List<CategoryModel>
    suspend fun get(id: Long): CategoryModel?
    suspend fun create(category: CategoryModel)
    suspend fun update(category: CategoryModel)
    suspend fun delete(id: Long)
}