package com.example.budgetahead.ui.categories

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.budgetahead.R
import com.example.budgetahead.data.categories.Category
import com.example.budgetahead.ui.AppViewModelProvider
import com.example.budgetahead.ui.components.dialogs.ConfirmationDeletionDialog
import com.example.budgetahead.ui.navigation.SecondaryScreenTopBar
import kotlinx.coroutines.launch

@Composable
fun CategoryDetailsScreen(
    navigateBack: () -> Unit,
    viewModel: CategoryDetailsViewModel = viewModel(factory = AppViewModelProvider.Factory),
) {

    val categoryState by viewModel.categoryState.collectAsState()
    var useUpdatedUiState = viewModel.showUpdatedState
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var deleteConfirmationRequired by rememberSaveable { mutableStateOf(false) }

    Log.d("CategoryDetailsScreen", "Loading category with ID : ${viewModel.categoryId}")
    Log.d("CategoryDetailsScreen", "CategoryDetails: $categoryState")

    Scaffold(
        topBar = {
            SecondaryScreenTopBar(
                navigateBack = navigateBack,
                titleResId = R.string.details_category_title,
                actions = {
                    IconButton(
                        onClick = { deleteConfirmationRequired = true },
                        modifier = Modifier.padding(horizontal = 8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Delete,
                            contentDescription = stringResource(R.string.delete),
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                    IconButton(
                        modifier = Modifier.padding(horizontal = 8.dp),
                        onClick = {
                            coroutineScope.launch {
                                viewModel.updateCategory()
                            }
                            navigateBack()
                        },
                        enabled = categoryState.isValid
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.save_24dp_fill0_wght400_grad0_opsz24),
                            contentDescription = stringResource(R.string.save),
                            tint = if (categoryState.isValid) MaterialTheme.colorScheme.onPrimary else Color.Gray
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        CategoryDetailsBody(
            categoryDetailsUiState = if (useUpdatedUiState) viewModel.categoryUiState else categoryState,
            onCategoryDetailsChanged = {
                useUpdatedUiState = true
                viewModel.updateUiState(it)
            },
            modifier = Modifier.padding(innerPadding)
        )


        if (deleteConfirmationRequired) {
            ConfirmationDeletionDialog(
                message = stringResource(R.string.delete_category),
                onDeleteConfirm = {
                    deleteConfirmationRequired = false
                    coroutineScope.launch {
                        try {
                            viewModel.deleteCategory()
                        } catch (e: Exception) {
                            // Show message to user
                            Toast.makeText(context, "Error deleting category", Toast.LENGTH_SHORT)
                                .show()
                            Log.e("CategoryDetailsScreen", "Error deleting category", e)
                        }
                    }
                    navigateBack()
                },
                onDeleteCancel = { deleteConfirmationRequired = false },
                modifier = Modifier.padding(dimensionResource(id = R.dimen.medium))
            )
        }

    }
}

@Composable
fun CategoryDetailsBody(
    categoryDetailsUiState: CategoryDetailsUiState,
    onCategoryDetailsChanged: (Category) -> Unit,
    modifier: Modifier = Modifier,
    categoriesSummaryViewModel: CategoriesSummaryViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val availableCategories by categoriesSummaryViewModel.categoriesUiState.collectAsState()

    Column(
        verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.large)),
        modifier = modifier.padding(dimensionResource(id = R.dimen.medium)),
    ) {
        CategoryForm(
            category = categoryDetailsUiState.category,
            //availableCategories = availableCategories.categoriesList.map { it.category },
            onValueChange = { onCategoryDetailsChanged(it) }
        )
    }
}