package com.example.budgetapplication.ui.categories

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.budgetapplication.R
import com.example.budgetapplication.data.categories.Category
import com.example.budgetapplication.ui.AppViewModelProvider
import kotlinx.coroutines.launch

@Composable
fun CategoryDetailsScreen(
    navigateBack: () -> Unit,
    viewModel: CategoryDetailsViewModel = viewModel(factory = AppViewModelProvider.Factory),
) {

    val categoryState by viewModel.categoryState.collectAsState()
    var useUpdatedUiState by remember { mutableStateOf(false) }
    val context = LocalContext.current

    val coroutineScope = rememberCoroutineScope()

    Log.d("CategoryDetailsScreen", "Loading category with ID : ${viewModel.categoryId}")
    Log.d("CategoryDetailsScreen", "CategoryDetails: $categoryState")

    Scaffold(
        topBar = {
            Surface(
                Modifier
                    .height(dimensionResource(id = R.dimen.tab_height))
                    .fillMaxWidth()
            ) {
                Text(
                    text = stringResource(R.string.entry_account_title),
                    modifier = Modifier.padding(dimensionResource(id = R.dimen.medium))
                )
            }
        }
    ) { innerPadding ->
        CategoryDetailsBody(
            categoryDetailsUiState = if (useUpdatedUiState) viewModel.categoryUiState else categoryState,
            navigateBack = navigateBack,
            onCategoryDetailsChanged = {
                useUpdatedUiState = true
                viewModel.updateUiState(it)
            },
            onCategoryDetailsSaved = {
                coroutineScope.launch {
                    viewModel.updateCategory()
                }
            },
            onCategoryDetailsDeleted = {
                coroutineScope.launch {
                    try {
                        viewModel.deleteCategory()
                    } catch (e: Exception) {
                        // Show message to user toast
                        Toast.makeText(context, "Error deleting category", Toast.LENGTH_SHORT).show()
                        Log.e("CategoryDetailsScreen", "Error deleting category", e)
                    }
                }
            },
            modifier = Modifier.padding(innerPadding)
        )
    }
}

@Composable
fun CategoryDetailsBody(
    categoryDetailsUiState: CategoryDetailsUiState,
    navigateBack: () -> Unit,
    onCategoryDetailsChanged: (Category) -> Unit,
    onCategoryDetailsSaved: () -> Unit,
    onCategoryDetailsDeleted: () -> Unit,
    modifier: Modifier = Modifier,
    categoriesSummaryViewModel: CategoriesSummaryViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    var deleteConfirmationRequired by rememberSaveable { mutableStateOf(false) }
    val availableCategories by categoriesSummaryViewModel.categoriesUiState.collectAsState()

    Column(modifier = modifier.fillMaxWidth()) {
        CategoryForm(
            category = categoryDetailsUiState.category,
            availableCategories = availableCategories.categoriesList.map { it.category },
            onValueChange = { onCategoryDetailsChanged(it) }
        )

        OutlinedButton(
            onClick = {
                onCategoryDetailsSaved()
                navigateBack()
            },
            shape = MaterialTheme.shapes.small,
            modifier = Modifier.fillMaxWidth(),
            enabled = categoryDetailsUiState.isValid
        ) {
            Text(stringResource(R.string.save))
        }

        OutlinedButton(
            onClick = { deleteConfirmationRequired = true },
            shape = MaterialTheme.shapes.small,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(stringResource(R.string.delete))
        }

        if (deleteConfirmationRequired) {
            DeleteConfirmationDialog(
                onDeleteConfirm = {
                    deleteConfirmationRequired = false
                    onCategoryDetailsDeleted()
                    navigateBack()
                },
                onDeleteCancel = { deleteConfirmationRequired = false },
                modifier = Modifier.padding(dimensionResource(id = R.dimen.medium))
            )
        }
    }
}


@Composable
private fun DeleteConfirmationDialog(
    onDeleteConfirm: () -> Unit,
    onDeleteCancel: () -> Unit,
    modifier: Modifier = Modifier
) {
    AlertDialog(onDismissRequest = { /* Do nothing */ },
        title = { Text(stringResource(R.string.attention)) },
        text = { Text(stringResource(R.string.delete_account)) },
        modifier = modifier,
        dismissButton = {
            TextButton(onClick = onDeleteCancel) {
                Text(stringResource(R.string.no))
            }
        },
        confirmButton = {
            TextButton(onClick = onDeleteConfirm) {
                Text(stringResource(R.string.yes))
            }
        })
}