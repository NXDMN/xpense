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
            CategoryModel(id = 1, name = "Food", icon = CategoryIcon.EATING),
            CategoryModel(id = 2, name = "Clothes", icon = CategoryIcon.CLOTHING),
            CategoryModel(id = 3, name = "Others", icon = CategoryIcon.OTHERS),
            CategoryModel(id = 4, name = "Entertainment", icon = CategoryIcon.ENTERTAINMENT),
            CategoryModel(id = 5, name = "Family", icon = CategoryIcon.FAMILY),
            CategoryModel(id = 6, name = "Fuel", icon = CategoryIcon.FUEL),
            CategoryModel(id = 7, name = "Gift", icon = CategoryIcon.GIFT),
            CategoryModel(id = 8, name = "Groceries", icon = CategoryIcon.GROCERIES),
            CategoryModel(id = 9, name = "Rental", icon = CategoryIcon.HOME),
            CategoryModel(id = 10, name = "Medical", icon = CategoryIcon.MEDICAL),
            CategoryModel(id = 11, name = "Phone bill", icon = CategoryIcon.PHONE_BILL),
            CategoryModel(id = 12, name = "Shopping", icon = CategoryIcon.SHOPPING),
            CategoryModel(id = 13, name = "Sports", icon = CategoryIcon.SPORTS),
            CategoryModel(id = 14, name = "Trip", icon = CategoryIcon.TRAVEL),
            CategoryModel(id = 15, name = "Utilities", icon = CategoryIcon.UTILITIES),
            CategoryModel(id = 16, name = "Insurance", icon = CategoryIcon.LIFE),
        ).onEach { createCategory(it) }
    }
}