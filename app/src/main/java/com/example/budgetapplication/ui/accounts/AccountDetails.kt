package com.example.budgetapplication.ui.accounts

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.budgetapplication.ui.AppViewModelProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import com.example.budgetapplication.R
import com.example.budgetapplication.data.accounts.Account
import com.example.budgetapplication.ui.currencies.CurrenciesViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountDetailsScreen(
    navigateBack: () -> Unit,
    viewModel: AccountDetailsViewModel = viewModel(factory = AppViewModelProvider.Factory),
) {

    val accountDetails by viewModel.accountState.collectAsState()
    val coroutineScope = rememberCoroutineScope()

    Log.d("AccountDetailsScreen", "Loading account with ID : ${viewModel.accountId}")
    Log.d("AccountDetailsScreen", "AccountDetails: $accountDetails")

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
        AccountDetailsBody(
            accountDetails = accountDetails.account,
            navigateBack = navigateBack,
            onAccountDetailsChanged = viewModel::updateUiState,
            onAccountDetailsSaved = {
                coroutineScope.launch {
                    viewModel.updateAccount()
                }
            },
            onAccountDetailsDeleted = {
                coroutineScope.launch {
                    viewModel.deleteAccount()
                }
            },
            modifier = Modifier.padding(innerPadding)
        )
    }
}

@Composable
fun AccountDetailsBody(
    accountDetails: Account,
    navigateBack: () -> Unit,
    onAccountDetailsChanged: (Account) -> Unit,
    onAccountDetailsSaved: () -> Unit,
    onAccountDetailsDeleted: () -> Unit,
    modifier: Modifier = Modifier,
    currenciesViewModel: CurrenciesViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    var deleteConfirmationRequired by rememberSaveable { mutableStateOf(false) }
    val availableCurrencies by currenciesViewModel.currenciesUiState.collectAsState()

    Column(modifier = modifier.fillMaxWidth()) {
        AccountForm(
            account = accountDetails,
            availableCurrencies = availableCurrencies.currenciesList,
            onValueChange = { onAccountDetailsChanged(it) }
        )

        OutlinedButton(
            onClick = {
                onAccountDetailsSaved()
                navigateBack()
            },
            shape = MaterialTheme.shapes.small,
            modifier = Modifier.fillMaxWidth()

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
                    onAccountDetailsDeleted()
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