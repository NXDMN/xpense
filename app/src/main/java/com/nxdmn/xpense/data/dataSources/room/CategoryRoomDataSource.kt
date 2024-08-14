package com.nxdmn.xpense.data.dataSources.room

import android.content.Context
import com.nxdmn.xpense.data.dataSources.CategoryDataSource
import com.nxdmn.xpense.data.models.CategoryModel
import com.nxdmn.xpense.data.models.asEntity
import com.nxdmn.xpense.data.models.asModel
import com.nxdmn.xpense.data.relations.CategoryWithExpensesModel
import com.nxdmn.xpense.data.relations.asModel

class CategoryRoomDataSource(private val context: Context) : CategoryDataSource {

    private var dao: CategoryDao = AppDatabase.getDatabase(context).categoryDao()

    override suspend fun findAll(): List<CategoryModel> {
        return dao.findAll().map { it.asModel() }
    }

    override suspend fun create(category: CategoryModel) {
        dao.create(category.asEntity())
    }

    override suspend fun update(category: CategoryModel) {
        dao.update(category.asEntity())
    }

    override suspend fun delete(category: CategoryModel) {
        dao.delete(category.asEntity())
    }

    suspend fun findById(id: Long): CategoryModel {
        return dao.findById(id).asModel()
    }

    suspend fun getCategoryWithExpenses(): List<CategoryWithExpensesModel> {
        return dao.getCategoryWithExpenses().map { it.asModel() }
    }
}