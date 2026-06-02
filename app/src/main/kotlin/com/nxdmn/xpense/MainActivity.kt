package com.nxdmn.xpense

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.nxdmn.xpense.navigation.Camera
import com.nxdmn.xpense.navigation.CategoryDetail
import com.nxdmn.xpense.navigation.ExpenseDetail
import com.nxdmn.xpense.navigation.ExpenseList
import com.nxdmn.xpense.navigation.NavigationState
import com.nxdmn.xpense.navigation.Navigator
import com.nxdmn.xpense.navigation.Settings
import com.nxdmn.xpense.navigation.XpenseNavHost
import com.nxdmn.xpense.navigation.rememberNavigationState
import com.nxdmn.xpense.ui.theme.XpenseTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            XpenseApp()
        }
    }
}

@Composable
fun XpenseApp(
    navigationState: NavigationState = rememberNavigationState(
        startRoute = ExpenseList,
        topLevelRoutes = setOf(ExpenseList, Settings),
    ),
    navigator: Navigator = remember { Navigator(navigationState) }
) {
    XpenseTheme {
        val currentScreen = navigationState.backStacks[navigationState.topLevelRoute]?.last()

        val appBarState = rememberAppBarState()

        Scaffold(
            modifier = Modifier.imePadding(),
            // Since every screen also use Scaffold, so no need set inset here
            contentWindowInsets = WindowInsets(0.dp, 0.dp, 0.dp, 0.dp),
            bottomBar = {
                if (currentScreen != Camera && currentScreen !is CategoryDetail)
                    BottomAppBar(
                        actions = {
                            IconButton(
                                enabled = currentScreen != ExpenseList,
                                onClick = { navigator.navigate(ExpenseList) }
                            ) {
                                Icon(
                                    painterResource(R.drawable.baseline_list_24),
                                    contentDescription = "Expense List",
                                )
                            }
                            IconButton(
                                enabled = currentScreen != Settings,
                                onClick = { navigator.navigate(Settings) }
                            ) {
                                Icon(
                                    painterResource(R.drawable.baseline_settings_24),
                                    contentDescription = "Settings"
                                )
                            }
                        },
                        floatingActionButton = {
                            if (currentScreen is ExpenseDetail) {
                                FloatingActionButton(
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
                                    onClick = {
                                        navigator.navigate(ExpenseDetail())
                                    }
                                ) {
                                    Icon(
                                        painterResource(R.drawable.outline_add_24),
                                        contentDescription = "Add"
                                    )
                                }
                            }
                        },
                    )
            },
        ) { innerPadding ->
            XpenseNavHost(
                navigator = navigator,
                navigationState = navigationState,
                modifier = Modifier.padding(innerPadding),
                appBarState = appBarState,
            )
        }
    }

}

@Stable
class AppBarState {
    var saveExpenseDetail: (() -> Unit)? = null
}

@Composable
fun rememberAppBarState(): AppBarState = remember {
    AppBarState()
}