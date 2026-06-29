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
import com.nxdmn.xpense.domain.ExpenseGroup
import com.nxdmn.xpense.domain.GetGroupedExpensesUseCase
import com.nxdmn.xpense.domain.ViewMode
import com.nxdmn.xpense.helpers.CurrencyHelper
import com.nxdmn.xpense.ui.components.ChartModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import java.time.LocalDate

data class ExpenseListUiState(
    val currencySymbol: String? = null,
    val viewMode: ViewMode = ViewMode.DAY,
    val isGroupByCategory: Boolean = true,
    val groupedExpenses: List<ExpenseGroup> = emptyList(),
    val expenseAmount: Double = 0.0,
    val selectedDate: LocalDate = LocalDate.now(),
    val charts: List<ChartModel> = emptyList()
)

class ExpenseListViewModel(
    private val getGroupedExpensesUseCase: GetGroupedExpensesUseCase,
    dataStore: UserPrefsDataStore
) : ViewModel() {
    private var today: LocalDate = LocalDate.now()

    private val _viewMode = MutableStateFlow(ViewMode.DAY)
    private val _selectedDate = MutableStateFlow(LocalDate.now())
    private val _isGroupByCategory = MutableStateFlow(true)

    @OptIn(ExperimentalCoroutinesApi::class)
    val uiState: StateFlow<ExpenseListUiState> =
        combine(
            _viewMode,
            _selectedDate,
            _isGroupByCategory,
            dataStore.currencyFlow,
        ) { viewMode, selectedDate, isGroupByCategory, currencyFlow ->
            Triple(viewMode, selectedDate, isGroupByCategory) to currencyFlow
        }.flatMapLatest { (params, currencyFlow) ->
            val (viewMode, selectedDate, isGroupByCategory) = params
            getGroupedExpensesUseCase(viewMode, selectedDate, isGroupByCategory).map { result ->
                val chartData = result.filteredExpenses
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
                    groupedExpenses = result.groupedExpenses,
                    expenseAmount = result.totalAmount,
                    selectedDate = selectedDate,
                    charts = chartData
                )
            }
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
                val app = this[APPLICATION_KEY] as MainApplication
                val repo = app.expenseRepository
                val ds = app.userPrefsDataStore
                val useCase = GetGroupedExpensesUseCase(repo)
                ExpenseListViewModel(useCase, ds)
            }
        }
    }
}