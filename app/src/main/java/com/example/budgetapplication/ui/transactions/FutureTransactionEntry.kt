package com.example.budgetapplication.ui.transactions

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.budgetapplication.R
import com.example.budgetapplication.data.categories.Category
import com.example.budgetapplication.data.categories.CategoryType
import com.example.budgetapplication.data.currencies.Currency
import com.example.budgetapplication.data.future_transactions.FutureTransaction
import com.example.budgetapplication.data.future_transactions.RecurrenceType
import com.example.budgetapplication.data.transactions.TransactionType
import com.example.budgetapplication.ui.AppViewModelProvider
import com.example.budgetapplication.ui.components.DatePickerField
import com.example.budgetapplication.ui.components.LargeDropdownMenu
import com.example.budgetapplication.ui.components.inputs.FloatOutlinedText
import com.example.budgetapplication.ui.navigation.SecondaryScreenTopBar
import com.example.budgetapplication.use_cases.IconFromReIdUseCase
import kotlinx.coroutines.launch

@Composable
fun FutureTransactionEntryScreen(
    navigateBack: () -> Unit,
    viewModel: FutureTransactionEntryViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val coroutineScope = rememberCoroutineScope()
    val availableCategories by viewModel.categoriesListState.collectAsState()
    val availableCurrencies by viewModel.currenciesListState.collectAsState()

    Scaffold(
        topBar = {
            SecondaryScreenTopBar(
                navigateBack = navigateBack,
                titleResId = R.string.entry_transaction_title
            )
        }
    ) { paddingValue ->
        FutureTransactionEntryBody(
            futureTransactionUiState = viewModel.transactionUiState,
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
            },
            modifier = Modifier.padding(paddingValue)
        )
    }
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

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.medium))
    ) {

        Row {
            FloatOutlinedText(
                record = futureTransaction,
                onValueChange = { newTransaction, newValue ->
                    onValueChange(futureTransaction.copy(amount = newValue))
                },
                recordToId = {
                    it.id
                },
                recordToFloat = {
                    it.amount
                },
                modifier = Modifier.weight(1.5f)
            )
            Spacer(modifier = Modifier.width(16.dp))
            LargeDropdownMenu(
                label = stringResource(id = R.string.entry_future_transaction_currency),
                items = availableCurrencies.map { it.name },
                onItemSelected = { index, item ->
                    onValueChange(
                        futureTransaction.copy(
                            currency = availableCurrencies[index].name,
                        )
                    )
                },
                initialIndex = availableCurrencies.indexOfFirst { it.name == futureTransaction.currency },
                modifier = Modifier.weight(1f)
            )
        }



        LargeDropdownMenu(
            label = stringResource(id = R.string.entry_transaction_category),
            items = availableCategories,
            onItemSelected = { index, item ->
                onValueChange(
                    futureTransaction.copy(
                        categoryId = availableCategories[index].id,
                        type = if (item.defaultType == CategoryType.Expense)
                            TransactionType.EXPENSE else TransactionType.INCOME
                    )
                )
            },
            selectedItemToString = {
                it.name
            },
            leadingIcon = {
                val iconResourceId = IconFromReIdUseCase(LocalContext.current).getCategoryIconResId(
                    it.iconResId
                )
                Image(
                    painter = painterResource(id = iconResourceId),
                    contentDescription = "Category Icon",
                    modifier = Modifier
                        .size(30.dp)
                        .clip(CircleShape)
                        .border(
                            2.dp,
                            if (it.defaultType == CategoryType.Expense)
                                Color.Red.copy(alpha = 0.3f) else
                                Color.Green.copy(alpha = 0.3f),
                            CircleShape
                        )
                        .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f))
                )
            },
            initialIndex = availableCategories.indexOfFirst { it.id == futureTransaction.categoryId }
        )



        Row {
            DatePickerField(
                label = stringResource(id = R.string.entry_future_transaction_initial_date),
                onDateChanged = { onValueChange(futureTransaction.copy(startDate = it)) },
                date = futureTransaction.startDate,
                modifier = Modifier.weight(1f)
            )

            Spacer(modifier = Modifier.width(16.dp))

            DatePickerField(
                label = stringResource(id = R.string.entry_future_transaction_final_date),
                onDateChanged = { onValueChange(futureTransaction.copy(endDate = it)) },
                date = futureTransaction.endDate,
                modifier = Modifier.weight(1f)
            )
        }

        Row {
            LargeDropdownMenu(
                label = stringResource(id = R.string.entry_future_transaction_recurrence_type),
                items = enumValues<RecurrenceType>().toList(),
                onItemSelected = { index, item ->
                    onValueChange(
                        futureTransaction.copy(
                            recurrenceType = item
                        )
                    )
                },
                initialIndex = enumValues<RecurrenceType>().toList()
                    .indexOfFirst { it == futureTransaction.recurrenceType },
                modifier = Modifier.weight(1f)
            )

            if (futureTransaction.recurrenceType != RecurrenceType.NONE) {
                Spacer(modifier = Modifier.width(16.dp))
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
                    initialIndex = futureTransaction.recurrenceValue - 1,
                    modifier = Modifier.weight(1f)
                )
            }
        }


    }
}