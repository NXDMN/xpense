package com.nxdmn.xpense.screens.setting

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.nxdmn.xpense.MainApplication
import com.nxdmn.xpense.data.repositories.CategoryRepository
import kotlinx.coroutines.launch

class SettingsViewModel(repository: CategoryRepository) : ViewModel() {

    init {
        viewModelScope.launch {
        }
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