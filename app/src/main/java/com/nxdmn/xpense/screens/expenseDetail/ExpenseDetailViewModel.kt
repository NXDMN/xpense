package com.nxdmn.xpense.screens.expenseDetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nxdmn.xpense.data.models.ExpenseModel
import com.nxdmn.xpense.data.repositories.ExpenseRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ExpenseDetailUiState(
    val expense: ExpenseModel = ExpenseModel(),
    val isEdit: Boolean = false,
)

class ExpenseDetailViewModel(private val repository: ExpenseRepository, private val expenseId: Long?) : ViewModel() {
    private val _uiState = MutableStateFlow(ExpenseDetailUiState())
    val uiState: StateFlow<ExpenseDetailUiState> = _uiState.asStateFlow()

    init {
        val expense: ExpenseModel? = if(expenseId != null) repository.getExpense(expenseId) else null

        if(expense != null)
            _uiState.value = ExpenseDetailUiState(expense, true)
        else
            _uiState.value = ExpenseDetailUiState()
    }

    fun saveExpense(expense: ExpenseModel){
        viewModelScope.launch {
            if (_uiState.value.isEdit) {
                repository.updateExpense(expense.copy())
            } else {
                repository.createExpense(expense.copy())
            }
            repository.getAllExpenses(refresh = true)
        }
    }
}