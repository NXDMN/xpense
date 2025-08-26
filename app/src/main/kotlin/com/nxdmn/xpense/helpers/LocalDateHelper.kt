package com.nxdmn.xpense.helpers

import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset


fun LocalDate.toEpochMilli(): Long {
    //val zoneId = ZoneId.systemDefault()
    return this.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli()
}

fun Long.toLocalDate(): LocalDate {
    return Instant.ofEpochMilli(this).atZone(ZoneOffset.UTC).toLocalDate()
}