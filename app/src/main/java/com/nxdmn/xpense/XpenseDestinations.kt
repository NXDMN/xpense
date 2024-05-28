package com.nxdmn.xpense

import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.nxdmn.xpense.screens.expenseDetail.ExpenseDetailScreen
import com.nxdmn.xpense.screens.expenseDetail.ExpenseDetailViewModel
import com.nxdmn.xpense.screens.expenseList.ExpenseListScreen
import com.nxdmn.xpense.screens.expenseList.ExpenseListViewModel

interface XpenseDestination {
    //val icon: ImageVector
    val route: String
}

object ExpenseList : XpenseDestination {
    //override val icon = Icons.Filled.PieChart
    override val route = "expenseList"
}

object ExpenseDetail : XpenseDestination {
    //override val icon = Icons.Filled.AttachMoney
    override val route = "expenseDetail"
    const val expenseIdArg = "expenseId"
    val routeWithArgs = "${route}?expenseId={${expenseIdArg}}"
    val arguments = listOf(navArgument(expenseIdArg){
        nullable = true
    })
}

//object SingleAccount : XpenseDestination {
//    override val route = "single_account"
//    override val screen: @Composable () -> Unit = { SingleAccountScreen() }
//    const val accountTypeArg = "account_type"
//}
//
//val tabRowScreens = listOf(ExpenseList, ExpenseDetail, Bills)
