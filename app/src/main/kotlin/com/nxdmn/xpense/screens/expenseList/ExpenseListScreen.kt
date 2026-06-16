package com.nxdmn.xpense.screens.expenseList

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContent
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
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
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import com.nxdmn.xpense.R
import com.nxdmn.xpense.data.models.CategoryModel
import com.nxdmn.xpense.data.models.ExpenseModel
import com.nxdmn.xpense.helpers.isLight
import com.nxdmn.xpense.helpers.toEpochMilli
import com.nxdmn.xpense.helpers.toLocalDate
import com.nxdmn.xpense.ui.CategoryIcon
import com.nxdmn.xpense.ui.components.ChartModel
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
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                expenseListViewModel.checkToday()
            } else if (event == Lifecycle.Event.ON_PAUSE) {
                expenseListViewModel.recordToday()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    val expenseListUiState by expenseListViewModel.uiState.collectAsState()

    Scaffold(
        Modifier.semantics { contentDescription = "ExpenseList" },
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
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(top = innerPadding.calculateTopPadding())
                .verticalScroll(rememberScrollState())
        ) {
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
                    .let {
                        val configuration = LocalConfiguration.current
                        if (configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                            it.windowInsetsPadding(WindowInsets.safeContent.only(WindowInsetsSides.Horizontal))
                        } else it
                    }
                    .padding(top = 20.dp)
                    .fillMaxSize()
                    .background(color = MaterialTheme.colorScheme.background),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                TextButton(onClick = { throw RuntimeException("Test Crash") }) { Text("Test Crash") }
                CalendarLabel(
                    viewMode = expenseListUiState.viewMode,
                    selectedDate = expenseListUiState.selectedDate,
                    onDateSelected = { expenseListViewModel.updateSelectedDate(it) },
                )

                Box(
                    modifier = Modifier
                        .padding(horizontal = 10.dp)
                        .fillMaxWidth()
                ) {
                    PieChart(
                        modifier = Modifier.align(Alignment.Center),
                        charts = expenseListUiState.charts,
                        text = "${expenseListUiState.currencySymbol ?: ""}${
                            "%.2f".format(expenseListUiState.expenseAmount)
                        }"
                    )

                    if (expenseListUiState.viewMode != ViewMode.DAY)
                        Button(
                            modifier = Modifier.align(Alignment.BottomStart),
                            contentPadding = PaddingValues(horizontal = 10.dp),
                            onClick = { expenseListViewModel.toggleIsGroupByCategory() }
                        ) {
                            Text("By ${if (expenseListUiState.isGroupByCategory) "Category" else "Date"}")
                        }
                }

                ExpenseGroupSection(
                    currencySymbol = expenseListUiState.currencySymbol ?: "",
                    expenseGroupList = expenseListUiState.groupedExpenses,
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

    val openDatePickerDialog = remember { mutableStateOf(false) }

    if (openDatePickerDialog.value) {
        DatePickerDialog(
            onDismissRequest = { openDatePickerDialog.value = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let {
                        onDateSelected(it.toLocalDate())
                    }
                    openDatePickerDialog.value = false
                }) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { openDatePickerDialog.value = false }) {
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
                    openDatePickerDialog.value = true
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
fun ExpenseGroupSection(
    currencySymbol: String,
    expenseGroupList: List<ExpenseGroup>,
    onNavigateToDetail: (Long?) -> Unit = {}
) {
    Column(
        modifier = Modifier.padding(start = 10.dp, end = 10.dp, bottom = 16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        expenseGroupList.forEach {
            Card {
                Column(
                    modifier = Modifier.padding(10.dp),
                    verticalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(it.groupName, fontSize = 20.sp)
                        Text(
                            "$currencySymbol ${"%.2f".format(it.amount)}",
                            fontSize = 20.sp
                        )
                    }

                    it.expenses.forEach { expense ->
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
    val backgroundColor = Color(expense.category.color)
    val contentColor = if (backgroundColor.isLight()) Color.Black else Color.White

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
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
                        .padding(end = 10.dp),
                    tint = contentColor
                )
                Text(
                    "$currencySymbol ${"%.2f".format(expense.amount)}",
                    fontSize = 20.sp,
                    color = contentColor
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
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                ),
                title = {
                    Text("Expenses")
                },
            )
        },
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            val selectedmode = ViewMode.MONTH
            SecondaryTabRow(selectedTabIndex = 2) {
                ViewMode.entries.forEach { mode ->
                    Tab(
                        text = { Text(mode.title) },
                        selected = mode == selectedmode,
                        onClick = {},
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
                    viewMode = ViewMode.MONTH,
                    selectedDate = LocalDate.now(),
                    onDateSelected = { },
                )

                Box(
                    modifier = Modifier
                        .padding(horizontal = 10.dp)
                        .fillMaxWidth()
                ) {
                    PieChart(
                        modifier = Modifier.align(Alignment.Center),
                        charts = listOf(ChartModel(10f, Color.Red)),
                        text = "$10.00"
                    )

                    TextButton(
                        modifier = Modifier.align(Alignment.BottomStart),
                        contentPadding = PaddingValues(horizontal = 10.dp),
                        onClick = {}
                    ) { Text("By Category") }
                }
                val cat =
                    CategoryModel(name = "Food", icon = CategoryIcon.LUNCH, color = 0xFF1AfEC1)
                ExpenseGroupSection(
                    currencySymbol = "$",
                    expenseGroupList = listOf(
                        ExpenseGroup(
                            cat.name,
                            10.0,
                            listOf(ExpenseModel(amount = 10.0, category = cat))
                        )
                    ),
                    onNavigateToDetail = {}
                )
            }
        }
    }
}