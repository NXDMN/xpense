package com.nxdmn.xpense.data.dataSources.room

import com.nxdmn.xpense.data.dataSources.CategoryDataSource
import com.nxdmn.xpense.data.models.CategoryModel
import com.nxdmn.xpense.data.models.asEntity
import com.nxdmn.xpense.data.models.asModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class CategoryRoomDataSource(private val dao: CategoryDao) : CategoryDataSource {
    override fun getAllAsFlow(): Flow<List<CategoryModel>> {
        return dao.getAllAsFlow().map { list -> list.map { it.asModel() } }
    }

    override suspend fun getAll(): List<CategoryModel> {
        return dao.getAll().map { it.asModel() }
    }

    override suspend fun get(id: Long): CategoryModel? {
        return dao.getById(id)?.asModel()
    }

    override suspend fun create(category: CategoryModel) {
        dao.create(category.asEntity())
    }

    override suspend fun update(category: CategoryModel) {
        dao.update(category.asEntity())
    }

    override suspend fun delete(id: Long) {
        dao.deleteById(id)
    }
}