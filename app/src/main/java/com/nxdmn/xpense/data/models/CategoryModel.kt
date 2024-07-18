package com.nxdmn.xpense.data.models

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