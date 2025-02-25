package com.nxdmn.xpense.data.dataStores

import android.content.Context
import android.icu.util.Currency
import androidx.datastore.core.CorruptionException
import androidx.datastore.core.DataStore
import androidx.datastore.core.Serializer
import androidx.datastore.dataStore
import com.google.protobuf.InvalidProtocolBufferException
import com.nxdmn.xpense.proto.UserPrefs
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.io.InputStream
import java.io.OutputStream
import java.util.Locale

private val Context.userPrefsDataStore: DataStore<UserPrefs> by dataStore(
    fileName = "userPrefs.pb",
    serializer = UserPrefsSerializer
)

class UserPrefsDataStore(val context: Context) {
    val currencyFlow: Flow<Currency> =
        context.userPrefsDataStore.data.map { Currency.getInstance(it.currencyCode) }

    suspend fun getCurrency(): Currency {
        val currencyCode = context.userPrefsDataStore.data.first().currencyCode
        if (currencyCode.isNullOrEmpty()) return Currency.getInstance(getLocale())
        return Currency.getInstance(currencyCode)
    }

    suspend fun setCurrency(value: Currency) = context.userPrefsDataStore.updateData {
        it.toBuilder().setCurrencyCode(value.currencyCode).build()
    }

    val localeFlow: Flow<Locale> =
        context.userPrefsDataStore.data.map { Locale(it.locale.language, it.locale.country) }

    suspend fun getLocale(): Locale {
        val locale = context.userPrefsDataStore.data.first().locale
        if (locale.language.isNullOrEmpty() && locale.country.isNullOrEmpty()) return Locale.getDefault()
        return Locale(locale.language, locale.country)
    }

    suspend fun setLocale(value: Locale) = context.userPrefsDataStore.updateData {
        val locale =
            it.locale.toBuilder().setLanguage(value.language).setCountry(value.country)
                .build()
        it.toBuilder().setLocale(locale).build()
    }

    val favCategoryIdFlow: Flow<Long> =
        context.userPrefsDataStore.data.map { it.favouriteCategoryId }

    suspend fun getFavCategoryId(): Long {
        return context.userPrefsDataStore.data.first().favouriteCategoryId
    }

    suspend fun setFavCategoryId(value: Long) = context.userPrefsDataStore.updateData {
        it.toBuilder().setFavouriteCategoryId(value).build()
    }
}

object UserPrefsSerializer : Serializer<UserPrefs> {
    override val defaultValue: UserPrefs = UserPrefs.getDefaultInstance()

    override suspend fun readFrom(input: InputStream): UserPrefs {
        try {
            return UserPrefs.parseFrom(input)
        } catch (exception: InvalidProtocolBufferException) {
            throw CorruptionException("Cannot read proto.", exception)
        }
    }

    override suspend fun writeTo(t: UserPrefs, output: OutputStream) {
        t.writeTo(output)
    }
}