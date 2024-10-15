package com.example.budgetahead.ui.transactions

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import com.example.budgetahead.data.transactions.TransactionRecord
import com.example.budgetahead.ui.AppViewModelProvider
import com.example.budgetahead.ui.accounts.AccountsViewModel
import com.example.budgetahead.ui.categories.CategoriesSummaryViewModel
import com.example.budgetahead.ui.components.dialogs.ConfirmationDeletionDialog
import com.example.budgetahead.ui.navigation.SecondaryScreenTopBar
import kotlinx.coroutines.launch

@Composable
fun TransactionDetailsScreen(
    navigateBack: () -> Unit,
    viewModel: TransactionDetailsViewModel = viewModel(factory = AppViewModelProvider.Factory),
) {
    val transactionDBState by viewModel.transactionDBState.collectAsState()
    val transactionUiState = viewModel.transactionUiState
    Log.d("TransactionDetailsScreen", "Transaction in DB: ${transactionDBState.transaction.id}")
    Log.d("TransactionDetailsScreen", "Transaction in UI: ${transactionUiState.transaction.id}")

    val context = LocalContext.current
    var deleteConfirmationRequired by rememberSaveable { mutableStateOf(false) }

    val coroutineScope = rememberCoroutineScope()

    Scaffold(topBar = {
        SecondaryScreenTopBar(
            navigateBack = navigateBack,
            titleResId = R.string.details_transaction_title,
            actions = {
                IconButton(
                    onClick = { deleteConfirmationRequired = true },
                    modifier = Modifier.padding(horizontal = 8.dp),
                ) {
                    Icon(
                        imageVector = Icons.Filled.Delete,
                        contentDescription = stringResource(R.string.delete),
                        tint = MaterialTheme.colorScheme.onPrimary,
                    )
                }
                IconButton(
                    modifier = Modifier.padding(horizontal = 8.dp),
                    onClick = {
                        coroutineScope.launch {
                            viewModel.updateTransaction()
                        }
                        navigateBack()
                    },
                    enabled = transactionUiState.isValid,
                ) {
                    Icon(
                        painter =
                            painterResource(
                                id = R.drawable.save_24dp_fill0_wght400_grad0_opsz24,
                            ),
                        contentDescription = stringResource(R.string.save),
                        tint = if (transactionUiState.isValid) MaterialTheme.colorScheme.onPrimary else Color.Gray,
                    )
                }
            },
        )
    }) { innerPadding ->
        TransactionDetailsBody(
            transactionDetailsUiState = viewModel.transactionUiState,
            onTransactionDetailsChanged = {
                viewModel.updateUiState(it)
            },
            modifier = Modifier.padding(innerPadding),
        )

        if (deleteConfirmationRequired) {
            ConfirmationDeletionDialog(
                message = stringResource(R.string.delete_account),
                onDeleteConfirm = {
                    deleteConfirmationRequired = false
                    coroutineScope.launch {
                        try {
                            viewModel.deleteTransaction()
                        } catch (e: Exception) {
                            Toast
                                .makeText(
                                    context,
                                    "Error deleting account",
                                    Toast.LENGTH_SHORT,
                                ).show()
                        }
                    }
                    navigateBack()
                },
                onDeleteCancel = { deleteConfirmationRequired = false },
                modifier = Modifier.padding(dimensionResource(id = R.dimen.medium)),
            )
        }
    }
}

@Composable
fun TransactionDetailsBody(
    transactionDetailsUiState: TransactionDetailsUiState,
    onTransactionDetailsChanged: (TransactionRecord) -> Unit,
    modifier: Modifier = Modifier,
    categoriesViewModel: CategoriesSummaryViewModel =
        viewModel(factory = AppViewModelProvider.Factory),
    accountsViewModel: AccountsViewModel = viewModel(factory = AppViewModelProvider.Factory),
) {
    val availableCategories by categoriesViewModel.categoriesUiState.collectAsState()
    val availableAccounts by accountsViewModel.accountsUiState.collectAsState()

    Column(modifier = modifier.fillMaxWidth()) {
        TransactionForm(
            transactionRecord = transactionDetailsUiState.transaction,
            onValueChange = { onTransactionDetailsChanged(it) },
            availableAccounts = availableAccounts.accountsList.map { it.account },
            availableCategories = availableCategories.categoriesList.map { it.category },
            modifier = Modifier.padding(dimensionResource(id = R.dimen.medium)),
        )
    }
}

@Composable
private fun DeleteConfirmationDialog(
    onDeleteConfirm: () -> Unit,
    onDeleteCancel: () -> Unit,
    modifier: Modifier = Modifier,
) {
    AlertDialog(
        onDismissRequest = { /* Do nothing */ },
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
        },
    )
}
