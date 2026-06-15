package com.nxdmn.xpense.screens.expenseList

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.nxdmn.xpense.MainApplication
import com.nxdmn.xpense.data.dataStores.UserPrefsDataStore
import com.nxdmn.xpense.data.models.CategoryModel
import com.nxdmn.xpense.data.models.ExpenseModel
import com.nxdmn.xpense.data.repositories.ExpenseRepository
import com.nxdmn.xpense.helpers.CurrencyHelper
import com.nxdmn.xpense.ui.components.ChartModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import java.time.LocalDate

// expenses group by date/category
data class ExpenseGroup(
    val groupName: String,
    val amount: Double,
    val expenses: List<ExpenseModel>
)

data class ExpenseListUiState(
    val currencySymbol: String? = null,
    val viewMode: ViewMode = ViewMode.DAY,
    val isGroupByCategory: Boolean = true,
    val groupedExpenses: List<ExpenseGroup> = emptyList(),
    val expenseAmount: Double = 0.0,
    val selectedDate: LocalDate = LocalDate.now(),
    val charts: List<ChartModel> = emptyList()
)

enum class ViewMode(val title: String) {
    DAY("Day"),
    MONTH("Month"),
    YEAR("Year")
}

class ExpenseListViewModel(
    repository: ExpenseRepository,
    dataStore: UserPrefsDataStore
) : ViewModel() {
    private var today: LocalDate = LocalDate.now()

    private val _viewMode = MutableStateFlow(ViewMode.DAY)
    private val _selectedDate = MutableStateFlow(LocalDate.now())
    private val _isGroupByCategory = MutableStateFlow(true)

    val uiState: StateFlow<ExpenseListUiState> =
        combine(
            repository.expenseListFlow,
            _viewMode,
            _selectedDate,
            dataStore.currencyFlow,
            _isGroupByCategory,
        ) { expenses, viewMode, selectedDate, currencyFlow, isGroupByCategory ->
            val filteredExpenses = when (viewMode) {
                ViewMode.DAY -> expenses.filter { e -> e.date == selectedDate }
                ViewMode.MONTH -> expenses.filter { e -> e.date.year == selectedDate.year && e.date.month == selectedDate.month }
                ViewMode.YEAR -> expenses.filter { e -> e.date.year == selectedDate.year }
            }

            val groupedExpenses =
                // in day, can be grouped by category
                if (isGroupByCategory || viewMode == ViewMode.DAY) filteredExpenses.groupBy { it.category }
                    .map { (category, expenses) ->
                        ExpenseGroup(
                            category.name,
                            expenses.sumOf { it.amount },
                            expenses
                        )
                    }
                else when (viewMode) {
                    // in month, can be grouped by day
                    ViewMode.MONTH -> filteredExpenses.groupBy { it.date }
                        .toSortedMap(compareByDescending { it }).map { (key, expenses) ->
                            ExpenseGroup(
                                (key as LocalDate).toString(),
                                expenses.sumOf { it.amount },
                                expenses
                            )
                        }
                    // in year, can be grouped by month
                    ViewMode.YEAR -> filteredExpenses.groupBy { it.date.month }
                        .map { (key, expenses) ->
                            ExpenseGroup(
                                key.name,
                                expenses.sumOf { it.amount },
                                expenses
                            )
                        }
                }

            val chartData = filteredExpenses
                .groupingBy { e -> e.category }
                .fold(0.0) { acc, element -> acc + element.amount }
                .map { entry ->
                    ChartModel(
                        entry.value.toFloat(),
                        Color(entry.key.color)
                    )
                }

            ExpenseListUiState(
                currencySymbol = CurrencyHelper.getSymbol(currencyFlow),
                viewMode = viewMode,
                isGroupByCategory = isGroupByCategory,
                groupedExpenses = groupedExpenses,
                expenseAmount = filteredExpenses.sumOf { e -> e.amount },
                selectedDate = selectedDate,
                charts = chartData
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = ExpenseListUiState()
        )

    fun updateSelectedDate(selectedDate: LocalDate) = _selectedDate.update { selectedDate }

    fun recordToday() {
        today = LocalDate.now()
    }

    fun checkToday() {
        if (today != LocalDate.now()) {
            updateSelectedDate(LocalDate.now())
        }
    }

    fun updateViewMode(viewMode: ViewMode) = _viewMode.update { viewMode }

    fun toggleIsGroupByCategory() = _isGroupByCategory.update { !it }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val repo = (this[APPLICATION_KEY] as MainApplication).expenseRepository
                val ds = (this[APPLICATION_KEY] as MainApplication).userPrefsDataStore
                ExpenseListViewModel(repo, ds)
            }
        }
    }
}