package com.nxdmn.xpense

import androidx.compose.runtime.Composable
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.nxdmn.xpense.data.repositories.ExpenseRepository
import com.nxdmn.xpense.screens.expenseDetail.ExpenseDetailScreen
import com.nxdmn.xpense.screens.expenseDetail.ExpenseDetailViewModel
import com.nxdmn.xpense.screens.expenseList.ExpenseListScreen
import com.nxdmn.xpense.screens.expenseList.ExpenseListViewModel

@Composable
fun XpenseNavHost(navController: NavHostController, expenseRepository: ExpenseRepository) {
    NavHost(navController = navController, startDestination = ExpenseList.route) {
        composable(route = ExpenseList.route) {
            ExpenseListScreen(
                ExpenseListViewModel(expenseRepository),
                onNavigateToDetail = { expenseId ->
                    navController.navigateSingleTopTo("${ExpenseDetail.route}?expenseId=$expenseId")
                }
            )
        }
        composable(
            route = ExpenseDetail.routeWithArgs,
            arguments = ExpenseDetail.arguments
        ) { navBackStackEntry ->
            val expenseId = navBackStackEntry.arguments?.getString(ExpenseDetail.expenseIdArg)
            ExpenseDetailScreen(
                ExpenseDetailViewModel(expenseRepository, expenseId?.toLong()),
                onNavigateToList = {
                    navController.navigateSingleTopTo(ExpenseList.route)
                }
            )
        }
    }
}



fun NavHostController.navigateSingleTopTo(route: String) =
    this.navigate(route) {
        popUpTo(
            this@navigateSingleTopTo.graph.findStartDestination().id
        ) {
            saveState = true
        }
        launchSingleTop = true
        restoreState = true
    }