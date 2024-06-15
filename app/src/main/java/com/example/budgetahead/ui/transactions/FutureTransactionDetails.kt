package com.example.budgetahead.ui.transactions

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.viewmodel.compose.viewModel
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
import com.example.budgetahead.R
import com.example.budgetahead.data.future_transactions.FutureTransaction
import com.example.budgetahead.ui.AppViewModelProvider
import com.example.budgetahead.ui.categories.CategoriesSummaryViewModel
import com.example.budgetahead.ui.components.dialogs.ConfirmationDeletionDialog
import com.example.budgetahead.ui.currencies.CurrenciesViewModel
import com.example.budgetahead.ui.navigation.SecondaryScreenTopBar
import kotlinx.coroutines.launch

@Composable
fun FutureTransactionDetailsScreen(
    navigateBack: () -> Unit,
    viewModel: FutureTransactionDetailsViewModel = viewModel(factory = AppViewModelProvider.Factory),
) {
    val transactionState by viewModel.transactionState.collectAsState()
    val useUpdatedUiState = viewModel.showUpdatedState
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var deleteConfirmationRequired by rememberSaveable { mutableStateOf(false) }


    Scaffold(
        topBar = {
            SecondaryScreenTopBar(
                navigateBack = navigateBack,
                titleResId = R.string.details_transaction_title,
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
                                viewModel.updateTransaction()
                            }
                            navigateBack()
                        },
                        enabled = transactionState.isValid
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.save_24dp_fill0_wght400_grad0_opsz24),
                            contentDescription = stringResource(R.string.save),
                            tint = if (transactionState.isValid) MaterialTheme.colorScheme.onPrimary else Color.Gray
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        FutureTransactionDetailsBody(
            futureTransactionDetailsUiState = if (useUpdatedUiState) viewModel.transactionUiState else transactionState,
            onTransactionDetailsChanged = {
                viewModel.updateUiState(it)
            },
            modifier = Modifier.padding(innerPadding)
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
                            Toast.makeText(
                                context,
                                "Error deleting transaction",
                                Toast.LENGTH_SHORT
                            )
                                .show()
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
fun FutureTransactionDetailsBody(
    futureTransactionDetailsUiState: FutureTransactionDetailsUiState,
    onTransactionDetailsChanged: (FutureTransaction) -> Unit,
    modifier: Modifier = Modifier,
    categoryViewModel: CategoriesSummaryViewModel = viewModel(factory = AppViewModelProvider.Factory),
    currenciesViewModel: CurrenciesViewModel = viewModel(factory = AppViewModelProvider.Factory),
) {

    val availableCategories by categoryViewModel.categoriesUiState.collectAsState()
    val availableCurrencies by currenciesViewModel.currenciesUiState.collectAsState()

    Column(modifier = modifier.fillMaxWidth()) {
        FutureTransactionForm(
            futureTransaction = futureTransactionDetailsUiState.transaction,
            availableCategories = availableCategories.categoriesList.map { it.category },
            availableCurrencies = availableCurrencies.currenciesList,
            onValueChange = { onTransactionDetailsChanged(it) },
            modifier = Modifier.padding(16.dp)
        )
    }
}
