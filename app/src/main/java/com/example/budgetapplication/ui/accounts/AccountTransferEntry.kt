package com.example.budgetapplication.ui.accounts

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.budgetapplication.R
import com.example.budgetapplication.data.accounts.FullAccount
import com.example.budgetapplication.data.transfers.Transfer
import com.example.budgetapplication.ui.AppViewModelProvider
import com.example.budgetapplication.ui.components.DatePickerField
import com.example.budgetapplication.ui.components.LargeDropdownMenu
import com.example.budgetapplication.ui.components.inputs.FloatOutlinedText
import com.example.budgetapplication.ui.navigation.SecondaryScreenTopBar
import kotlinx.coroutines.launch


@Composable
fun TransferEntryScreen(
    navigateBack: () -> Unit,
    viewModel: AccountTransferEntryViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val coroutineScope = rememberCoroutineScope()
    val accountsListState by viewModel.accountsListState.collectAsState()

    Scaffold(topBar = {
        SecondaryScreenTopBar(
            navigateBack = navigateBack,
            titleResId = R.string.entry_transfer_title
        )
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
        Row(verticalAlignment = Alignment.CenterVertically) {
            FloatOutlinedText(
                record = transfer,
                onValueChange = { _, newValue ->
                    onValueChange(transfer.copy(amountSource = newValue))
                },
                recordToId = { it.id },
                recordToFloat = { it.amountSource },
                modifier = Modifier.weight(1f)
            )
            Icon(
                imageVector = Icons.Filled.ArrowForward,
                contentDescription = "To",
                modifier = Modifier.padding(horizontal = 8.dp)
            )
            FloatOutlinedText(
                record = transfer,
                onValueChange = { _, newValue ->
                    onValueChange(transfer.copy(amountDestination = newValue))
                },
                recordToId = { it.id },
                recordToFloat = { it.amountDestination },
                modifier = Modifier.weight(1f)
            )
        }

        LargeDropdownMenu(
            label = stringResource(R.string.transfer_entry_source_account),
            items = availableAccounts.map {
                it.account.name
            },
            onItemSelected = { index, item ->
                onValueChange(
                    transfer.copy(sourceAccountId = availableAccounts[index].account.id)
                )
            },
            initialIndex = availableAccounts.indexOfFirst {
                it.account.id == transfer.sourceAccountId
            }
        )
        LargeDropdownMenu(
            label = stringResource(R.string.transfer_entry_destination_account),
            items = availableAccounts.map {
                it.account.name
            },
            onItemSelected = { index, item ->
                onValueChange(
                    transfer.copy(destinationAccountId = availableAccounts[index].account.id)
                )
            },
            initialIndex = availableAccounts.indexOfFirst {
                it.account.id == transfer.destinationAccountId
            }
        )

        DatePickerField(
            label = stringResource(id = R.string.entry_transaction_date),
            onDateChanged = { onValueChange(transfer.copy(date = it)) },
            date = transfer.date
        )
    }
}