package com.example.budgetapplication.ui.categories

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.budgetapplication.R
import com.example.budgetapplication.data.categories.Category
import com.example.budgetapplication.ui.AppViewModelProvider
import com.example.budgetapplication.ui.accounts.AccountForm
import com.example.budgetapplication.ui.components.LargeDropdownMenu
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryEntryScreen(
    navigateBack: () -> Unit,
    viewModel: CategoryEntryViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val coroutineScope = rememberCoroutineScope()
    val availableCategories by viewModel.categoriesListState.collectAsState()

    Scaffold(topBar = {
        Surface(
            Modifier
                .height(dimensionResource(id = R.dimen.tab_height))
                .fillMaxWidth()
        ) {
            Text(
                text = stringResource(R.string.entry_category_title),
                modifier = Modifier.padding(dimensionResource(id = R.dimen.medium))
            )
        }
    }) { innerPadding ->
        CategoryEntryBody(
            categoryUiState = viewModel.categoryUiState,
            availableCategories = availableCategories,
            onCategoryValueChange = viewModel::updateUiState,
            onSaveClick = {
                coroutineScope.launch {
                    viewModel.saveCategory()
                    navigateBack()
                }
            },
            modifier = Modifier
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .fillMaxWidth()
        )
    }
}


@Composable
fun CategoryEntryBody(
    categoryUiState: CategoryUiState,
    availableCategories: List<Category>,
    onCategoryValueChange: (Category) -> Unit,
    onSaveClick: () -> Unit,
    modifier: Modifier = Modifier
) {

    Column(
        verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.large)),
        modifier = modifier.padding(dimensionResource(id = R.dimen.medium))
    ) {
        CategoryForm(
            category = categoryUiState.category,
            availableCategories = availableCategories,
            onValueChange = onCategoryValueChange,
            modifier = Modifier.fillMaxWidth()
        )
        Button(
            onClick = onSaveClick,
            enabled = categoryUiState.isValid,
            shape = MaterialTheme.shapes.small,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = stringResource(R.string.entry_account_save))
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryForm(
    category: Category,
    availableCategories: List<Category>,
    onValueChange: (Category) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.large))
    ) {
        OutlinedTextField(value = category.name,
            onValueChange = { onValueChange(category.copy(name = it)) },
            label = { Text(text = stringResource(R.string.entry_category_name)) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        LargeDropdownMenu(
            label = stringResource(id = R.string.entry_category_type),
            items = listOf("Expense", "Income"),
            onItemSelected = { index, item -> onValueChange(category.copy(defaultType = item)) },
            initialIndex = 0
        )
        LargeDropdownMenu(
            label = stringResource(id = R.string.entry_category_parent),
            items = availableCategories.map { it.name },
            onItemSelected = {index, item -> onValueChange(category.copy(parentCategoryId = availableCategories[index].id)) },
        )
    }
}