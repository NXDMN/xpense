package com.nxdmn.xpense.navigation

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.MutableCreationExtras
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.nxdmn.xpense.AppBarState
import com.nxdmn.xpense.data.repositories.CategoryRepository
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
) {
    navController.addOnDestinationChangedListener { controller, _, _ ->
        val routes = controller
            .currentBackStack.value
            .map { it.destination.route }
            .joinToString(", ")

        Log.d("BackStackLog", "BackStack: $routes")
    }
    NavHost(
        navController = navController,
        startDestination = Route.ExpenseList,
        modifier = modifier
    ) {
        expenseListScreen(
            onNavigateToExpenseDetail = { expenseId ->
                navController.navigateToExpenseDetail(expenseId)
            }
        )
        expenseDetailScreen(
            appBarState,
            onNavigateBack = { navController.popBackStack() }
        )
        settingScreen()
    }
}

fun NavHostController.navigateSingleTopTo(route: Any) =
    this.navigate(route) {
        popUpTo(
            this@navigateSingleTopTo.graph.findStartDestination().id
        ) {
            saveState = true
        }
        launchSingleTop = true
    }

fun NavHostController.navigateToExpenseList() =
    this.navigateSingleTopTo(Route.ExpenseList)

fun NavHostController.navigateToExpenseDetail(expenseId: Long? = null) =
    this.navigateSingleTopTo(Route.ExpenseDetail(expenseId = expenseId))

fun NavHostController.navigateToSetting() =
    this.navigateSingleTopTo(Route.Settings)

fun NavGraphBuilder.expenseListScreen(
    onNavigateToExpenseDetail: (Long?) -> Unit
) {
    composable<Route.ExpenseList> {
        val vm: ExpenseListViewModel = viewModel(
            factory = ExpenseListViewModel.Factory,
        )
        ExpenseListScreen(
            vm,
            onNavigateToExpenseDetail
        )
    }
}

fun NavGraphBuilder.expenseDetailScreen(
    appBarState: AppBarState,
    onNavigateBack: () -> Unit
) {
    composable<Route.ExpenseDetail> { navBackStackEntry ->
        val expenseDetail: Route.ExpenseDetail = navBackStackEntry.toRoute()

        val extras = MutableCreationExtras(navBackStackEntry.defaultViewModelCreationExtras).apply {
            set(ExpenseDetailViewModel.EXPENSE_ID_KEY, expenseDetail.expenseId)
        }
        val vm: ExpenseDetailViewModel = viewModel(
            factory = ExpenseDetailViewModel.Factory,
            extras = extras
        )
        ExpenseDetailScreen(
            appBarState,
            vm,
            onNavigateBack
        )
    }
}

fun NavGraphBuilder.settingScreen() {
    composable<Route.Settings> {
        SettingScreen(SettingViewModel())
    }
}