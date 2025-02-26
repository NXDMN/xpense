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

        return getAllCategoriesMutex.withLock { this.categoryList.sortedByDescending { it.count } }
    }

    suspend fun getCategory(id: Long): CategoryModel? {
        return if (categoryList.isNotEmpty()) categoryList.find { it.id == id }
        else categoryLocalDataSource.find(id)
    }

    suspend fun updateCategory(category: CategoryModel) = categoryLocalDataSource.update(category)

    suspend fun createCategory(category: CategoryModel) = categoryLocalDataSource.create(category)

    suspend fun deleteCategory(category: CategoryModel) = categoryLocalDataSource.delete(category)

    private suspend fun generateDefaultList(): List<CategoryModel> {
        return listOf(
            CategoryModel(id = 1, name = "Food", icon = CategoryIcon.EATING, color = 0xFFACDDDE),
            CategoryModel(
                id = 2,
                name = "Clothes",
                icon = CategoryIcon.CLOTHING,
                color = 0xFFCAF1DE
            ),
            CategoryModel(id = 3, name = "Others", icon = CategoryIcon.OTHERS, color = 0xFFE1F8DC),
            CategoryModel(
                id = 4,
                name = "Entertainment",
                icon = CategoryIcon.ENTERTAINMENT,
                color = 0xFFFEF8DD
            ),
            CategoryModel(id = 5, name = "Family", icon = CategoryIcon.FAMILY, color = 0xFFFFE7C7),
            CategoryModel(id = 6, name = "Fuel", icon = CategoryIcon.FUEL, color = 0xFFF7D8BA),
            CategoryModel(id = 7, name = "Gift", icon = CategoryIcon.GIFT, color = 0xFF5B657F),
            CategoryModel(
                id = 8,
                name = "Groceries",
                icon = CategoryIcon.GROCERIES,
                color = 0xFF9E8867
            ),
            CategoryModel(id = 9, name = "Rental", icon = CategoryIcon.HOME, color = 0xFFBFA583),
            CategoryModel(
                id = 10,
                name = "Medical",
                icon = CategoryIcon.MEDICAL,
                color = 0xFFA76B4F
            ),
            CategoryModel(
                id = 11,
                name = "Phone bill",
                icon = CategoryIcon.PHONE_BILL,
                color = 0xFF805544
            ),
            CategoryModel(
                id = 12,
                name = "Shopping",
                icon = CategoryIcon.SHOPPING,
                color = 0xFF6FB4DE
            ),
            CategoryModel(id = 13, name = "Sports", icon = CategoryIcon.SPORTS, color = 0xFF81E2F0),
            CategoryModel(id = 14, name = "Trip", icon = CategoryIcon.TRAVEL, color = 0xFFFCEEC9),
            CategoryModel(
                id = 15,
                name = "Utilities",
                icon = CategoryIcon.UTILITIES,
                color = 0xFFF2B9A1
            ),
            CategoryModel(
                id = 16,
                name = "Insurance",
                icon = CategoryIcon.LIFE,
                color = 0xFFD4727D
            ),
        ).onEach { createCategory(it) }
    }
}