package com.nxdmn.xpense.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.nxdmn.xpense.data.converters.CategoryIconConverters
import com.nxdmn.xpense.ui.CategoryIcon
import kotlinx.serialization.Serializable

@Serializable
data class CategoryModel(
    var id: Long = 0,
    var name: String,
    var icon: CategoryIcon,
    var color: Long = 0,
    var count: Int = 0
)

@TypeConverters(CategoryIconConverters::class)
@Entity
data class CategoryEntity(
    @PrimaryKey(autoGenerate = true) var id: Long = 0,
    var name: String,
    var icon: CategoryIcon,
    var color: Long = 0,
    var count: Int = 0
)

fun CategoryModel.asEntity() = CategoryEntity(
    id = id,
    name = name,
    icon = icon,
    color = color,
    count = count
)

fun CategoryEntity.asModel() = CategoryModel(
    id = id,
    name = name,
    icon = icon,
    color = color,
    count = count
)