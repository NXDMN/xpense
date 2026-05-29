package com.nxdmn.xpense.navigation

import android.annotation.SuppressLint
import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.result.LocalResultEventBus
import androidx.navigation3.ui.NavDisplay
import com.nxdmn.xpense.AppBarState
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
import kotlinx.serialization.Serializable

@Serializable
data object ExpenseList : NavKey

@Serializable
data class ExpenseDetail(val expenseId: Long? = null) : NavKey

@Serializable
data object Camera : NavKey

@Serializable
data object Settings : NavKey

@Serializable
data class CategoryDetail(val categoryId: Long? = null) : NavKey


@SuppressLint("RestrictedApi")
@Composable
fun XpenseNavHost(
    navigator: Navigator,
    navigationState: NavigationState,
    modifier: Modifier = Modifier,
    appBarState: AppBarState,
) {
    val entryProvider = entryProvider {
        expenseListScreen(
            onNavigateToExpenseDetail = { expenseId ->
                navigator.navigate(ExpenseDetail(expenseId))
            }
        )
        expenseDetailScreen(
            appBarState,
            onNavigateToCamera = {
                navigator.navigate(Camera)
            },
            onNavigateBack = { navigator.goBack() }
        )
        cameraScreen(onNavigateBack = {
            navigator.goBack()
        })
        settingsScreen(onNavigateToCategoryDetail = { categoryId ->
            navigator.navigate(CategoryDetail(categoryId))
        })
        categoryDetailScreen(onNavigateBack = { navigator.goBack() })
    }

    NavDisplay(
        modifier = modifier,
        entries = navigationState.toEntries(entryProvider),
        onBack = { navigator.goBack() }
    )
}

fun EntryProviderScope<NavKey>.expenseListScreen(
    onNavigateToExpenseDetail: (Long?) -> Unit
) {
    entry<ExpenseList> {
        val vm: ExpenseListViewModel = viewModel(
            factory = ExpenseListViewModel.Factory,
        )
        ExpenseListScreen(
            vm,
            onNavigateToExpenseDetail
        )
    }
}

fun EntryProviderScope<NavKey>.expenseDetailScreen(
    appBarState: AppBarState,
    onNavigateToCamera: () -> Unit,
    onNavigateBack: () -> Unit
) {
    entry<ExpenseDetail> { expenseDetail ->
        val vm: ExpenseDetailViewModel = viewModel(
            factory = ExpenseDetailViewModel.Factory(expenseDetail),
        )
        val resultBus = LocalResultEventBus.current
        val resultState = resultBus.conflateAsState<Uri?>(null)

        LaunchedEffect(resultBus) {
            snapshotFlow { resultState }.collect { uriState ->
                val uri = uriState.value;
                if (uri != null) {
                    vm.addImage(uri.toString())
                    resultBus.sendResult(null)
                }
            }
        }


        ExpenseDetailScreen(
            appBarState,
            vm,
            onNavigateToCamera,
            onNavigateBack
        )
    }
}


fun EntryProviderScope<NavKey>.cameraScreen(onNavigateBack: () -> Unit) {
    entry<Camera> {
        val vm: CameraViewModel = viewModel()
        val resultBus = LocalResultEventBus.current
        CameraScreen(vm, onNavigateBackWithResult = {
            resultBus.sendResult(result = it)
            onNavigateBack()
        })
    }
}

fun EntryProviderScope<NavKey>.settingsScreen(onNavigateToCategoryDetail: (Long?) -> Unit) {
    entry<Settings> {
        val vm: SettingsViewModel = viewModel(
            factory = SettingsViewModel.Factory
        )
        SettingsScreen(vm, onNavigateToCategoryDetail)
    }
}

fun EntryProviderScope<NavKey>.categoryDetailScreen(onNavigateBack: () -> Unit) {
    entry<CategoryDetail> { categoryDetail ->
        val vm: CategoryDetailViewModel = viewModel(
            factory = CategoryDetailViewModel.Factory(categoryDetail)
        )
        CategoryDetailScreen(vm, onNavigateBack)
    }
}