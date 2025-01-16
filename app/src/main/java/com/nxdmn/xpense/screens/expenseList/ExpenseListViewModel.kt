package com.nxdmn.xpense.screens.expenseList

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.nxdmn.xpense.MainApplication
import com.nxdmn.xpense.data.models.ExpenseModel
import com.nxdmn.xpense.data.repositories.ExpenseRepository
import com.nxdmn.xpense.ui.components.ChartModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import java.time.LocalDate

data class ExpenseListUiState(
    val viewMode: ViewMode = ViewMode.DAY,
    val totalExpenseList: List<ExpenseModel> = emptyList(),
    val dayExpenseList: List<ExpenseModel> = emptyList(),
    val dayExpenseAmount: Double = 0.0,
    val monthExpenseList: List<ExpenseModel> = emptyList(),
    val monthExpenseAmount: Double = 0.0,
    val yearExpenseList: List<ExpenseModel> = emptyList(),
    val yearExpenseAmount: Double = 0.0,
    val selectedDate: LocalDate = LocalDate.now(),
    val charts: List<ChartModel> = emptyList()
)

enum class ViewMode(val title: String) {
    DAY("Day"),
    MONTH("Month"),
    YEAR("Year")
}

class ExpenseListViewModel(private val repository: ExpenseRepository) : ViewModel() {
    private val _viewMode = MutableStateFlow(ViewMode.DAY)
    private val _selectedDate = MutableStateFlow(LocalDate.now())

    val expenseGroupedByCategory
        get() = when (uiState.value.viewMode) {
            ViewMode.DAY -> uiState.value.dayExpenseList
            ViewMode.MONTH -> uiState.value.monthExpenseList
            ViewMode.YEAR -> uiState.value.yearExpenseList
        }.groupBy { it.category }

    val uiState: StateFlow<ExpenseListUiState> =
        combine(
            repository.expenseListFlow,
            _viewMode,
            _selectedDate
        ) { expenses, viewMode, selectedDate ->
            val dayExpenseList = expenses.filter { e -> e.date == selectedDate }
            val monthExpenseList =
                expenses.filter { e -> e.date.year == selectedDate.year && e.date.month == selectedDate.month }
            val yearExpenseList = expenses.filter { e -> e.date.year == selectedDate.year }

            ExpenseListUiState(
                viewMode = viewMode,
                totalExpenseList = expenses,
                dayExpenseList = dayExpenseList,
                dayExpenseAmount = dayExpenseList.sumOf { e -> e.amount },
                monthExpenseList = monthExpenseList,
                monthExpenseAmount = monthExpenseList.sumOf { e -> e.amount },
                yearExpenseList = yearExpenseList,
                yearExpenseAmount = yearExpenseList.sumOf { e -> e.amount },
                selectedDate = selectedDate,
                charts = updateChart(
                    when (_viewMode.value) {
                        ViewMode.DAY -> dayExpenseList
                        ViewMode.MONTH -> monthExpenseList
                        ViewMode.YEAR -> yearExpenseList
                    }
                )
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = ExpenseListUiState()
        )

    fun updateSelectedDate(selectedDate: LocalDate) {
        _selectedDate.update { selectedDate }
    }

    fun updateViewMode(viewMode: ViewMode) {
        _viewMode.update { viewMode }
    }

    private fun updateChart(expenseList: List<ExpenseModel>): List<ChartModel> =
        expenseList
            .groupingBy { e -> e.category }
            .fold(0.0) { acc, element -> acc + element.amount }
            .map { entry ->
                ChartModel(
                    entry.value.toFloat(),
                    Color(entry.key.color)
                )
            }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val repo = (this[APPLICATION_KEY] as MainApplication).expenseRepository
                ExpenseListViewModel(repo)
            }
        }
    }
}