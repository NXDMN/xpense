package com.nxdmn.xpense.helpers

import android.icu.util.Currency
import java.util.Locale

object CurrencyHelper {
    private val currencyLocaleMap: Map<Currency, Locale> = Locale.getAvailableLocales()
        .mapNotNull { locale ->
            Currency.getInstance(locale)?.let { it to locale }
        }
        .sortedBy { it.first.displayName }
        .toMap()

    val currencySymbolMap: Map<Currency, String> =
        currencyLocaleMap.mapValues { it.key.getSymbol(it.value) }

    fun getSymbol(currency: Currency): String? = currencySymbolMap[currency]
}