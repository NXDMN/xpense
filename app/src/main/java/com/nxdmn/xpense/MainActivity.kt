package com.nxdmn.xpense

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.nxdmn.xpense.data.dataSources.room.ExpenseRoomDataSource
import com.nxdmn.xpense.data.repositories.ExpenseRepository
import com.nxdmn.xpense.navigation.ExpenseDetail
import com.nxdmn.xpense.navigation.ExpenseList
import com.nxdmn.xpense.navigation.Setting
import com.nxdmn.xpense.navigation.XpenseNavHost
import com.nxdmn.xpense.navigation.bottomNavigationScreens
import com.nxdmn.xpense.navigation.navigateToExpenseDetail
import com.nxdmn.xpense.navigation.navigateToExpenseList
import com.nxdmn.xpense.navigation.navigateToSetting
import com.nxdmn.xpense.ui.theme.XpenseTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent{
            XpenseApp(applicationContext)
        }
    }
}

@Composable
private fun XpenseApp(context: Context) {
    XpenseTheme {
        val navController = rememberNavController()
        val appBarState = rememberAppBarState(navController)

        val expenseRepository = ExpenseRepository(ExpenseRoomDataSource(context))

        Scaffold(
            bottomBar = {
                BottomAppBar(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.primary,
                    actions = {
                        IconButton(onClick = { navController.navigateToExpenseList() }) {
                            Icon(ExpenseList.icon, contentDescription = "Expense List")
                        }
                        IconButton(onClick = { navController.navigateToSetting() }) {
                            Icon(Setting.icon, contentDescription = "Setting")
                        }
                    },
                    floatingActionButton = {
                        if(appBarState.currentScreen == ExpenseDetail){
                            FloatingActionButton(
                                containerColor = MaterialTheme.colorScheme.primary,
                                contentColor = MaterialTheme.colorScheme.inversePrimary,
                                onClick = {
                                    appBarState.saveExpenseDetail?.let { it() }
                                }
                            ) {
                                Icon(painterResource(R.drawable.baseline_save_24), contentDescription = "Save")
                            }
                        }
                        else{
                            FloatingActionButton(
                                containerColor = MaterialTheme.colorScheme.primary,
                                contentColor = MaterialTheme.colorScheme.inversePrimary,
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
            )
        }
    }

}

@Stable
class AppBarState(private val navController: NavHostController){

    val currentDestination: NavDestination?
        @Composable get() = navController.currentBackStackEntryAsState().value?.destination

    val currentScreen
        @Composable get() = bottomNavigationScreens.find { it.route == currentDestination?.route } ?: ExpenseList

    var saveExpenseDetail: (() -> Unit)? = null
}

@Composable
fun rememberAppBarState(navController: NavHostController): AppBarState = remember {
    AppBarState(navController)
}