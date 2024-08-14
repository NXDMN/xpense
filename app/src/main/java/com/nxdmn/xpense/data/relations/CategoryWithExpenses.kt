package com.nxdmn.xpense.data.relations

import androidx.room.Embedded
import androidx.room.Relation
import com.nxdmn.xpense.data.models.CategoryEntity
import com.nxdmn.xpense.data.models.CategoryModel
import com.nxdmn.xpense.data.models.ExpenseEntity
import com.nxdmn.xpense.data.models.ExpenseModel
import com.nxdmn.xpense.data.models.asModel

data class CategoryWithExpensesModel(
    val category: CategoryModel,
    val expenses: List<ExpenseModel>
)

data class CategoryWithExpenses(
    @Embedded val category: CategoryEntity,
    @Relation(
        entity = ExpenseEntity::class,
        parentColumn = "id",
        entityColumn = "categoryId"
    )
    val expenses: List<ExpenseEntity>
)

fun CategoryWithExpenses.asModel() = CategoryWithExpensesModel(
    category = category.asModel(),
    expenses = expenses.map { it.asModel(category.asModel()) }
)