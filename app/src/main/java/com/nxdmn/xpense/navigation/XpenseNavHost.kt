package com.nxdmn.xpense.navigation

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.nxdmn.xpense.AppBarState
import com.nxdmn.xpense.data.repositories.ExpenseRepository
import com.nxdmn.xpense.screens.expenseDetail.ExpenseDetailScreen
import com.nxdmn.xpense.screens.expenseDetail.ExpenseDetailViewModel
import com.nxdmn.xpense.screens.expenseList.ExpenseListScreen
import com.nxdmn.xpense.screens.expenseList.ExpenseListViewModel
import com.nxdmn.xpense.screens.setting.SettingScreen
import com.nxdmn.xpense.screens.setting.SettingViewModel

@SuppressLint("RestrictedApi")
@Composable
fun XpenseNavHost(
    navController: NavHostController,
    modifier: Modifier,
    appBarState: AppBarState,
    expenseRepository: ExpenseRepository,
) {
    navController.addOnDestinationChangedListener { controller, _, _ ->
        val routes = controller
            .currentBackStack.value
            .map { it.destination.route }
            .joinToString(", ")

        Log.d("BackStackLog", "BackStack: $routes")
    }
    NavHost(navController = navController, startDestination = ExpenseList.route, modifier = modifier) {
        expenseListScreen(
            expenseRepository,
            onNavigateToExpenseDetail = { expenseId ->
                navController.navigateToExpenseDetail(expenseId)
            }
        )
        expenseDetailScreen(
            appBarState,
            expenseRepository,
            onNavigateToExpenseList = { navController.navigateToExpenseList() },
            onNavigateBack = { navController.popBackStack() }
        )
        settingScreen()
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
    }

fun NavHostController.navigateToExpenseList() =
    this.navigateSingleTopTo(ExpenseList.route)

fun NavHostController.navigateToExpenseDetail(expenseId: Long? = null) =
    this.navigateSingleTopTo("${ExpenseDetail.routePrefix}?expenseId=$expenseId")

fun NavHostController.navigateToSetting() =
    this.navigateSingleTopTo(Setting.route)

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
    appBarState: AppBarState,
    expenseRepository: ExpenseRepository,
    onNavigateToExpenseList: () -> Unit,
    onNavigateBack: () -> Unit
){
    composable(
        route = ExpenseDetail.route,
        arguments = ExpenseDetail.arguments
    ) { navBackStackEntry ->
        val expenseId = navBackStackEntry.arguments?.getString(ExpenseDetail.expenseIdArg)
        ExpenseDetailScreen(
            appBarState,
            ExpenseDetailViewModel(expenseRepository, expenseId?.toLong()),
            onNavigateToExpenseList,
            onNavigateBack
        )
    }
}

fun NavGraphBuilder.settingScreen(){
    composable(route = Setting.route) {
        SettingScreen(SettingViewModel())
    }
}