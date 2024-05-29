package com.nxdmn.xpense.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
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
        expenseListScreen(
            expenseRepository,
            onNavigateToExpenseDetail = { expenseId ->
                navController.navigateToExpenseDetail(expenseId)
            }
        )
        expenseDetailScreen(
            expenseRepository,
            onNavigateToExpenseList = { navController.navigateToExpenseList() }
        )
    }
}

fun NavHostController.navigateSingleTopTo(route: String) =
    this.navigate(route) {
//        popUpTo(
//            this@navigateSingleTopTo.graph.findStartDestination().id
//        ) {
//            saveState = true
//        }
        launchSingleTop = true
        restoreState = true
    }

fun NavHostController.navigateToExpenseList() =
    this.navigateSingleTopTo(ExpenseList.route)

fun NavHostController.navigateToExpenseDetail(expenseId: Long?) =
    this.navigateSingleTopTo("${ExpenseDetail.route}?expenseId=$expenseId")

fun NavGraphBuilder.expenseListScreen(
    expenseRepository: ExpenseRepository,
    onNavigateToExpenseDetail: (Long?) -> Unit
){
    composable(route = ExpenseList.route) {
        ExpenseListScreen(
            ExpenseListViewModel(expenseRepository),
            onNavigateToExpenseDetail
        )
    }
}

fun NavGraphBuilder.expenseDetailScreen(
    expenseRepository: ExpenseRepository,
    onNavigateToExpenseList: () -> Unit
){
    composable(
        route = ExpenseDetail.routeWithArgs,
        arguments = ExpenseDetail.arguments
    ) { navBackStackEntry ->
        val expenseId = navBackStackEntry.arguments?.getString(ExpenseDetail.expenseIdArg)
        ExpenseDetailScreen(
            ExpenseDetailViewModel(expenseRepository, expenseId?.toLong()),
            onNavigateToExpenseList
        )
    }
}