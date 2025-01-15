package com.nxdmn.xpense.screens.expenseDetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.nxdmn.xpense.MainApplication
import com.nxdmn.xpense.data.models.CategoryModel
import com.nxdmn.xpense.data.models.ExpenseModel
import com.nxdmn.xpense.data.repositories.CategoryRepository
import com.nxdmn.xpense.data.repositories.ExpenseRepository
import com.nxdmn.xpense.helpers.toLocalDate
import com.nxdmn.xpense.ui.CategoryIcon
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ExpenseDetailUiState(
    val isBusy: Boolean = true,
    val expense: ExpenseModel,
    val categoryList: List<CategoryModel> = emptyList(),
    val isEdit: Boolean = false,
)

class ExpenseDetailViewModel(
    private val expenseRepository: ExpenseRepository,
    private val categoryRepository: CategoryRepository,
    private val expenseId: Long?
) : ViewModel() {
    //TODO: make uistate to store real ui state value like amounr, date, image and use expenseModel only here

    private val _uiState = MutableStateFlow(
        ExpenseDetailUiState(
            expense = ExpenseModel(
                amount = 0.0,
                category = CategoryModel(id = 3, name = "", icon = CategoryIcon.OTHERS)
            )
        )
    )
    val uiState: StateFlow<ExpenseDetailUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            _uiState.update {
                it.copy(categoryList = categoryRepository.getAllCategories())
            }

            val expense: ExpenseModel? =
                if (expenseId != null) expenseRepository.getExpense(expenseId) else null

            if (expense != null)
                _uiState.update { it.copy(expense = expense, isEdit = true) }
            else
                _uiState.update { it.copy(expense = it.expense.copy(category = it.categoryList.first())) }

            _uiState.update { it.copy(isBusy = false) }
        }
    }

    fun saveExpense() {
        val expense = _uiState.value.expense
        viewModelScope.launch {
            if (_uiState.value.isEdit) {
                expenseRepository.updateExpense(expense.copy())
            } else {
                expenseRepository.createExpense(expense.copy())
            }
            expenseRepository.getAllExpenses(refresh = true)
        }
    }

    fun updateAmount(amount: String) {
        _uiState.update {
            it.copy(
                expense = it.expense.copy(
                    amount = amount.toDoubleOrNull() ?: 0.0
                )
            )
        }
    }

    fun updateDate(date: Long) {
        _uiState.update {
            it.copy(
                expense = it.expense.copy(
                    date = date.toLocalDate()
                )
            )
        }
    }

    fun updateCategory(category: CategoryModel) {
        _uiState.update {
            it.copy(
                expense = it.expense.copy(
                    category = category
                )
            )
        }
    }

    fun updateRemarks(remarks: String) {
        _uiState.update {
            it.copy(
                expense = it.expense.copy(
                    remarks = remarks
                )
            )
        }
    }

    fun updateImage(image: String) {
        _uiState.update {
            it.copy(
                expense = it.expense.copy(
                    image = image
                )
            )
        }
    }

    companion object {
        val EXPENSE_ID_KEY = object : CreationExtras.Key<Long?> {}
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app = this[APPLICATION_KEY] as MainApplication
                val expenseRepo = app.expenseRepository
                val categoryRepo = app.categoryRepository

                val expenseId = this[EXPENSE_ID_KEY] as Long?
                ExpenseDetailViewModel(
                    expenseRepository = expenseRepo,
                    categoryRepository = categoryRepo,
                    expenseId = expenseId,
                )
            }
        }
    }
}