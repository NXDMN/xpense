package com.nxdmn.xpense

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.nxdmn.xpense.data.dataSources.json.CategoryJSONDataSource
import com.nxdmn.xpense.data.dataSources.json.ExpenseJSONDataSource
import com.nxdmn.xpense.data.dataSources.room.AppDatabase
import com.nxdmn.xpense.data.dataSources.room.CategoryRoomDataSource
import com.nxdmn.xpense.data.dataSources.room.ExpenseRoomDataSource
import com.nxdmn.xpense.data.repositories.CategoryRepository
import com.nxdmn.xpense.data.repositories.ExpenseRepository
import com.nxdmn.xpense.navigation.Route
import com.nxdmn.xpense.navigation.XpenseNavHost
import com.nxdmn.xpense.navigation.navigateToExpenseDetail
import com.nxdmn.xpense.navigation.navigateToExpenseList
import com.nxdmn.xpense.navigation.navigateToSetting
import com.nxdmn.xpense.navigation.routes
import com.nxdmn.xpense.ui.theme.XpenseTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            XpenseApp(applicationContext)
        }
    }
}

@Composable
private fun XpenseApp(context: Context) {
    XpenseTheme {
        val navController = rememberNavController()
        val appBarState = rememberAppBarState(navController)

//        val expenseRepository = ExpenseRepository(ExpenseJSONDataSource(context))
//        val categoryRepository = CategoryRepository(CategoryJSONDataSource(context))

        val expenseRepository =
            ExpenseRepository(ExpenseRoomDataSource(AppDatabase.getDatabase(context).expenseDao()))
        val categoryRepository = CategoryRepository(
            CategoryRoomDataSource(
                AppDatabase.getDatabase(context).categoryDao()
            )
        )

        Scaffold(
            modifier = Modifier.imePadding(),
            contentWindowInsets = WindowInsets(0.dp),
            bottomBar = {
                BottomAppBar(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    actions = {
                        IconButton(
                            enabled = appBarState.currentScreen != Route.ExpenseList,
                            onClick = { navController.navigateToExpenseList() }
                        ) {
                            Icon(
                                Icons.AutoMirrored.Filled.List,
                                contentDescription = "Expense List"
                            )
                        }
                        IconButton(
                            enabled = appBarState.currentScreen != Route.Settings,
                            onClick = { navController.navigateToSetting() }
                        ) {
                            Icon(Icons.Filled.Settings, contentDescription = "Settings")
                        }
                    },
                    floatingActionButton = {
                        if (appBarState.currentScreen == Route.ExpenseDetail) {
                            FloatingActionButton(
                                containerColor = MaterialTheme.colorScheme.onPrimaryContainer,
                                onClick = {
                                    appBarState.saveExpenseDetail?.let { it() }
                                }
                            ) {
                                Icon(
                                    painterResource(R.drawable.baseline_save_24),
                                    contentDescription = "Save"
                                )
                            }
                        } else {
                            FloatingActionButton(
                                containerColor = MaterialTheme.colorScheme.onPrimaryContainer,
                                onClick = {
                                    navController.navigateToExpenseDetail()
                                }
                            ) {
                                Icon(Icons.Default.Add, contentDescription = "Add")
                            }
                        }
                    },
                )
            },
        ) { innerPadding ->
            XpenseNavHost(
                navController = navController,
                modifier = Modifier.padding(innerPadding),
                appBarState = appBarState,
                expenseRepository = expenseRepository,
                categoryRepository = categoryRepository
            )
        }
    }

}

@Stable
class AppBarState(private val navController: NavHostController) {
    private val currentDestination: NavDestination?
        @Composable get() = navController.currentBackStackEntryAsState().value?.destination

    val currentScreen
        @Composable get() = routes.find { route ->
            currentDestination?.hierarchy?.any {
                val s = route.toString().substringBefore("@").substringBefore("$")
                it.route?.contains(s) == true
            } == true
        }
            ?: Route.ExpenseList

    var saveExpenseDetail: (() -> Unit)? = null
}

@Composable
fun rememberAppBarState(navController: NavHostController): AppBarState = remember {
    AppBarState(navController)
}