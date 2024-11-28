package com.nxdmn.xpense.screens.expenseList

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nxdmn.xpense.data.models.ExpenseModel
import com.nxdmn.xpense.data.repositories.ExpenseRepository
import com.nxdmn.xpense.ui.components.ChartModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate

data class ExpenseListUiState(
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

class ExpenseListViewModel(private val repository: ExpenseRepository) : ViewModel() {
    private val _uiState = MutableStateFlow(ExpenseListUiState())
    val uiState: StateFlow<ExpenseListUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            _uiState.value = ExpenseListUiState(totalExpenseList = repository.getAllExpenses())
            _uiState.update {
                it.copy(
                    dayExpenseList = it.totalExpenseList.filter { e -> e.date == it.selectedDate },
                    monthExpenseList = it.totalExpenseList.filter { e -> e.date.month == it.selectedDate.month },
                    yearExpenseList = it.totalExpenseList.filter { e -> e.date.year == it.selectedDate.year },
                )
            }
            _uiState.update {
                it.copy(
                    dayExpenseAmount = it.dayExpenseList.sumOf { e -> e.amount },
                    monthExpenseAmount = it.monthExpenseList.sumOf { e -> e.amount },
                    yearExpenseAmount = it.yearExpenseList.sumOf { e -> e.amount },
                    charts = updateChart()
                )
            }
        }
    }

    fun updateSelectedDate(selectedDate: LocalDate) {
        _uiState.update {
            it.copy(
                selectedDate = selectedDate,
                dayExpenseList = it.totalExpenseList.filter { e -> e.date == selectedDate },
                monthExpenseList = it.totalExpenseList.filter { e -> e.date.month == selectedDate.month },
                yearExpenseList = it.totalExpenseList.filter { e -> e.date.year == selectedDate.year },
            )
        }
        _uiState.update {
            it.copy(
                dayExpenseAmount = it.dayExpenseList.sumOf { e -> e.amount },
                monthExpenseAmount = it.monthExpenseList.sumOf { e -> e.amount },
                yearExpenseAmount = it.yearExpenseList.sumOf { e -> e.amount },
                charts = updateChart()
            )
        }
    }


    private fun updateChart(): List<ChartModel> =
        _uiState.value.dayExpenseList.groupingBy { it.category }
            .fold(0.0) { acc, element -> acc + element.amount }
            .map { entry ->
                ChartModel(
                    entry.value.toFloat(),
                    Color(entry.key.color)
                )
            }
}