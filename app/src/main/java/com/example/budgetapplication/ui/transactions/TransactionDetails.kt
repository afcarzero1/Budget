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
import com.example.budgetapplication.data.transactions.TransactionRecord
import com.example.budgetapplication.ui.AppViewModelProvider
import com.example.budgetapplication.ui.accounts.AccountsViewModel
import com.example.budgetapplication.ui.categories.CategoriesSummaryViewModel
import kotlinx.coroutines.launch


@Composable
fun TransactionDetailsScreen(
    navigateBack: () -> Unit,
    viewModel: TransactionDetailsViewModel = viewModel(factory = AppViewModelProvider.Factory),
){
    val transactionDBState by viewModel.transactionState.collectAsState()
    var useUpdatedUiState by remember { mutableStateOf(false) }
    val transactionUiState = viewModel.transactionUiState

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
        TransactionDetailsBody(
            transactionDetailsUiState = if (useUpdatedUiState) viewModel.transactionUiState else transactionDBState,
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
                        Log.d("TransactionDetailsScreen", "Deleting transaction ${transactionDBState.transaction.id}")
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
fun TransactionDetailsBody(
    transactionDetailsUiState: TransactionDetailsUiState,
    navigateBack: () -> Unit,
    onTransactionDetailsChanged: (TransactionRecord) -> Unit,
    onTransactionDetailsSaved: () -> Unit,
    onTransactionDetailsDeleted: () -> Unit,
    modifier: Modifier = Modifier,
    categoriesViewModel: CategoriesSummaryViewModel = viewModel(factory = AppViewModelProvider.Factory),
    accountsViewModel: AccountsViewModel = viewModel(factory = AppViewModelProvider.Factory),

){
    var deleteConfirmationRequired by rememberSaveable { mutableStateOf(false) }

    val availableCategories by categoriesViewModel.categoriesUiState.collectAsState()
    val availableAccounts by accountsViewModel.accountsUiState.collectAsState()

    Column(modifier = modifier.fillMaxWidth()) {
        TransactionForm(
            transactionRecord = transactionDetailsUiState.transaction,
            onValueChange = { onTransactionDetailsChanged(it) },
            availableAccounts = availableAccounts.accountsList.map { it.account },
            availableCategories = availableCategories.categoriesList.map { it.category },
        )

        OutlinedButton(
            onClick = {
                onTransactionDetailsSaved()
                navigateBack()
            },
            shape = MaterialTheme.shapes.small,
            modifier = Modifier.fillMaxWidth(),
            enabled = transactionDetailsUiState.isValid
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