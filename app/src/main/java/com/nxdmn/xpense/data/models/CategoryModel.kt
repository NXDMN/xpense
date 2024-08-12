package com.nxdmn.xpense.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.nxdmn.xpense.helpers.CategoryIconConverters
import com.nxdmn.xpense.helpers.EnumSerializer
import com.nxdmn.xpense.ui.CategoryIcon
import kotlinx.serialization.Serializable

@Serializable
data class CategoryModel(
    var id: Long = 0,
    var name: String,
    @Serializable(with = CategoryIconSerializer::class)
    var icon: CategoryIcon,
    var count: Int = 0
) {
    private object CategoryIconSerializer : EnumSerializer<CategoryIcon>(
        "CategoryIcon",
        CategoryIcon.entries
    )
}

@TypeConverters(CategoryIconConverters::class)
@Entity
data class CategoryEntity(
    @PrimaryKey(autoGenerate = true) var id: Long = 0,
    var name: String,
    var icon: CategoryIcon,
    var count: Int = 0
)

fun CategoryModel.asEntity() = CategoryEntity(
    id = id,
    name = name,
    icon = icon,
    count = count
)

fun CategoryEntity.asModel() = CategoryModel(
    id = id,
    name = name,
    icon = icon,
    count = count
)