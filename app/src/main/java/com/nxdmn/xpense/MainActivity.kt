package com.nxdmn.xpense

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.nxdmn.xpense.screens.expenseList.ExpenseListScreen
import com.nxdmn.xpense.screens.expenseList.ExpenseListViewModel
import com.nxdmn.xpense.data.dataSources.room.ExpenseRoomDataSource
import com.nxdmn.xpense.data.repositories.ExpenseRepository
import com.nxdmn.xpense.screens.expenseDetail.ExpenseDetailScreen
import com.nxdmn.xpense.screens.expenseDetail.ExpenseDetailViewModel
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

        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            val expenseRepository = ExpenseRepository(ExpenseRoomDataSource(context))

            XpenseNavHost(navController = navController, expenseRepository = expenseRepository)
        }
    }

}