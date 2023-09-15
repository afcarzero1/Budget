package com.example.budgetapplication.ui.transactions

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import com.example.budgetapplication.data.categories.Category
import com.example.budgetapplication.data.currencies.Currency
import com.example.budgetapplication.data.future_transactions.FutureTransaction
import com.example.budgetapplication.ui.AppViewModelProvider
import com.example.budgetapplication.ui.components.DatePickerField
import com.example.budgetapplication.ui.components.LargeDropdownMenu
import kotlinx.coroutines.launch

@Composable
fun FutureTransactionEntryScreen(
    navigateBack: () -> Unit,
    viewModel: FutureTransactionEntryViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val coroutineScope = rememberCoroutineScope()
    val availableCategories by viewModel.categoriesListState.collectAsState()
    val availableCurrencies by viewModel.currenciesListState.collectAsState()


    FutureTransactionEntryBody(futureTransactionUiState = viewModel.transactionUiState,
        availableCategories = availableCategories,
        availableCurrencies = availableCurrencies,
        onFutureTransactionValueChanged = {
            viewModel.updateUiState(it)
        },
        onSaveClick = {
            coroutineScope.launch {
                viewModel.saveTransaction()
                navigateBack()
            }
        })
}

@Composable
fun FutureTransactionEntryBody(
    futureTransactionUiState: FutureTransactionUiState,
    availableCategories: List<Category>,
    availableCurrencies: List<Currency>,
    onFutureTransactionValueChanged: (FutureTransaction) -> Unit,
    onSaveClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.large)),
        modifier = modifier.padding(dimensionResource(id = R.dimen.medium))
    ) {
        FutureTransactionForm(
            futureTransaction = futureTransactionUiState.futureTransaction,
            availableCategories = availableCategories,
            availableCurrencies = availableCurrencies,
            onValueChange = onFutureTransactionValueChanged
        )
        Button(
            onClick = onSaveClick,
            enabled = futureTransactionUiState.isValid,
            shape = MaterialTheme.shapes.small,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = stringResource(R.string.entry_transaction_save))
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FutureTransactionForm(
    futureTransaction: FutureTransaction,
    availableCategories: List<Category>,
    availableCurrencies: List<Currency>,
    onValueChange: (FutureTransaction) -> Unit,
    modifier: Modifier = Modifier
) {
    val recurrenceTypes = listOf("None", "Weekly", "Monthly", "Yearly")

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.medium))
    ) {
        OutlinedTextField(value = futureTransaction.amount.toString(),
            onValueChange = {
                onValueChange(futureTransaction.copy(amount = it.toFloat()))
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

        LargeDropdownMenu(label = stringResource(id = R.string.entry_transaction_category),
            items = availableCategories.map { it.name },
            onItemSelected = { index, item ->
                onValueChange(
                    futureTransaction.copy(
                        categoryId = availableCategories[index].id,
                    )
                )
            },
            initialIndex = availableCategories.indexOfFirst { it.id == futureTransaction.categoryId }
        )

        LargeDropdownMenu(label = stringResource(id = R.string.entry_future_transaction_currency),
            items = availableCurrencies.map { it.name },
            onItemSelected = { index, item ->
                onValueChange(
                    futureTransaction.copy(
                        currency = availableCurrencies[index].name,
                    )
                )
            },
            initialIndex = availableCategories.indexOfFirst { it.name == futureTransaction.currency }
        )

        LargeDropdownMenu(
            label = stringResource(id = R.string.entry_category_type),
            items = listOf("Expense", "Income"),
            onItemSelected = { index, item -> onValueChange(futureTransaction.copy(type = item)) },
            initialIndex = if (futureTransaction.type == "Expense") 0 else 1
        )

        DatePickerField(
            label = stringResource(id = R.string.entry_future_transaction_initial_date),
            onDateChanged = { onValueChange(futureTransaction.copy(startDate = it)) },
            initialDate = futureTransaction.startDate
        )

        DatePickerField(
            label = stringResource(id = R.string.entry_future_transaction_final_date),
            onDateChanged = { onValueChange(futureTransaction.copy(endDate = it)) },
            initialDate = futureTransaction.endDate
        )

        LargeDropdownMenu(
            label = stringResource(id = R.string.entry_future_transaction_recurrence_type),
            items = recurrenceTypes,
            onItemSelected = { index, item -> onValueChange(futureTransaction.copy(recurrenceType = item)) },
            initialIndex = recurrenceTypes.indexOfFirst { it == futureTransaction.recurrenceType }
        )

        if (futureTransaction.recurrenceType != "None") {
            LargeDropdownMenu(
                label = stringResource(id = R.string.entry_future_transaction_recurrence_value),
                items = listOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12"),
                onItemSelected = { index, item ->
                    onValueChange(
                        futureTransaction.copy(
                            recurrenceValue = item.toInt()
                        )
                    )
                },
            )
        }
    }
}