package com.nxdmn.xpense.data.repositories

import com.nxdmn.xpense.data.dataSources.CategoryDataSource
import com.nxdmn.xpense.data.models.CategoryModel
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class CategoryRepository(
    private val categoryLocalDataSource: CategoryDataSource, // Local data source
) {
    // Mutex to make writes to cached values thread-safe.
    private val getAllCategoriesMutex = Mutex()

    // Cache of the expenseList
    private var categoryList: List<CategoryModel> = emptyList()

    suspend fun getAllCategories(refresh: Boolean = false): List<CategoryModel> {
        if (refresh || categoryList.isEmpty()) {
            val data = categoryLocalDataSource.findAll()
            // Thread-safe write to expenseList
            getAllCategoriesMutex.withLock {
                this.categoryList = data
            }
        }

        return getAllCategoriesMutex.withLock { this.categoryList }
    }

    suspend fun updateCategory(category: CategoryModel) = categoryLocalDataSource.update(category)

    suspend fun createCategory(category: CategoryModel) = categoryLocalDataSource.create(category)
}