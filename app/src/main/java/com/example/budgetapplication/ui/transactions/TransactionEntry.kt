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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import com.example.budgetapplication.R
import com.example.budgetapplication.data.accounts.Account
import com.example.budgetapplication.data.categories.Category
import com.example.budgetapplication.data.transactions.TransactionRecord
import com.example.budgetapplication.data.transactions.TransactionType
import com.example.budgetapplication.ui.accounts.AccountEntryBody
import com.example.budgetapplication.ui.accounts.AccountForm
import com.example.budgetapplication.ui.components.DatePickerField
import com.example.budgetapplication.ui.components.LargeDropdownMenu
import kotlinx.coroutines.launch

@Composable
fun TransactionEntryScreen(
    navigateBack: () -> Unit,
    viewModel: TransactionEntryViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val coroutineScore = rememberCoroutineScope()
    val accountsListState by viewModel.accountsListState.collectAsState()
    val categoriesListState by viewModel.categoriesListState.collectAsState()


    Scaffold(topBar = {
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
    }) { innerPadding ->

        TransactionEntryBody(
            transactionUiState = viewModel.transactionUiState,
            availableAccounts = accountsListState,
            availableCategories = categoriesListState,
            onTransactionValueChanged = {
                Log.d("TransactionEntryScreen", "TransactionEntryScreen: $it")
                viewModel.updateUiState(it)
            },
            onSaveClick = {
                coroutineScore.launch {
                    viewModel.saveTransaction()
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
fun TransactionEntryBody(
    transactionUiState: TransactionUiState,
    availableAccounts: List<Account>,
    availableCategories: List<Category>,
    onTransactionValueChanged: (TransactionRecord) -> Unit,
    onSaveClick: () -> Unit,
    modifier: Modifier = Modifier
) {
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
) {
    Log.d("TransactionForm", "TransactionForm: ${transactionRecord.id}")
    var text by remember(transactionRecord.amount) { mutableStateOf(transactionRecord.amount.toString()) }
    var supportText by remember { mutableStateOf("") }
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.medium))
    ) {

        OutlinedTextField(
            value = transactionRecord.amount.toString(),
            onValueChange = {
                text = it
            },
            label = { Text(text = stringResource(R.string.entry_transaction_amount)) },
            colors = TextFieldDefaults.outlinedTextFieldColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer
            ),
            modifier = Modifier
                .fillMaxWidth()
                .onFocusChanged { focusState ->
                    if (!focusState.isFocused) {
                        try {
                            val parsedFloat = text.toFloat()
                            onValueChange(transactionRecord.copy(amount = parsedFloat))
                            supportText = "" // Clear any previous error message
                        } catch (e: NumberFormatException) {
                            // Handle the error case
                            text = transactionRecord.amount.toString() // Revert to the previous valid value
                            supportText = "Please enter a valid number."
                        }
                    }
                },
            enabled = true,
            singleLine = true,
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
            isError = supportText.isNotEmpty() // Show error styling when there's an error message
        )
        LargeDropdownMenu(
            label = stringResource(id = R.string.entry_transaction_account),
            items = availableAccounts.map { it.name },
            onItemSelected = { index, item -> onValueChange(transactionRecord.copy(accountId = availableAccounts[index].id)) },
            initialIndex = availableAccounts.indexOfFirst { it.id == transactionRecord.accountId }
        )
        LargeDropdownMenu(label = stringResource(id = R.string.entry_transaction_category),
            items = availableCategories.map { it.name },
            onItemSelected = { index, item ->
                onValueChange(
                    transactionRecord.copy(
                        categoryId = availableCategories[index].id,
                    )
                )
            },
            initialIndex = availableCategories.indexOfFirst { it.id == transactionRecord.categoryId }
        )

        //TODO : make this radio button for adding transfers
        LargeDropdownMenu(
            label = stringResource(id = R.string.entry_category_type),
            items = enumValues<TransactionType>().toList(),
            onItemSelected = { index, item -> onValueChange(transactionRecord.copy(type = item)) },
            initialIndex = if (transactionRecord.type == TransactionType.EXPENSE) 0 else 1
        )

        DatePickerField(
            label = stringResource(id = R.string.entry_transaction_date),
            onDateChanged = { onValueChange(transactionRecord.copy(date = it)) },
            initialDate = transactionRecord.date
        )

        OutlinedTextField(
            value = transactionRecord.name,
            onValueChange = { onValueChange(transactionRecord.copy(name = it)) },
            label = { Text(text = stringResource(R.string.entry_transaction_name)) },
            colors = TextFieldDefaults.outlinedTextFieldColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer
            ),
            modifier = Modifier.fillMaxWidth(),
            enabled = true,
            singleLine = true,
        )

    }
}