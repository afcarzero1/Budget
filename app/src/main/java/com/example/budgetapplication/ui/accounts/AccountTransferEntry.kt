package com.example.budgetapplication.ui.accounts

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.budgetapplication.R
import com.example.budgetapplication.data.accounts.FullAccount
import com.example.budgetapplication.data.transfers.Transfer
import com.example.budgetapplication.ui.AppViewModelProvider
import com.example.budgetapplication.ui.components.DatePickerField
import com.example.budgetapplication.ui.components.LargeDropdownMenu
import kotlinx.coroutines.launch


@Composable
fun TransferEntryScreen(
    navigateBack: () -> Unit,
    viewModel: AccountTransferEntryViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val coroutineScope = rememberCoroutineScope()
    val accountsListState by viewModel.accountsListState.collectAsState()

    Scaffold(topBar = {
        Surface(
            Modifier
                .height(dimensionResource(id = R.dimen.tab_height))
                .fillMaxWidth()
        ) {
            Text(
                text = stringResource(R.string.entry_transfer_title),
                modifier = Modifier.padding(dimensionResource(id = R.dimen.medium))
            )
        }
    }) { innerPadding ->

        TransferEntryBody(
            transferUiState = viewModel.transferUiState,
            availableAccounts = accountsListState,
            onTransferValueChanged = viewModel::updateUiState,
            onSaveClick = {
                coroutineScope.launch {
                    viewModel.saveTransfer()
                    navigateBack()
                }
            },
            modifier = Modifier
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .fillMaxWidth()
        )
    }
}

@Composable
fun TransferEntryBody(
    transferUiState: AccountTransferUiState,
    availableAccounts: List<FullAccount>,
    onTransferValueChanged: (Transfer) -> Unit,
    onSaveClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.large)),
        modifier = modifier.padding(dimensionResource(id = R.dimen.medium))
    ) {
        AccountTransferForm(
            transfer = transferUiState.transfer,
            availableAccounts = availableAccounts,
            onValueChange = onTransferValueChanged,
            modifier = Modifier.fillMaxWidth()
        )
        Button(
            onClick = onSaveClick,
            enabled = transferUiState.isValid,
            shape = MaterialTheme.shapes.small,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = stringResource(R.string.transfer_entry_save_transfer))
        }
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountTransferForm(
    transfer: Transfer,
    availableAccounts: List<FullAccount>,
    onValueChange: (Transfer) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.large))
    ) {
        OutlinedTextField(
            value = transfer.amountSource.toString(),
            onValueChange = {
                onValueChange(transfer.copy(amountSource = it.toFloat()))
            },
            label = { Text(text = stringResource(R.string.entry_transaction_amount)) },
            colors = TextFieldDefaults.outlinedTextFieldColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer
            ),
            modifier = Modifier.fillMaxWidth(),
            enabled = true,
            singleLine = true,
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
        )
        OutlinedTextField(
            value = transfer.amountDestination.toString(),
            onValueChange = {
                onValueChange(transfer.copy(amountDestination = it.toFloat()))
            },
            label = { Text(text = stringResource(R.string.entry_transaction_amount)) },
            colors = TextFieldDefaults.outlinedTextFieldColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer
            ),
            modifier = Modifier.fillMaxWidth(),
            enabled = true,
            singleLine = true,
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
        )
        LargeDropdownMenu(
            label = stringResource(R.string.transfer_entry_source_account),
            items = availableAccounts.map {
                it.account.name
            },
            onItemSelected = { index, item ->
                onValueChange(
                    transfer.copy(sourceAccount = availableAccounts[index].account)
                )
            },
            initialIndex = availableAccounts.indexOfFirst {
                it.account.id == transfer.sourceAccount.id
            }
        )

        LargeDropdownMenu(
            label = stringResource(R.string.transfer_entry_destination_account),
            items = availableAccounts.map {
                it.account.name
            },
            onItemSelected = { index, item ->
                onValueChange(
                    transfer.copy(destinationAccount = availableAccounts[index].account)
                )
            },
            initialIndex = availableAccounts.indexOfFirst {
                it.account.id == transfer.destinationAccount.id
            }
        )

        DatePickerField(
            label = stringResource(id = R.string.entry_transaction_date),
            onDateChanged = { onValueChange(transfer.copy(date = it)) },
            initialDate = transfer.date
        )
    }
}