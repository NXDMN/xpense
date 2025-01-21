package com.nxdmn.xpense.navigation

import kotlinx.serialization.Serializable

sealed interface Route {
    @Serializable
    data object ExpenseList : Route

    @Serializable
    data class ExpenseDetail(val expenseId: Long? = null) : Route

    @Serializable
    data object Settings : Route

    @Serializable
    data class CategoryDetail(val categoryId: Long? = null) : Route
}

val routes = listOf(
    Route.ExpenseList,
    Route.ExpenseDetail,
    Route.Settings,
    Route.CategoryDetail
)