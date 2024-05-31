package com.example.budgetapplication.ui.accounts

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
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
import com.example.budgetapplication.R
import com.example.budgetapplication.data.accounts.Account
import com.example.budgetapplication.ui.AppViewModelProvider
import com.example.budgetapplication.ui.components.dialogs.ConfirmationDeletionDialog
import com.example.budgetapplication.ui.currencies.CurrenciesViewModel
import com.example.budgetapplication.ui.navigation.SecondaryScreenTopBar
import kotlinx.coroutines.launch

@Composable
private const val s = "Error deleting account"

@Composable
fun AccountDetailsScreen(
    navigateBack: () -> Unit,
    viewModel: AccountDetailsViewModel = viewModel(factory = AppViewModelProvider.Factory),
) {
    val context = LocalContext.current
    val accountDetails by viewModel.accountState.collectAsState()
    val useUpdatedUiState = viewModel.showUpdatedState

    val coroutineScope = rememberCoroutineScope()
    var deleteConfirmationRequired by rememberSaveable { mutableStateOf(false) }

    Log.d("AccountDetailsScreen", "Loading account with ID : ${viewModel.accountId}")
    Log.d("AccountDetailsScreen", "AccountDetails: $accountDetails")
    Log.d("AccountDetailsScreen", "Use updated: $useUpdatedUiState")

    Scaffold(
        topBar = {
            SecondaryScreenTopBar(
                navigateBack = navigateBack,
                titleResId = R.string.details_account_title,
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
                }
            )
        }
    ) { innerPadding ->
        AccountDetailsBody(
            accountDetailsUiState = if (useUpdatedUiState) viewModel.accountUiState else accountDetails,
            onAccountDetailsChanged = {
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
                            viewModel.deleteAccount()
                        } catch (e: Exception) {
                            Toast.makeText(context, stringResource(id = R.string.error_on_deletion_account), Toast.LENGTH_SHORT)
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
fun AccountDetailsBody(
    accountDetailsUiState: AccountDetailsUiState,
    onAccountDetailsChanged: (Account) -> Unit,
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

