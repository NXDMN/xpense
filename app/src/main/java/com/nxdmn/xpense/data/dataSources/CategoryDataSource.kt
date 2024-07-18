package com.nxdmn.xpense.data.dataSources

import com.nxdmn.xpense.data.models.CategoryModel

interface CategoryDataSource {
    suspend fun findAll(): List<CategoryModel>
    suspend fun create(category: CategoryModel)
    suspend fun update(category: CategoryModel)
    suspend fun delete(category: CategoryModel)
}