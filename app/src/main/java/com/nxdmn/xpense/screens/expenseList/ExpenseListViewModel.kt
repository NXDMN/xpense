package com.nxdmn.xpense.screens.expenseList

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nxdmn.xpense.data.models.ExpenseModel
import com.nxdmn.xpense.data.repositories.ExpenseRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ExpenseListUiState(
    val expenseList: List<ExpenseModel> = emptyList()
)

class ExpenseListViewModel(private val repository: ExpenseRepository) : ViewModel() {
    private val _uiState = MutableStateFlow(ExpenseListUiState())
    val uiState: StateFlow<ExpenseListUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            _uiState.value = ExpenseListUiState(repository.getAllExpenses())
        }
    }

    fun updateExpenseList(){
        viewModelScope.launch {
            _uiState.update {
                it.copy(expenseList = repository.getAllExpenses())
            }
        }
    }
}