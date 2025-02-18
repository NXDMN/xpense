package com.nxdmn.xpense.screens.expenseList

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.nxdmn.xpense.R
import com.nxdmn.xpense.data.models.CategoryModel
import com.nxdmn.xpense.data.models.ExpenseModel
import com.nxdmn.xpense.helpers.toEpochMilli
import com.nxdmn.xpense.helpers.toLocalDate
import com.nxdmn.xpense.ui.CategoryIcon
import com.nxdmn.xpense.ui.components.MonthPicker
import com.nxdmn.xpense.ui.components.PieChart
import com.nxdmn.xpense.ui.components.YearPicker
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpenseListScreen(
    expenseListViewModel: ExpenseListViewModel = viewModel(factory = ExpenseListViewModel.Factory),
    onNavigateToDetail: (Long?) -> Unit = {},
) {
    val expenseListUiState by expenseListViewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                ),
                title = {
                    Text("Expenses")
                },
            )
        },
        contentWindowInsets = WindowInsets(0.dp),
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            SecondaryTabRow(selectedTabIndex = expenseListUiState.viewMode.ordinal) {
                ViewMode.entries.forEach { mode ->
                    Tab(
                        text = { Text(mode.title) },
                        selected = mode == expenseListUiState.viewMode,
                        onClick = {
                            expenseListViewModel.updateViewMode(mode)
                        },
                    )
                }
            }

            Column(
                modifier = Modifier
                    .padding(top = 20.dp)
                    .fillMaxSize()
                    .background(color = MaterialTheme.colorScheme.background),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                CalendarLabel(
                    viewMode = expenseListUiState.viewMode,
                    selectedDate = expenseListUiState.selectedDate,
                    onDateSelected = { expenseListViewModel.updateSelectedDate(it) },
                )

                PieChart(
                    charts = expenseListUiState.charts,
                    text = when (expenseListUiState.viewMode) {
                        ViewMode.DAY -> "${expenseListUiState.currencySymbol}${expenseListUiState.dayExpenseAmount}"
                        ViewMode.MONTH -> "${expenseListUiState.currencySymbol}${expenseListUiState.monthExpenseAmount}"
                        ViewMode.YEAR -> "${expenseListUiState.currencySymbol}${expenseListUiState.yearExpenseAmount}"
                    }
                )

                ExpenseListSection(
                    currencySymbol = expenseListUiState.currencySymbol ?: "",
                    expenseGroupedByCategory = expenseListUiState.expensesGroupedByCategory,
                    onNavigateToDetail = onNavigateToDetail
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarLabel(
    viewMode: ViewMode,
    selectedDate: LocalDate,
    onDateSelected: (LocalDate) -> Unit
) {
    val datePickerState =
        rememberDatePickerState(initialSelectedDateMillis = selectedDate.toEpochMilli())

    var openDatePickerDialog by remember { mutableStateOf(false) }

    if (openDatePickerDialog) {
        DatePickerDialog(
            onDismissRequest = { openDatePickerDialog = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let {
                        onDateSelected(it.toLocalDate())
                    }
                    openDatePickerDialog = false
                }) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { openDatePickerDialog = false }) {
                    Text("Cancel")
                }
            }
        ) {
            when (viewMode) {
                ViewMode.DAY -> DatePicker(
                    state = datePickerState,
                    title = null,
                    headline = null,
                    showModeToggle = false
                )

                ViewMode.MONTH -> MonthPicker(state = datePickerState)
                ViewMode.YEAR -> YearPicker(state = datePickerState)
            }
        }
    }

    val shape = RoundedCornerShape(20.dp)

    Row(
        modifier = Modifier
            .background(color = MaterialTheme.colorScheme.surfaceContainer, shape = shape)
            .clip(shape)
            .clickable(
                remember { MutableInteractionSource() },
                indication = null,
                onClick = {
                    openDatePickerDialog = true
                }
            )
            .padding(10.dp),
        horizontalArrangement = Arrangement.spacedBy(5.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = when (viewMode) {
                ViewMode.DAY -> selectedDate.toString()
                ViewMode.MONTH -> selectedDate.format(DateTimeFormatter.ofPattern("MMMM yyyy"))
                ViewMode.YEAR -> selectedDate.year.toString()
            },
            fontSize = 20.sp
        )
        Icon(
            painter = painterResource(R.drawable.baseline_calendar_month_24),
            contentDescription = "Calendar",
        )
    }
}

@Composable
fun ExpenseListSection(
    currencySymbol: String,
    expenseGroupedByCategory: Map<CategoryModel, List<ExpenseModel>>,
    onNavigateToDetail: (Long?) -> Unit = {}
) {
    Column(
        modifier = Modifier
            .padding(horizontal = 10.dp)
            .verticalScroll(rememberScrollState())
            .padding(bottom = 16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        expenseGroupedByCategory.forEach {
            Card {
                Column(
                    modifier = Modifier.padding(10.dp),
                    verticalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    val total by remember(it.value) {
                        mutableDoubleStateOf(it.value.fold(0.0) { acc, element -> acc + element.amount })
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(it.key.name, fontSize = 20.sp)
                        Text(
                            "$currencySymbol ${"%.2f".format(total)}",
                            fontSize = 20.sp
                        )
                    }

                    it.value.forEach { expense ->
                        ExpenseCard(
                            currencySymbol,
                            expense,
                            onNavigateToDetail = onNavigateToDetail
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ExpenseCard(
    currencySymbol: String,
    expense: ExpenseModel,
    onNavigateToDetail: (Long?) -> Unit = {}
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        colors = CardDefaults.cardColors(containerColor = Color(expense.category.color)),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
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
                Text(
                    "$currencySymbol ${"%.2f".format(expense.amount)}",
                    fontSize = 20.sp
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(device = Devices.PIXEL_7_PRO)
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
        Column(
            modifier = Modifier.padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            SecondaryTabRow(selectedTabIndex = 0) {
                Tab(text = { Text("Day") }, selected = true, onClick = { })
                Tab(text = { Text("Month") }, selected = true, onClick = { })
                Tab(text = { Text("Year") }, selected = true, onClick = { })
            }

            var selectedDate by remember { mutableStateOf(LocalDate.now()) }

            CalendarLabel(
                viewMode = ViewMode.DAY,
                selectedDate = selectedDate,
                onDateSelected = { selectedDate = it })

            LazyColumn(
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
                        currencySymbol = "$",
                        it
                    )
                }
            }
        }
    }

}