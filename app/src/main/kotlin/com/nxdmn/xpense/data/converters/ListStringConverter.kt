package com.nxdmn.xpense.data.converters

import androidx.room.TypeConverter
import kotlinx.serialization.json.Json

class ListStringConverters {
    @TypeConverter
    fun fromListString(value: List<String>?): String? =
        value?.let { Json.encodeToString(it) }

    @TypeConverter
    fun toListString(value: String?): List<String>? =
        value?.let { Json.decodeFromString<List<String>>(value) }
}