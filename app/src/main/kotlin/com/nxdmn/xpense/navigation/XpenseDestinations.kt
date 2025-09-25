package com.nxdmn.xpense.navigation

import kotlinx.serialization.Serializable

sealed class Route {
    @Serializable
    data object ExpenseList : Route()

    @Serializable
    data class ExpenseDetail(val expenseId: Long? = null) : Route()

    @Serializable
    data object Camera : Route()

    @Serializable
    data object Settings : Route()

    @Serializable
    data class CategoryDetail(val categoryId: Long? = null) : Route()
}

val routes: List<Route> = listOf(
    Route.ExpenseList,
    Route.ExpenseDetail(),
    Route.Camera,
    Route.Settings,
    Route.CategoryDetail()
)