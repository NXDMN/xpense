package com.nxdmn.xpense.screens.categoryDetail

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
import com.nxdmn.xpense.data.models.CategoryModel
import com.nxdmn.xpense.data.repositories.CategoryRepository
import com.nxdmn.xpense.navigation.CategoryDetail
import com.nxdmn.xpense.ui.CategoryIcon
import com.nxdmn.xpense.ui.DisplayState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable

private const val CATEGORY_STATE_SAVED_STATE_KEY = "CategoryStateKey"

@Serializable
data class CategoryState(
    val name: String? = null,
    val icon: CategoryIcon? = null,
    val color: Long = 0xFF808080,
)

data class CategoryDetailUiState(
    val displayState: DisplayState = DisplayState.Loading,
    val isEdit: Boolean = false,
    val category: CategoryState = CategoryState(),
    val nameErrorText: String? = null,
    val iconErrorText: String? = null,
)

class CategoryDetailViewModel(
    private val savedStateHandle: SavedStateHandle,
    private val repository: CategoryRepository,
    private val categoryId: Long?
) : ViewModel() {
    private val _uiState = MutableStateFlow(CategoryDetailUiState())
    val uiState: StateFlow<CategoryDetailUiState> = _uiState.asStateFlow()

    private var categoryState by savedStateHandle.saved(key = CATEGORY_STATE_SAVED_STATE_KEY) { CategoryState() }

    init {
        viewModelScope.launch {
            try {
                val isCategoryStateRestored =
                    savedStateHandle.contains(CATEGORY_STATE_SAVED_STATE_KEY)

                if (!isCategoryStateRestored) {
                    val category: CategoryModel? = categoryId?.let { repository.getCategory(it) }

                    categoryState = if (category != null)
                        CategoryState(
                            name = category.name,
                            icon = category.icon,
                            color = category.color,
                        )
                    else CategoryState()
                }

                _uiState.update {
                    it.copy(
                        displayState = DisplayState.Content,
                        isEdit = categoryId != null,
                        category = categoryState
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

    private inline fun updateCategoryState(crossinline block: (CategoryState) -> CategoryState) {
        categoryState = block(categoryState)
        _uiState.update { it.copy(category = categoryState) }
    }

    fun updateName(value: String) = updateCategoryState {
        it.copy(name = value)
    }

    fun updateIcon(value: CategoryIcon) = updateCategoryState {
        it.copy(icon = value)
    }

    fun updateColor(value: Long) = updateCategoryState {
        it.copy(color = value)
    }

    private fun validate(): Boolean {
        _uiState.update {
            it.copy(nameErrorText = null, iconErrorText = null)
        }
        var valid = true
        if (_uiState.value.category.name.isNullOrBlank()) {
            _uiState.update {
                it.copy(nameErrorText = "Please enter name")
            }
            valid = false
        }
        if (_uiState.value.category.icon == null) {
            _uiState.update {
                it.copy(iconErrorText = "Please choose icon")
            }
            valid = false
        }
        return valid
    }

    fun saveCategory(): Boolean {
        if (!validate()) return false

        val currentCategory = _uiState.value.category
        val category = CategoryModel(
            id = categoryId ?: 0,
            name = currentCategory.name!!,
            icon = currentCategory.icon!!,
            color = currentCategory.color
        )
        viewModelScope.launch {
            if (_uiState.value.isEdit) {
                repository.updateCategory(category)
            } else {
                repository.createCategory(category)
            }
        }
        savedStateHandle.remove<CategoryState>(CATEGORY_STATE_SAVED_STATE_KEY)
        return true
    }

    fun deleteCategory() {
        viewModelScope.launch {
            repository.deleteCategory(categoryId!!)
        }
        savedStateHandle.remove<CategoryState>(CATEGORY_STATE_SAVED_STATE_KEY)
    }

    companion object {
        fun Factory(navKey: CategoryDetail? = null): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val savedStateHandle = createSavedStateHandle()
                val appContainer = (this[APPLICATION_KEY] as MainApplication).appContainer
                CategoryDetailViewModel(
                    savedStateHandle,
                    appContainer.categoryRepository,
                    navKey?.categoryId
                )
            }
        }
    }
}