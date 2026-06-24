package com.nxdmn.xpense.screens.expenseDetail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.serialization.saved
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.google.firebase.Firebase
import com.google.firebase.crashlytics.crashlytics
import com.nxdmn.xpense.MainApplication
import com.nxdmn.xpense.data.converters.LocalDateSerializer
import com.nxdmn.xpense.data.dataStores.UserPrefsDataStore
import com.nxdmn.xpense.data.models.CategoryModel
import com.nxdmn.xpense.data.models.ExpenseModel
import com.nxdmn.xpense.data.repositories.CategoryRepository
import com.nxdmn.xpense.data.repositories.ExpenseRepository
import com.nxdmn.xpense.helpers.toLocalDate
import com.nxdmn.xpense.navigation.ExpenseDetail
import com.nxdmn.xpense.ui.DisplayState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import java.time.LocalDate

private const val MAX_PHOTOS = 4
private const val EXPENSE_STATE_SAVED_STATE_KEY = "ExpenseStateKey"

@Serializable
data class ExpenseState(
    val amount: Double = 0.0,
    @Serializable(with = LocalDateSerializer::class)
    val date: LocalDate = LocalDate.now(),
    val category: CategoryModel? = null,
    val remarks: String = "",
    val images: List<String> = emptyList()
)

data class ExpenseDetailUiState(
    val displayState: DisplayState = DisplayState.Loading,
    val isEdit: Boolean = false,
    val currencyCode: String = "",
    val categoryList: List<CategoryModel> = emptyList(),
    val amountErrorText: String? = null,
    val expense: ExpenseState = ExpenseState(),
) {
    val allowImages: Int get() = MAX_PHOTOS - expense.images.size
}

class ExpenseDetailViewModel(
    private val savedStateHandle: SavedStateHandle,
    private val expenseRepository: ExpenseRepository,
    private val categoryRepository: CategoryRepository,
    dataStore: UserPrefsDataStore,
    private val expenseId: Long?
) : ViewModel() {
    private val _uiState = MutableStateFlow(ExpenseDetailUiState())
    val uiState: StateFlow<ExpenseDetailUiState> = _uiState.asStateFlow()

    private var expenseState by savedStateHandle.saved(key = EXPENSE_STATE_SAVED_STATE_KEY) { ExpenseState() }

    init {
        viewModelScope.launch {
            try {
                val favCatId = dataStore.getFavCategoryId()
                val categoryList = categoryRepository.getAllCategories().toMutableList()
                val favCategory = categoryList.find { it.id == favCatId }
                if (favCategory != null) {
                    categoryList.remove(favCategory)
                    categoryList.add(0, favCategory)
                }
                val currencyCode = dataStore.getCurrency().currencyCode

                val isExpenseStateRestored =
                    savedStateHandle.contains(EXPENSE_STATE_SAVED_STATE_KEY)

                val expense: ExpenseModel? =
                    if (expenseId != null && !isExpenseStateRestored) expenseRepository.getExpense(
                        expenseId
                    ) else null

                if (!isExpenseStateRestored) {
                    expenseState = if (expense != null)
                        ExpenseState(
                            amount = expense.amount,
                            date = expense.date,
                            category = expense.category,
                            remarks = expense.remarks,
                            images = expense.images,
                        )
                    else ExpenseState(category = categoryList.firstOrNull())
                }

                _uiState.update {
                    it.copy(
                        displayState = DisplayState.Content,
                        currencyCode = currencyCode,
                        categoryList = categoryList,
                        isEdit = expenseId != null,
                        expense = expenseState,
                    )
                }
            } catch (ex: Exception) {
                Firebase.crashlytics.recordException(ex)
                _uiState.update {
                    it.copy(displayState = DisplayState.Error)
                }
            }
        }
    }

    private fun validate(): Boolean {
        _uiState.update {
            it.copy(amountErrorText = null)
        }
        var valid = true
        if (_uiState.value.expense.amount <= 0.0) {
            _uiState.update {
                it.copy(amountErrorText = "Please enter amount")
            }
            valid = false
        }
        return valid
    }

    fun saveExpense(): Boolean {
        if (!validate()) return false

        val currentExpense = _uiState.value.expense
        val expense = ExpenseModel(
            id = expenseId ?: 0,
            amount = currentExpense.amount,
            date = currentExpense.date,
            category = currentExpense.category!!,
            remarks = currentExpense.remarks,
            images = currentExpense.images
        )
        viewModelScope.launch {
            if (_uiState.value.isEdit) {
                expenseRepository.updateExpense(expense)
            } else {
                expenseRepository.createExpense(expense)
                // Only increase count when create
                categoryRepository.updateCategory(currentExpense.category.apply { count++ })
            }
        }
        savedStateHandle.remove<ExpenseState>(EXPENSE_STATE_SAVED_STATE_KEY)
        return true
    }

    fun deleteExpense() {
        viewModelScope.launch {
            expenseRepository.deleteExpense(expenseId!!)
        }
        savedStateHandle.remove<ExpenseState>(EXPENSE_STATE_SAVED_STATE_KEY)
    }

    private inline fun updateExpenseState(crossinline block: (ExpenseState) -> ExpenseState) {
        // Update the SavedStateHandle delegate directly
        expenseState = block(expenseState)
        // Push the new value to the UI flow
        _uiState.update { it.copy(expense = expenseState) }
    }

    fun updateAmount(amount: String) = updateExpenseState {
        it.copy(amount = amount.toDoubleOrNull() ?: 0.0)
    }

    fun updateDate(date: Long) = updateExpenseState {
        it.copy(date = date.toLocalDate())
    }

    fun updateCategory(category: CategoryModel) = updateExpenseState {
        it.copy(category = category)
    }

    fun updateRemarks(remarks: String) = updateExpenseState {
        it.copy(remarks = remarks)
    }

    fun addImage(image: String) = updateExpenseState {
        it.copy(images = expenseState.images + image)
    }

    fun removeImages(images: List<String>) = updateExpenseState {
        it.copy(images = expenseState.images - images.toSet())
    }

    companion object {
        fun Factory(navKey: ExpenseDetail? = null): ViewModelProvider.Factory =
            viewModelFactory {
                initializer {
                    val savedStateHandle = createSavedStateHandle()
                    val app = this[APPLICATION_KEY] as MainApplication
                    val expenseRepo = app.expenseRepository
                    val categoryRepo = app.categoryRepository
                    val ds = app.userPrefsDataStore

                    ExpenseDetailViewModel(
                        savedStateHandle = savedStateHandle,
                        expenseRepository = expenseRepo,
                        categoryRepository = categoryRepo,
                        dataStore = ds,
                        expenseId = navKey?.expenseId,
                    )
                }
            }
    }
}