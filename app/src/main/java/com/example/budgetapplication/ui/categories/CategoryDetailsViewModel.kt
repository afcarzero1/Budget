package com.example.budgetapplication.ui.categories

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.budgetapplication.data.accounts.Account
import com.example.budgetapplication.data.categories.CategoriesRepository
import com.example.budgetapplication.data.categories.Category
import com.example.budgetapplication.ui.accounts.AccountDetailsUiState
import com.example.budgetapplication.ui.accounts.AccountDetailsViewModel
import com.example.budgetapplication.ui.navigation.CategoryDetails
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import com.example.budgetapplication.data.categories.CategoryType

class CategoryDetailsViewModel(
    savedStateHandle: SavedStateHandle,
    private val categoriesRepository: CategoriesRepository
) : ViewModel() {

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }

    val categoryId: Int = checkNotNull(savedStateHandle[CategoryDetails.categoryIdArg])

    val categoryState: StateFlow<CategoryDetailsUiState> =
        categoriesRepository.getCategoryStream(categoryId)
            .filterNotNull()
            .map {
                CategoryDetailsUiState(it, true)
            }.stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = CategoryDetailsUiState()
            )

    var categoryUiState by mutableStateOf(CategoryDetailsUiState())
        private set

    fun updateUiState(category: Category) {
        this.categoryUiState = CategoryDetailsUiState(
            category = category,
            isValid = validateInput(category)
        )
    }

    private fun validateInput(category: Category): Boolean {
        //TODO: check here no cycles in category tree
        return category.name.isNotBlank()
    }

    suspend fun updateCategory() {
        if (categoryUiState.isValid) {
         categoriesRepository.update(categoryUiState.category)
        }
    }

    suspend fun deleteCategory() {
        categoriesRepository.delete(categoryState.value.category)
    }


}

data class CategoryDetailsUiState(
    val category: Category = Category(
        id = -1,
        name = "",
        defaultType = CategoryType.Expense,
        parentCategoryId = -1,
        iconResId = null
    ),
    val isValid: Boolean = false
)
