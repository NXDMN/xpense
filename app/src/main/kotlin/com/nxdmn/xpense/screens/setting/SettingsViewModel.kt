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
import com.nxdmn.xpense.ui.DisplayState
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class SettingsUiState(
    val displayState: DisplayState = DisplayState.Loading,
    val categoryList: List<CategoryModel> = emptyList(),
    val currencySymbolMap: Map<Currency, String> = emptyMap(),
    val currencySymbol: String? = null,
    val favouriteCategory: CategoryModel? = null,
)

class SettingsViewModel(
    private val repository: CategoryRepository,
    private val dataStore: UserPrefsDataStore
) : ViewModel() {

    val uiState: StateFlow<SettingsUiState> = combine(
        repository.categoryListFlow,
        dataStore.currencyFlow,
        dataStore.favCategoryIdFlow
    ) { categories, currency, favCategoryId ->

        val categoryList = categories.toMutableList()
        val favCategory = categoryList.find { it.id == favCategoryId }

        if (favCategory != null) {
            categoryList.remove(favCategory)
            categoryList.add(0, favCategory)
        }

        SettingsUiState(
            displayState = DisplayState.Content,
            categoryList = categoryList,
            currencySymbolMap = CurrencyHelper.currencySymbolMap,
            currencySymbol = CurrencyHelper.getSymbol(currency),
            favouriteCategory = favCategory
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = SettingsUiState(displayState = DisplayState.Loading)
    )

    fun deleteCategory(categoryId: Long) = viewModelScope.launch {
        repository.deleteCategory(categoryId)
    }

    fun updateCurrency(value: Currency) = viewModelScope.launch {
        dataStore.setCurrency(value)
    }

    fun updateFavouriteCategory(value: CategoryModel) = viewModelScope.launch {
        dataStore.setFavCategoryId(value.id)
    }

    fun removeFavouriteCategory() = viewModelScope.launch {
        dataStore.setFavCategoryId(null)
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