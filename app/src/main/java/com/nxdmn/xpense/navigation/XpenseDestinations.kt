package com.nxdmn.xpense.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.navArgument

interface XpenseDestination {
    val icon: ImageVector
    val route: String
}

object ExpenseList : XpenseDestination {
    override val icon = Icons.AutoMirrored.Filled.List
    override val route = "expenseList"
}

object ExpenseDetail : XpenseDestination {
    override val icon = Icons.Filled.Info
    override val route = "expenseDetail"
    const val expenseIdArg = "expenseId"
    val routeWithArgs = "$route?expenseId={$expenseIdArg}"
    val arguments = listOf(navArgument(expenseIdArg){
        nullable = true
    })
}

object Setting : XpenseDestination {
    override val icon = Icons.Filled.Settings
    override val route = "setting"
}

val bottomNavigationScreens = listOf(ExpenseList, Setting)
