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
    expenseRepository: ExpenseRepository,
    categoryRepository: CategoryRepository,
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
        startDestination = ExpenseList.route,
        modifier = modifier
    ) {
        expenseListScreen(
            expenseRepository,
            onNavigateToExpenseDetail = { expenseId ->
                navController.navigateToExpenseDetail(expenseId)
            }
        )
        expenseDetailScreen(
            appBarState,
            expenseRepository,
            categoryRepository,
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
) {
    composable(route = ExpenseList.route) {
        val extras = MutableCreationExtras().apply {
            set(ExpenseListViewModel.EXPENSE_REPOSITORY_KEY, expenseRepository)
        }
        val vm: ExpenseListViewModel = viewModel(
            factory = ExpenseListViewModel.Factory,
            extras = extras
        )
        ExpenseListScreen(
            vm,
            onNavigateToExpenseDetail
        )
    }
}

fun NavGraphBuilder.expenseDetailScreen(
    appBarState: AppBarState,
    expenseRepository: ExpenseRepository,
    categoryRepository: CategoryRepository,
    onNavigateBack: () -> Unit
) {
    composable(
        route = ExpenseDetail.route,
        arguments = ExpenseDetail.arguments
    ) { navBackStackEntry ->
        val expenseId = navBackStackEntry.arguments?.getString(ExpenseDetail.expenseIdArg)
        val extras = MutableCreationExtras().apply {
            set(ExpenseDetailViewModel.EXPENSE_REPOSITORY_KEY, expenseRepository)
            set(ExpenseDetailViewModel.CATEGORY_REPOSITORY_KEY, categoryRepository)
            set(ExpenseDetailViewModel.EXPENSE_ID_KEY, expenseId?.toLong())
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
    composable(route = Setting.route) {
        SettingScreen(SettingViewModel())
    }
}