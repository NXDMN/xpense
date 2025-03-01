package com.nxdmn.xpense.data.relations

import androidx.room.Embedded
import androidx.room.Relation
import com.nxdmn.xpense.data.models.CategoryEntity
import com.nxdmn.xpense.data.models.ExpenseEntity
import com.nxdmn.xpense.data.models.ExpenseModel
import com.nxdmn.xpense.data.models.asModel

data class ExpenseWithCategory(
    @Embedded
    val expense: ExpenseEntity,
    @Relation(
        entity = CategoryEntity::class,
        parentColumn = "categoryId",
        entityColumn = "id"
    )
    val category: CategoryEntity
)

fun ExpenseWithCategory.asModel() = ExpenseModel(
    id = expense.id,
    amount = expense.amount,
    date = expense.date,
    category = category.asModel(),
    remarks = expense.remarks,
    image = expense.image,
)