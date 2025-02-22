package com.nxdmn.xpense.screens.expenseDetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.nxdmn.xpense.MainApplication
import com.nxdmn.xpense.data.dataStores.UserPrefsDataStore
import com.nxdmn.xpense.data.models.CategoryModel
import com.nxdmn.xpense.data.models.ExpenseModel
import com.nxdmn.xpense.data.repositories.CategoryRepository
import com.nxdmn.xpense.data.repositories.ExpenseRepository
import com.nxdmn.xpense.helpers.toLocalDate
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate

data class ExpenseDetailUiState(
    val isBusy: Boolean = true,
    val isEdit: Boolean = false,
    val currencyCode: String = "",
    val amount: Double = 0.0,
    val date: LocalDate = LocalDate.now(),
    val category: CategoryModel? = null,
    val remarks: String = "",
    val image: String = "",
    val categoryList: List<CategoryModel> = emptyList(),
)

class ExpenseDetailViewModel(
    private val expenseRepository: ExpenseRepository,
    private val categoryRepository: CategoryRepository,
    dataStore: UserPrefsDataStore,
    private val expenseId: Long?
) : ViewModel() {
    private val _uiState = MutableStateFlow(ExpenseDetailUiState())
    val uiState: StateFlow<ExpenseDetailUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    currencyCode = dataStore.getCurrency().currencyCode,
                    categoryList = categoryRepository.getAllCategories()
                )
            }

            val expense: ExpenseModel? =
                if (expenseId != null) expenseRepository.getExpense(expenseId) else null

            if (expense != null)
                _uiState.update {
                    it.copy(
                        amount = expense.amount,
                        date = expense.date,
                        category = expense.category,
                        remarks = expense.remarks,
                        image = expense.image,
                        isEdit = true
                    )
                }
            else
                _uiState.update { it.copy(category = it.categoryList.first()) }

            _uiState.update { it.copy(isBusy = false) }
        }
    }

    fun validate(): Boolean {
        return _uiState.value.amount > 0.0 && _uiState.value.category != null
    }

    fun saveExpense() {
        val expense = ExpenseModel(
            id = expenseId ?: 0,
            amount = _uiState.value.amount,
            date = _uiState.value.date,
            category = _uiState.value.category!!,
            remarks = _uiState.value.remarks,
            image = _uiState.value.image
        )
        viewModelScope.launch {
            if (_uiState.value.isEdit) {
                expenseRepository.updateExpense(expense)
            } else {
                expenseRepository.createExpense(expense)
            }
        }
    }

    fun deleteExpense() {
        val expense = ExpenseModel(
            id = expenseId ?: 0,
            amount = _uiState.value.amount,
            date = _uiState.value.date,
            category = _uiState.value.category!!,
            remarks = _uiState.value.remarks,
            image = _uiState.value.image
        )
        viewModelScope.launch {
            expenseRepository.deleteExpense(expense)
        }
    }

    fun updateAmount(amount: String) {
        _uiState.update {
            it.copy(amount = amount.toDoubleOrNull() ?: 0.0)
        }
    }

    fun updateDate(date: Long) {
        _uiState.update {
            it.copy(date = date.toLocalDate())
        }
    }

    fun updateCategory(category: CategoryModel) {
        _uiState.update {
            it.copy(category = category)
        }
    }

    fun updateRemarks(remarks: String) {
        _uiState.update {
            it.copy(remarks = remarks)
        }
    }

    fun updateImage(image: String) {
        _uiState.update {
            it.copy(image = image)
        }
    }

    companion object {
        val EXPENSE_ID_KEY = object : CreationExtras.Key<Long?> {}
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app = this[APPLICATION_KEY] as MainApplication
                val expenseRepo = app.expenseRepository
                val categoryRepo = app.categoryRepository
                val ds = app.userPrefsDataStore

                val expenseId = this[EXPENSE_ID_KEY]
                ExpenseDetailViewModel(
                    expenseRepository = expenseRepo,
                    categoryRepository = categoryRepo,
                    dataStore = ds,
                    expenseId = expenseId,
                )
            }
        }
    }
}