package com.nxdmn.xpense.data.dataSources

import com.nxdmn.xpense.data.models.CategoryModel

interface CategoryDataSource {
    suspend fun getAll(): List<CategoryModel>
    suspend fun get(id: Long): CategoryModel?
    suspend fun create(category: CategoryModel)
    suspend fun update(category: CategoryModel)
    suspend fun delete(id: Long)
}