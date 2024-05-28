package com.example.budgetapplication.ui.accounts

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.budgetapplication.R
import com.example.budgetapplication.data.accounts.Account
import com.example.budgetapplication.ui.AppViewModelProvider
import com.example.budgetapplication.ui.currencies.CurrenciesViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountDetailsScreen(
    navigateBack: () -> Unit,
    viewModel: AccountDetailsViewModel = viewModel(factory = AppViewModelProvider.Factory),
) {

    val accountDetails by viewModel.accountState.collectAsState()
    val useUpdatedUiState = viewModel.showUpdatedState

    val coroutineScope = rememberCoroutineScope()
    var deleteConfirmationRequired by rememberSaveable { mutableStateOf(false) }

    Log.d("AccountDetailsScreen", "Loading account with ID : ${viewModel.accountId}")
    Log.d("AccountDetailsScreen", "AccountDetails: $accountDetails")
    Log.d("AccountDetailsScreen", "Use updated: $useUpdatedUiState")

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.details_account_title),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navigateBack() }) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "Go back",
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                },
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
                                viewModel.updateAccount()
                            }
                            navigateBack()
                        },
                        enabled = accountDetails.isValid
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.save_24dp_fill0_wght400_grad0_opsz24),
                            contentDescription = stringResource(R.string.save),
                            tint = if (accountDetails.isValid) MaterialTheme.colorScheme.onPrimary else Color.Gray
                        )
                    }

                },
                modifier = Modifier
                    .fillMaxWidth(),
                colors = TopAppBarDefaults.smallTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            )
        }
    ) { innerPadding ->
        AccountDetailsBody(
            accountDetailsUiState = if (useUpdatedUiState) viewModel.accountUiState else accountDetails,
            navigateBack = navigateBack,
            onAccountDetailsChanged = {
                viewModel.updateUiState(it)
            },
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

        if (deleteConfirmationRequired) {
            DeleteConfirmationDialog(
                onDeleteConfirm = {
                    deleteConfirmationRequired = false
                    coroutineScope.launch {
                        viewModel.deleteAccount()
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
fun AccountDetailsBody(
    accountDetailsUiState: AccountDetailsUiState,
    navigateBack: () -> Unit,
    onAccountDetailsChanged: (Account) -> Unit,
    onAccountDetailsSaved: () -> Unit,
    onAccountDetailsDeleted: () -> Unit,
    modifier: Modifier = Modifier,
    currenciesViewModel: CurrenciesViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val availableCurrencies by currenciesViewModel.currenciesUiState.collectAsState()

    Surface(color = MaterialTheme.colorScheme.background, modifier = Modifier.fillMaxSize()) {
        Column(
            verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.large)),
            modifier = modifier.padding(dimensionResource(id = R.dimen.medium)),
        ) {
            AccountForm(
                account = accountDetailsUiState.account,
                availableCurrencies = availableCurrencies.currenciesList,
                onValueChange = { onAccountDetailsChanged(it) }
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