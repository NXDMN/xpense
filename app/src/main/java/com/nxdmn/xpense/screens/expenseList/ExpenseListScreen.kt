package com.nxdmn.xpense.screens.expenseList

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.nxdmn.xpense.data.models.CategoryModel
import com.nxdmn.xpense.data.models.ExpenseModel
import com.nxdmn.xpense.ui.CategoryIcon


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpenseListScreen(
    expenseListViewModel: ExpenseListViewModel = viewModel(),
    onNavigateToDetail: (Long?) -> Unit = {},
) {
    val expenseListUiState by expenseListViewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                title = {
                    Text("Expenses")
                },
            )
        },
    ) { innerPadding ->
        LazyColumn(
            Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            contentPadding = PaddingValues(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(expenseListUiState.expenseList, key = { expense -> expense.id }) {
                ExpenseCard(it, onNavigateToDetail = onNavigateToDetail)
            }
        }
    }
}

@Composable
fun ExpenseCard(expense: ExpenseModel, onNavigateToDetail: (Long?) -> Unit = {}) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 6.dp
        ),
        onClick = {
            onNavigateToDetail(expense.id)
        }
    ) {
        Column(Modifier.padding(horizontal = 20.dp, vertical = 10.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    painterResource(id = expense.category.icon.resId),
                    contentDescription = expense.category.name,
                    modifier = Modifier
                        .height(40.dp)
                        .padding(end = 10.dp)
                )
                Text("$ ${"%.2f".format(expense.amount)}", fontSize = 20.sp)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Preview(device = Devices.PIXEL_7_PRO, heightDp = 2000)
@Composable
fun TestPreview() {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                title = {
                    Text("Expenses")
                },
            )
        },
    ) { innerPadding ->
        LazyColumn(
            Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            contentPadding = PaddingValues(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(listOf(
                ExpenseModel(
                    amount = 10.0,
                    category = CategoryModel(name = "Food", icon = CategoryIcon.FASTFOOD)
                )
            ), key = { expense -> expense.id }) {
                ExpenseCard(
                    it
                )
            }
        }
    }

}