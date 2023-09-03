package com.example.budgetapplication.ui.transactions

import android.util.Log
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.budgetapplication.ui.AppViewModelProvider
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import com.example.budgetapplication.R
import com.example.budgetapplication.data.accounts.Account
import com.example.budgetapplication.data.categories.Category
import com.example.budgetapplication.data.transactions.TransactionRecord
import com.example.budgetapplication.ui.accounts.AccountEntryBody
import com.example.budgetapplication.ui.accounts.AccountForm
import com.example.budgetapplication.ui.components.LargeDropdownMenu
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionEntryScreen(
    navigateBack: () -> Unit,
    viewModel: TransactionEntryViewModel = viewModel(factory = AppViewModelProvider.Factory)
){
    val coroutineScore = rememberCoroutineScope()
    val accountsListState by viewModel.accountsListState.collectAsState()
    val categoriesListState by viewModel.categoriesListState.collectAsState()


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

    }
}

@Composable
fun TransactionEntryBody(
    transactionUiState: TransactionUiState,
    availableAccounts: List<Account>,
    availableCategories: List<Category>,
    onTransactionValueChanged: (TransactionRecord) -> Unit,
    onSaveClick: () -> Unit,
    modifier: Modifier = Modifier
){
    Column(
        verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.large)),
        modifier = modifier.padding(dimensionResource(id = R.dimen.medium))
    ) {
        TransactionForm(
            transactionRecord = transactionUiState.transaction,
            availableAccounts = availableAccounts,
            availableCategories = availableCategories,
            onValueChange = onTransactionValueChanged,
            modifier = Modifier.fillMaxWidth()
        )
        Button(
            onClick = onSaveClick,
            enabled = transactionUiState.isValid,
            shape = MaterialTheme.shapes.small,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = stringResource(R.string.entry_transaction_save))
        }
    }

}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionForm(
    transactionRecord: TransactionRecord,
    availableAccounts: List<Account>,
    availableCategories: List<Category>,
    onValueChange: (TransactionRecord) -> Unit,
    modifier: Modifier = Modifier
){

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.medium))
    ) {
        OutlinedTextField(
            value = transactionRecord.amount.toString(),
            onValueChange = { onValueChange(transactionRecord.copy(amount = it.toFloat())) },
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
            label = stringResource(id = R.string.entry_transaction_account),
            items = availableAccounts.map { it.name },
            onItemSelected = {index, item -> onValueChange(transactionRecord.copy(accountId = availableAccounts[index].id)) }
        )

        LargeDropdownMenu(
            label = stringResource(id = R.string.entry_transaction_category),
            items = availableAccounts.map { it.name },
            onItemSelected = {index, item -> onValueChange(transactionRecord.copy(categoryId = availableCategories[index].id))}
        )




    }
}