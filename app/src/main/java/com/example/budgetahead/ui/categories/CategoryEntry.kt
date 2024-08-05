package com.example.budgetahead.ui.categories

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.budgetahead.R
import com.example.budgetahead.data.categories.Category
import com.example.budgetahead.data.categories.CategoryType
import com.example.budgetahead.ui.AppViewModelProvider
import com.example.budgetahead.ui.components.LargeDropdownMenu
import com.example.budgetahead.ui.components.graphics.AvailableIcons
import com.example.budgetahead.ui.components.graphics.IconPicker
import com.example.budgetahead.ui.navigation.SecondaryScreenTopBar
import kotlinx.coroutines.launch

@Composable
fun CategoryEntryScreen(
    navigateBack: () -> Unit,
    viewModel: CategoryEntryViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val coroutineScope = rememberCoroutineScope()
    // val availableCategories by viewModel.categoriesListState.collectAsState()

    Scaffold(
        topBar = {
            SecondaryScreenTopBar(
                navigateBack = navigateBack,
                titleResId = R.string.entry_category_title
            )
        }
    ) { innerPadding ->
        CategoryEntryBody(
            categoryUiState = viewModel.categoryUiState,
            onCategoryValueChange = viewModel::updateUiState,
            onSaveClick = {
                coroutineScope.launch {
                    viewModel.saveCategory()
                    navigateBack()
                }
            },
            modifier =
            Modifier
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .fillMaxWidth()
        )
    }
}

@Composable
fun CategoryEntryBody(
    categoryUiState: CategoryUiState,
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
            // availableCategories = availableCategories,
            onValueChange = onCategoryValueChange,
            modifier = Modifier.fillMaxWidth()
        )
        Button(
            onClick = onSaveClick,
            enabled = categoryUiState.isValid,
            shape = MaterialTheme.shapes.small,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = stringResource(R.string.entry_category_save))
        }
    }
}

@Composable
fun CategoryForm(
    category: Category,
    // availableCategories: List<Category>,
    onValueChange: (Category) -> Unit,
    modifier: Modifier = Modifier
) {
    val colors =
        OutlinedTextFieldDefaults.colors(
            focusedContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
            unfocusedContainerColor =
            MaterialTheme.colorScheme.onSurfaceVariant.copy(
                alpha = 0.05f
            ),
            disabledContainerColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
        )
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.large))
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            IconPicker(
                currentIconName = category.iconResId ?: "groceries",
                iconOptions = AvailableIcons.icons,
                onIconChanged = {
                    onValueChange(category.copy(iconResId = it))
                },
                modifier = Modifier.padding(end = 16.dp)
            )

            OutlinedTextField(
                value = category.name,
                onValueChange = { onValueChange(category.copy(name = it)) },
                label = { Text(text = stringResource(R.string.entry_category_name)) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                colors = colors
            )
        }

        LargeDropdownMenu(
            label = stringResource(id = R.string.entry_category_type),
            items = listOf(CategoryType.Expense, CategoryType.Income),
            onItemSelected = { index, item -> onValueChange(category.copy(defaultType = item)) },
            initialIndex = if (category.defaultType == CategoryType.Expense) 0 else 1,
            colors = colors
        )
        /*
        LargeDropdownMenu(
            label = stringResource(id = R.string.entry_category_parent),
            items = availableCategories.map { it.name },
            onItemSelected = { index, item ->
                onValueChange(category.copy(parentCategoryId = availableCategories[index].id))
            },
            colors = colors
        )*/
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewCategoryForm() {
    // Create a test category object
    val testCategory =
        Category(
            id = 1,
            name = "Groceries",
            iconResId = "groceries",
            defaultType = CategoryType.Expense,
            parentCategoryId = null
        )

    // Create a list of categories for the dropdown
    val categoriesList =
        listOf(
            Category(
                id = 1,
                name = "Groceries",
                iconResId = "groceries",
                defaultType = CategoryType.Expense,
                parentCategoryId = null
            ),
            Category(
                id = 2,
                name = "Utilities",
                iconResId = "bar",
                defaultType = CategoryType.Expense,
                parentCategoryId = null
            )
        )

    // Provide necessary Theme and Modifier
    MaterialTheme {
        CategoryForm(
            category = testCategory,
            // availableCategories = categoriesList,
            onValueChange = {},
            modifier =
            Modifier
                .padding(16.dp)
                .fillMaxHeight()
        )
    }
}
