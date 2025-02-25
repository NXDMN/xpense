package com.nxdmn.xpense.screens.setting

import android.icu.util.Currency
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.nxdmn.xpense.MainApplication
import com.nxdmn.xpense.data.dataStores.UserPrefsDataStore
import com.nxdmn.xpense.data.models.CategoryModel
import com.nxdmn.xpense.data.repositories.CategoryRepository
import com.nxdmn.xpense.helpers.CurrencyHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class SettingsUiState(
    val categoryList: List<CategoryModel> = emptyList(),
    val currencySymbolMap: Map<Currency, String> = emptyMap(),
    val currencySymbol: String? = null,
    val favouriteCategory: CategoryModel? = null,
)

class SettingsViewModel(
    private val repository: CategoryRepository,
    private val dataStore: UserPrefsDataStore
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    private lateinit var currency: Currency

    init {
        viewModelScope.launch {
            currency = dataStore.getCurrency()
            val favCatId = dataStore.getFavCategoryId()
            _uiState.update {
                it.copy(
                    currencySymbolMap = CurrencyHelper.currencySymbolMap,
                    currencySymbol = CurrencyHelper.getSymbol(currency),
                    favouriteCategory = it.categoryList.find { c -> c.id == favCatId }
                )
            }
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


    fun updateCurrency(value: Currency) {
        _uiState.update { it.copy(currencySymbol = CurrencyHelper.getSymbol(value)) }
        viewModelScope.launch {
            dataStore.setCurrency(value)
        }
    }

    fun updateFavouriteCategory(value: CategoryModel) {
        _uiState.update { it.copy(favouriteCategory = value) }
        viewModelScope.launch {
            dataStore.setFavCategoryId(value.id)
        }
    }

    fun removeFavouriteCategory() {
        _uiState.update { it.copy(favouriteCategory = null) }
        viewModelScope.launch {
            dataStore.setFavCategoryId(null)
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val repo = (this[APPLICATION_KEY] as MainApplication).categoryRepository
                val ds = (this[APPLICATION_KEY] as MainApplication).userPrefsDataStore
                SettingsViewModel(repo, ds)
            }
        }
    }
}