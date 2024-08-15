package com.nxdmn.xpense.data.repositories

import com.nxdmn.xpense.data.dataSources.CategoryDataSource
import com.nxdmn.xpense.data.models.CategoryModel
import com.nxdmn.xpense.ui.CategoryIcon
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class CategoryRepository(
    private val categoryLocalDataSource: CategoryDataSource, // Local data source
) {
    // Mutex to make writes to cached values thread-safe.
    private val getAllCategoriesMutex = Mutex()

    // Cache of the categoryList
    private var categoryList: List<CategoryModel> = emptyList()

    suspend fun getAllCategories(refresh: Boolean = false): List<CategoryModel> {
        if (refresh || categoryList.isEmpty()) {
            var data = categoryLocalDataSource.findAll()

            if (data.isEmpty()) data = generateDefaultList()
            
            // Thread-safe write to expenseList
            getAllCategoriesMutex.withLock {
                this.categoryList = data
            }
        }

        return getAllCategoriesMutex.withLock { this.categoryList }
    }

    suspend fun updateCategory(category: CategoryModel) = categoryLocalDataSource.update(category)

    suspend fun createCategory(category: CategoryModel) = categoryLocalDataSource.create(category)

    private suspend fun generateDefaultList(): List<CategoryModel> {
        return listOf(
            CategoryModel(name = "Food", icon = CategoryIcon.EATING),
            CategoryModel(name = "Clothes", icon = CategoryIcon.CLOTHING),
            CategoryModel(name = "Others", icon = CategoryIcon.OTHERS),
            CategoryModel(name = "Entertainment", icon = CategoryIcon.ENTERTAINMENT),
            CategoryModel(name = "Family", icon = CategoryIcon.FAMILY),
            CategoryModel(name = "Fuel", icon = CategoryIcon.FUEL),
            CategoryModel(name = "Gift", icon = CategoryIcon.GIFT),
            CategoryModel(name = "Groceries", icon = CategoryIcon.GROCERIES),
            CategoryModel(name = "Rental", icon = CategoryIcon.HOME),
            CategoryModel(name = "Medical", icon = CategoryIcon.MEDICAL),
            CategoryModel(name = "Phone bill", icon = CategoryIcon.PHONE_BILL),
            CategoryModel(name = "Shopping", icon = CategoryIcon.SHOPPING),
            CategoryModel(name = "Sports", icon = CategoryIcon.SPORTS),
            CategoryModel(name = "Trip", icon = CategoryIcon.TRAVEL),
            CategoryModel(name = "Utilities", icon = CategoryIcon.UTILITIES),
            CategoryModel(name = "Insurance", icon = CategoryIcon.LIFE),
        ).onEach { createCategory(it) }
    }
}