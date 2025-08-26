package com.nxdmn.xpense.data.models

import androidx.room.*
import com.nxdmn.xpense.data.converters.ListStringConverters
import com.nxdmn.xpense.data.converters.LocalDateConverters
import com.nxdmn.xpense.data.converters.LocalDateSerializer
import kotlinx.serialization.Serializable
import java.time.LocalDate

@Serializable
data class ExpenseModel(
    var id: Long = 0,
    var amount: Double,
    @Serializable(with = LocalDateSerializer::class)
    var date: LocalDate = LocalDate.now(),
    var category: CategoryModel,
    var remarks: String = "",
    var images: List<String> = emptyList()
)

@TypeConverters(LocalDateConverters::class, ListStringConverters::class)
@Entity(
    foreignKeys = [
        ForeignKey(
            entity = CategoryEntity::class,
            parentColumns = ["id"],
            childColumns = ["categoryId"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        )
    ],
    indices = [Index("categoryId")]
)
data class ExpenseEntity(
    @PrimaryKey(autoGenerate = true) var id: Long = 0,
    var amount: Double,
    var date: LocalDate = LocalDate.now(),
    var categoryId: Long,
    var remarks: String = "",
    var images: List<String> = emptyList()
)


fun ExpenseModel.asEntity() = ExpenseEntity(
    id = id,
    amount = amount,
    date = date,
    categoryId = category.id,
    remarks = remarks,
    images = images,
)

fun ExpenseEntity.asModel(category: CategoryModel) = ExpenseModel(
    id = id,
    amount = amount,
    date = date,
    category = category,
    remarks = remarks,
    images = images,
)