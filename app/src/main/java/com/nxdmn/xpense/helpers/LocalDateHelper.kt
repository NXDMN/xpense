package com.nxdmn.xpense.helpers

import androidx.room.TypeConverter
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializer
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.io.Console
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZoneOffset


@OptIn(ExperimentalSerializationApi::class)
@Serializer(forClass = LocalDate::class)
class LocalDateSerializer : KSerializer<LocalDate>{
    override fun serialize(encoder: Encoder, value: LocalDate) {
        encoder.encodeString(value.toString())
    }

    override fun deserialize(decoder: Decoder): LocalDate {
        return LocalDate.parse(decoder.decodeString())
    }
}

class LocalDateConverters {
    @TypeConverter
    fun fromString(value: String?): LocalDate? {
        return value?.let { LocalDate.parse(it) }
    }

    @TypeConverter
    fun dateToString(date: LocalDate?): String? {
        return date?.toString()
    }
}

fun LocalDate.toEpochMilli(): Long {
    //val zoneId = ZoneId.systemDefault()
    return this.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli()
}

fun Long.toLocalDate(): LocalDate {
    return Instant.ofEpochMilli(this).atZone(ZoneOffset.UTC).toLocalDate()
}