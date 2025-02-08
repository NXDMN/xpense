package com.nxdmn.xpense.screens.setting

import android.icu.util.Currency
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.nxdmn.xpense.MainApplication
import com.nxdmn.xpense.data.models.CategoryModel
import com.nxdmn.xpense.data.repositories.CategoryRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Locale

data class SettingsUiState(
    val categoryList: List<CategoryModel> = emptyList(),
    val currencySymbolMap: Map<Currency, String> = emptyMap(),
    val currency: Currency? = null
)

class SettingsViewModel(private val repository: CategoryRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        _uiState.update {
            it.copy(currencySymbolMap = getAllCurrencies())
        }
    }

    suspend fun refreshCategoryList() {
        _uiState.update {
            it.copy(categoryList = repository.getAllCategories(true))
        }
    }

    fun deleteCategory(category: CategoryModel) {
        viewModelScope.launch {
            repository.deleteCategory(category)
            refreshCategoryList()
        }
    }

    private fun getAllCurrencies(): Map<Currency, String> {
        return Locale.getAvailableLocales()
            .mapNotNull { locale ->
                Currency.getInstance(locale)?.let { it to locale }
            }
            .sortedBy { it.first.displayName }
            .associate { it.first to it.first.getSymbol(it.second) }
    }

    fun updateCurrency(value: Currency) {
        _uiState.update { it.copy(currency = value) }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val repo = (this[APPLICATION_KEY] as MainApplication).categoryRepository
                SettingsViewModel(repo)
            }
        }
    }
}