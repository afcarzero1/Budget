package com.example.budgetahead.ui.categories

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.budgetahead.data.categories.CategoriesRepository
import com.example.budgetahead.data.categories.Category
import com.example.budgetahead.data.categories.CategoryType
import com.example.budgetahead.ui.components.graphics.AvailableIcons
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class CategoryEntryViewModel(
    private val categoriesRepository: CategoriesRepository,
) : ViewModel() {
    var categoryUiState by mutableStateOf(CategoryUiState())
        private set

    var categoriesListState: StateFlow<List<Category>> =
        categoriesRepository
            .getAllCategoriesStream()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000L),
                initialValue = listOf(),
            )

    fun updateUiState(category: Category) {
        categoryUiState =
            CategoryUiState(
                category = category,
                isValid = validateInput(category),
            )
    }

    private fun validateInput(category: Category): Boolean = category.name.isNotBlank()

    suspend fun saveCategory() {
        if (validateInput(categoryUiState.category)) {
            categoriesRepository.insert(categoryUiState.category)
        }
    }
}

data class CategoryUiState(
    val category: Category =
        Category(
            id = 0,
            name = "",
            defaultType = CategoryType.Expense,
            parentCategoryId = null,
            iconResId = AvailableIcons.icons[0],
        ),
    val isValid: Boolean = false,
)
