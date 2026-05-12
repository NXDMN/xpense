package com.nxdmn.xpense.navigation

import android.annotation.SuppressLint
import android.net.Uri
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
import com.nxdmn.xpense.rememberAppBarState
import com.nxdmn.xpense.screens.camera.CameraScreen
import com.nxdmn.xpense.screens.camera.CameraViewModel
import com.nxdmn.xpense.screens.categoryDetail.CategoryDetailScreen
import com.nxdmn.xpense.screens.categoryDetail.CategoryDetailViewModel
import com.nxdmn.xpense.screens.expenseDetail.ExpenseDetailScreen
import com.nxdmn.xpense.screens.expenseDetail.ExpenseDetailViewModel
import com.nxdmn.xpense.screens.expenseList.ExpenseListScreen
import com.nxdmn.xpense.screens.expenseList.ExpenseListViewModel
import com.nxdmn.xpense.screens.setting.SettingsScreen
import com.nxdmn.xpense.screens.setting.SettingsViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

const val RESULT_KEY = "RESULT_KEY"

@SuppressLint("RestrictedApi")
@Composable
fun XpenseNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    appBarState: AppBarState = rememberAppBarState(navController),
) {
//    navController.addOnDestinationChangedListener { controller, _, _ ->
//        val routes = controller
//            .currentBackStack.value
//            .map { it.destination.route }
//            .joinToString(", ")
//
//        Log.d("BackStackLog", "BackStack: $routes")
//    }
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
            onNavigateToCamera = { navController.navigateForResult { navController.navigateToCamera() } },
            onNavigateBack = { navController.popBackStack() }
        )
        cameraScreen(onNavigateBackWithResult = { navController.navigateBackWithResult(it) })
        settingsScreen(onNavigateToCategoryDetail = { categoryId ->
            navController.navigateToCategoryDetail(categoryId)
        })
        categoryDetailScreen(onNavigateBack = { navController.popBackStack() })
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

fun NavHostController.navigateToCamera() =
    this.navigate(Route.Camera) {
        launchSingleTop = true
    }

fun NavHostController.navigateToSetting() =
    this.navigateSingleTopTo(Route.Settings)

fun NavHostController.navigateToCategoryDetail(categoryId: Long? = null) =
    this.navigate(Route.CategoryDetail(categoryId = categoryId)) {
        launchSingleTop = true
    }

fun <T> NavHostController.navigateBackWithResult(data: T?) {
    previousBackStackEntry?.savedStateHandle?.set(RESULT_KEY, data)
    popBackStack()
}

// since this coroutine is across different screens, make sure it is called in CoroutineScope that lives
// longer than composable screen like viewModelScope
suspend fun <T> NavHostController.navigateForResult(navigate: () -> Unit): T? =
    suspendCancellableCoroutine { continuation ->
        val currentNavEntry =
            currentBackStackEntry
                ?: throw IllegalStateException("No current back stack entry found")

        val resultflow =
            currentNavEntry.savedStateHandle.getStateFlow<T?>(RESULT_KEY, initialValue = null)
        val job = CoroutineScope(Dispatchers.Main.immediate).launch {
            resultflow.collect { result ->
                if (result != null) {
                    continuation.resume(result)
                    currentNavEntry.savedStateHandle.remove<T?>(RESULT_KEY)
                    this.cancel()
                }
            }
        }

        // Handle coroutine cancellation
        continuation.invokeOnCancellation {
            job.cancel()
        }

        navigate()
    }

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
    onNavigateToCamera: suspend () -> Uri?,
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
            onNavigateToCamera,
            onNavigateBack
        )
    }
}

fun NavGraphBuilder.cameraScreen(onNavigateBackWithResult: (Uri?) -> Unit) {
    composable<Route.Camera> {
        val vm: CameraViewModel = viewModel()
        CameraScreen(vm, onNavigateBackWithResult)
    }
}

fun NavGraphBuilder.settingsScreen(onNavigateToCategoryDetail: (Long?) -> Unit) {
    composable<Route.Settings> {
        val vm: SettingsViewModel = viewModel(
            factory = SettingsViewModel.Factory
        )
        SettingsScreen(vm, onNavigateToCategoryDetail)
    }
}

fun NavGraphBuilder.categoryDetailScreen(onNavigateBack: () -> Unit) {
    composable<Route.CategoryDetail> { navBackStackEntry ->
        val categoryDetail: Route.CategoryDetail = navBackStackEntry.toRoute()

        val extras = MutableCreationExtras(navBackStackEntry.defaultViewModelCreationExtras).apply {
            set(CategoryDetailViewModel.CATEGORY_ID_KEY, categoryDetail.categoryId)
        }
        val vm: CategoryDetailViewModel = viewModel(
            factory = CategoryDetailViewModel.Factory,
            extras = extras
        )
        CategoryDetailScreen(vm, onNavigateBack)
    }
}