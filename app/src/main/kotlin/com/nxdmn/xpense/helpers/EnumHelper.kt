package com.nxdmn.xpense.helpers

import androidx.room.TypeConverter
import com.nxdmn.xpense.ui.CategoryIcon
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerializationException
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlin.enums.EnumEntries

open class EnumSerializer<E : Enum<E>>(
    serialName: String, private val entries: EnumEntries<E>
) : KSerializer<E> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor(serialName, PrimitiveKind.INT)

    override fun serialize(encoder: Encoder, value: E) {
        val index = entries.indexOf(value)
        if (index == -1) {
            throw SerializationException(
                "$value is not a valid enum ${descriptor.serialName}, " +
                        "must be one of ${
                            entries.joinToString(
                                prefix = "[",
                                separator = ", ",
                                postfix = "]"
                            )
                        }"
            )
        }
        encoder.encodeInt(index)
    }


    override fun deserialize(decoder: Decoder): E {
        val index = decoder.decodeInt()
        if (index !in entries.indices) {
            throw SerializationException(
                "$index is not among valid ${descriptor.serialName} enum values, " +
                        "values size is ${entries.size}"
            )
        }
        return entries[index]
    }
}

class CategoryIconConverters {
    @TypeConverter
    fun fromInt(value: Int?): CategoryIcon? =
        if (value != null) enumValues<CategoryIcon>()[value] else null

    @TypeConverter
    fun toInt(value: CategoryIcon?): Int? = value?.ordinal
}