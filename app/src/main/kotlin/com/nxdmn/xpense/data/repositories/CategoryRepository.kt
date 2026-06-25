package com.nxdmn.xpense.data.repositories

import com.nxdmn.xpense.data.dataSources.CategoryDataSource
import com.nxdmn.xpense.data.models.CategoryModel
import com.nxdmn.xpense.ui.CategoryIcon
import kotlinx.coroutines.flow.Flow

class CategoryRepository(
    private val categoryLocalDataSource: CategoryDataSource, // Local data source
) {
    val categoryListFlow: Flow<List<CategoryModel>> = categoryLocalDataSource.getAllAsFlow()

    suspend fun getAllCategories(): List<CategoryModel> {
        var data = categoryLocalDataSource.getAll()

        if (data.isEmpty()) data = generateDefaultList()

        return data.sortedByDescending { it.count }
    }

    suspend fun getCategory(id: Long): CategoryModel? = categoryLocalDataSource.get(id)

    suspend fun updateCategory(category: CategoryModel) = categoryLocalDataSource.update(category)

    suspend fun createCategory(category: CategoryModel) = categoryLocalDataSource.create(category)

    suspend fun deleteCategory(id: Long) = categoryLocalDataSource.delete(id)

    private suspend fun generateDefaultList(): List<CategoryModel> {
        val rawDefaults = listOf(
            CategoryModel(name = "Food", icon = CategoryIcon.EATING, color = 0xFFACDDDE),
            CategoryModel(name = "Clothes", icon = CategoryIcon.CLOTHING, color = 0xFFCAF1DE),
            CategoryModel(name = "Others", icon = CategoryIcon.OTHERS, color = 0xFFE1F8DC),
            CategoryModel(
                name = "Entertainment",
                icon = CategoryIcon.ENTERTAINMENT,
                color = 0xFFFEF8DD
            ),
            CategoryModel(name = "Family", icon = CategoryIcon.FAMILY, color = 0xFFFFE7C7),
            CategoryModel(name = "Fuel", icon = CategoryIcon.FUEL, color = 0xFFF7D8BA),
            CategoryModel(name = "Gift", icon = CategoryIcon.GIFT, color = 0xFF5B657F),
            CategoryModel(name = "Groceries", icon = CategoryIcon.GROCERIES, color = 0xFF9E8867),
            CategoryModel(name = "Rental", icon = CategoryIcon.HOME, color = 0xFFBFA583),
            CategoryModel(name = "Medical", icon = CategoryIcon.MEDICAL, color = 0xFFA76B4F),
            CategoryModel(name = "Phone bill", icon = CategoryIcon.PHONE_BILL, color = 0xFF805544),
            CategoryModel(name = "Shopping", icon = CategoryIcon.SHOPPING, color = 0xFF6FB4DE),
            CategoryModel(name = "Sports", icon = CategoryIcon.SPORTS, color = 0xFF81E2F0),
            CategoryModel(name = "Trip", icon = CategoryIcon.TRAVEL, color = 0xFFFCEEC9),
            CategoryModel(name = "Utilities", icon = CategoryIcon.UTILITIES, color = 0xFFF2B9A1),
            CategoryModel(name = "Insurance", icon = CategoryIcon.LIFE, color = 0xFFD4727D),
        )
        return rawDefaults.map { category ->
            val realDatabaseId = categoryLocalDataSource.create(category)
            category.copy(id = realDatabaseId)
        }
    }
}