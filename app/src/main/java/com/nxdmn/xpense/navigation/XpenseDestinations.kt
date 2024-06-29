package com.nxdmn.xpense.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.navArgument

interface XpenseDestination {
    val icon: ImageVector
    val routePrefix: String
    val route: String
}

object ExpenseList : XpenseDestination {
    override val icon = Icons.AutoMirrored.Filled.List
    override val routePrefix = "expenseList"
    override val route = "expenseList"
}

object ExpenseDetail : XpenseDestination {
    override val icon = Icons.Filled.Info
    override val routePrefix = "expenseDetail"
    const val expenseIdArg = "expenseId"
    override val route = "$routePrefix?expenseId={$expenseIdArg}"
    val arguments = listOf(navArgument(expenseIdArg){
        nullable = true
    })
}

object Setting : XpenseDestination {
    override val icon = Icons.Filled.Settings
    override val routePrefix = "setting"
    override val route = "setting"
}

val bottomNavigationScreens = listOf(ExpenseList, Setting, ExpenseDetail)