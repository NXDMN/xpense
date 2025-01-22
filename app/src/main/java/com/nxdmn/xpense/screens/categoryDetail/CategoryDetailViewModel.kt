package com.nxdmn.xpense.screens.categoryDetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.nxdmn.xpense.MainApplication
import com.nxdmn.xpense.data.models.CategoryModel
import com.nxdmn.xpense.data.repositories.CategoryRepository
import com.nxdmn.xpense.ui.CategoryIcon
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class CategoryDetailUiState(
    val name: String? = null,
    var icon: CategoryIcon? = null,
    var color: Long? = null,
)

class CategoryDetailViewModel(repository: CategoryRepository, categoryId: Long?) : ViewModel() {
    private val _uiState = MutableStateFlow(CategoryDetailUiState())
    val uiState: StateFlow<CategoryDetailUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            val category: CategoryModel? =
                if (categoryId != null) repository.getCategory(categoryId) else null

            if (category != null)
                _uiState.update {
                    it.copy(name = category.name, icon = category.icon, color = category.color)
                }
        }
    }

    fun updateName(value: String) = _uiState.update {
        it.copy(name = value)
    }

    companion object {
        val CATEGORY_ID_KEY = object : CreationExtras.Key<Long?> {}
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val repo = (this[APPLICATION_KEY] as MainApplication).categoryRepository
                val categoryId = this[CATEGORY_ID_KEY]
                CategoryDetailViewModel(repo, categoryId)
            }
        }
    }
}