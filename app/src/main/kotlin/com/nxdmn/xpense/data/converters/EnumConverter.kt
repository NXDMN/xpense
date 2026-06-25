package com.nxdmn.xpense.data.converters

import androidx.room.TypeConverter
import com.nxdmn.xpense.ui.CategoryIcon

class CategoryIconConverters {
    @TypeConverter
    fun fromInt(value: Int?): CategoryIcon? =
        if (value != null) enumValues<CategoryIcon>()[value] else null

    @TypeConverter
    fun toInt(value: CategoryIcon?): Int? = value?.ordinal
}