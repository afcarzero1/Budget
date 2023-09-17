package com.example.budgetapplication.ui.transactions

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
import androidx.lifecycle.viewmodel.compose.viewModel
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
import com.example.budgetapplication.R
import com.example.budgetapplication.data.future_transactions.FutureTransaction
import com.example.budgetapplication.ui.AppViewModelProvider
import com.example.budgetapplication.ui.accounts.AccountsViewModel
import com.example.budgetapplication.ui.categories.CategoriesSummaryViewModel
import com.example.budgetapplication.ui.currencies.CurrenciesViewModel
import kotlinx.coroutines.launch

@Composable
fun FutureTransactionDetailsScreen(
    navigateBack: () -> Unit,
    viewModel: FutureTransactionDetailsViewModel = viewModel(factory = AppViewModelProvider.Factory),
){
    val transactionState by viewModel.transactionState.collectAsState()
    var useUpdatedUiState by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            Surface(
                Modifier
                    .height(dimensionResource(id = R.dimen.tab_height))
                    .fillMaxWidth()
            ) {
                Text(
                    text = stringResource(R.string.entry_transaction_title),
                    modifier = Modifier.padding(dimensionResource(id = R.dimen.medium))
                )
            }
        }
    ) { innerPadding ->
        FutureTransactionDetailsBody(
            futureTransactionDetailsUiState = if (useUpdatedUiState) viewModel.transactionUiState else transactionState,
            navigateBack = navigateBack,
            onTransactionDetailsChanged = {
                useUpdatedUiState = true
                viewModel.updateUiState(it)
            },
            onTransactionDetailsSaved = {
                coroutineScope.launch {
                    viewModel.updateTransaction()
                }
            },
            onTransactionDetailsDeleted = {
                coroutineScope.launch {
                    try {
                        Log.d("FutureTransactionDetailsScreen", "Deleting transaction ${transactionState.transaction.id}")
                        viewModel.deleteTransaction()
                    } catch (e: Exception) {
                        // Show message to user
                        Toast.makeText(context, "Error deleting transaction", Toast.LENGTH_SHORT).show()
                        Log.e("TransactionDetailsScreen", "Error deleting transaction", e)
                    }
                }
            },
            modifier = Modifier.padding(innerPadding)
        )
    }
}

@Composable
fun FutureTransactionDetailsBody(
    futureTransactionDetailsUiState: FutureTransactionDetailsUiState,
    navigateBack: () -> Unit,
    onTransactionDetailsChanged: (FutureTransaction) -> Unit,
    onTransactionDetailsSaved: () -> Unit,
    onTransactionDetailsDeleted: () -> Unit,
    modifier: Modifier = Modifier,
    categoryViewModel: CategoriesSummaryViewModel = viewModel(factory = AppViewModelProvider.Factory),
    currenciesViewModel: CurrenciesViewModel = viewModel(factory = AppViewModelProvider.Factory),
){
    var deleteConfirmationRequired by rememberSaveable { mutableStateOf(false) }

    val availableCategories by categoryViewModel.categoriesUiState.collectAsState()
    val availableCurrencies by currenciesViewModel.currenciesUiState.collectAsState()

    Column(modifier = modifier.fillMaxWidth()) {
        FutureTransactionForm(
            futureTransaction = futureTransactionDetailsUiState.transaction,
            availableCategories = availableCategories.categoriesList.map { it.category },
            availableCurrencies = availableCurrencies.currenciesList,
            onValueChange = { onTransactionDetailsChanged(it)}
        )

        OutlinedButton(
            onClick = {
                onTransactionDetailsSaved()
                navigateBack()
            },
            shape = MaterialTheme.shapes.small,
            modifier = Modifier.fillMaxWidth(),
            enabled = futureTransactionDetailsUiState.isValid
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
                    onTransactionDetailsDeleted()
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
        text = { Text(stringResource(R.string.delete_transaction)) },
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