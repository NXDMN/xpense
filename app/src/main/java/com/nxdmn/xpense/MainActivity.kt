package com.nxdmn.xpense

import android.content.Context
import android.os.Bundle
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController
import com.nxdmn.xpense.data.dataSources.room.ExpenseRoomDataSource
import com.nxdmn.xpense.data.repositories.ExpenseRepository
import com.nxdmn.xpense.navigation.ExpenseList
import com.nxdmn.xpense.navigation.Setting
import com.nxdmn.xpense.navigation.XpenseNavHost
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
                        FloatingActionButton(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.inversePrimary,
                            onClick = {
                                navController.navigateToExpenseDetail()
                            }
                        ) {
                            Icon(Icons.Default.Add, contentDescription = "Add")
                        }
                    },
                )
            },
        ) { innerPadding ->
            XpenseNavHost(navController = navController, expenseRepository = expenseRepository, modifier = Modifier.padding(innerPadding))
        }
    }

}