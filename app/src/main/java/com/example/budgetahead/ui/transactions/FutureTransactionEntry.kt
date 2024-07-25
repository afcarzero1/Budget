package com.example.budgetahead.ui.transactions

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
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.budgetahead.R
import com.example.budgetahead.data.categories.Category
import com.example.budgetahead.data.categories.CategoryType
import com.example.budgetahead.data.currencies.Currency
import com.example.budgetahead.data.future_transactions.FutureTransaction
import com.example.budgetahead.data.future_transactions.RecurrenceType
import com.example.budgetahead.data.future_transactions.RecurrenceTypeDescriptions
import com.example.budgetahead.data.transactions.TransactionType
import com.example.budgetahead.ui.AppViewModelProvider
import com.example.budgetahead.ui.components.DatePickerField
import com.example.budgetahead.ui.components.LargeDropdownMenu
import com.example.budgetahead.ui.components.inputs.FloatOutlinedText
import com.example.budgetahead.ui.navigation.SecondaryScreenTopBar
import com.example.budgetahead.use_cases.IconFromReIdUseCase
import kotlinx.coroutines.launch
import java.time.LocalDateTime

@Composable
fun FutureTransactionEntryScreen(
    navigateBack: () -> Unit,
    viewModel: FutureTransactionEntryViewModel = viewModel(factory = AppViewModelProvider.Factory),
) {
    val coroutineScope = rememberCoroutineScope()
    val availableCategories by viewModel.categoriesListState.collectAsState()
    val availableCurrencies by viewModel.currenciesListState.collectAsState()

    Scaffold(
        topBar = {
            SecondaryScreenTopBar(
                navigateBack = navigateBack,
                titleResId = R.string.entry_transaction_title,
            )
        },
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
            modifier = Modifier.padding(paddingValue),
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
    modifier: Modifier = Modifier,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.large)),
        modifier = modifier.padding(dimensionResource(id = R.dimen.medium)),
    ) {
        FutureTransactionForm(
            futureTransaction = futureTransactionUiState.futureTransaction,
            availableCategories = availableCategories,
            availableCurrencies = availableCurrencies,
            onValueChange = onFutureTransactionValueChanged,
        )
        Button(
            onClick = onSaveClick,
            enabled = futureTransactionUiState.isValid,
            shape = MaterialTheme.shapes.small,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(text = stringResource(R.string.entry_transaction_save))
        }
    }
}

@Composable
fun FutureTransactionForm(
    futureTransaction: FutureTransaction,
    availableCategories: List<Category>,
    availableCurrencies: List<Currency>,
    onValueChange: (FutureTransaction) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.medium)),
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
                modifier = Modifier.weight(1.5f),
            )
            Spacer(modifier = Modifier.width(16.dp))
            LargeDropdownMenu(
                label = stringResource(id = R.string.entry_future_transaction_currency),
                items = availableCurrencies.map { it.name },
                onItemSelected = { index, item ->
                    onValueChange(
                        futureTransaction.copy(
                            currency = availableCurrencies[index].name,
                        ),
                    )
                },
                initialIndex =
                    availableCurrencies.indexOfFirst {
                        it.name ==
                            futureTransaction.currency
                    },
                modifier = Modifier.weight(1f),
            )
        }

        LargeDropdownMenu(
            label = stringResource(id = R.string.entry_transaction_category),
            items = availableCategories,
            onItemSelected = { index, item ->
                onValueChange(
                    futureTransaction.copy(
                        categoryId = availableCategories[index].id,
                        type =
                            if (item.defaultType == CategoryType.Expense) {
                                TransactionType.EXPENSE
                            } else {
                                TransactionType.INCOME
                            },
                    ),
                )
            },
            selectedItemToString = {
                it.name
            },
            leadingIcon = {
                val iconResourceId =
                    IconFromReIdUseCase(LocalContext.current).getCategoryIconResId(
                        it.iconResId,
                    )
                Image(
                    painter = painterResource(id = iconResourceId),
                    contentDescription = "Category Icon",
                    modifier =
                        Modifier
                            .size(30.dp)
                            .clip(CircleShape)
                            .border(
                                2.dp,
                                if (it.defaultType == CategoryType.Expense) {
                                    Color.Red.copy(alpha = 0.3f)
                                } else {
                                    Color.Green.copy(alpha = 0.3f)
                                },
                                CircleShape,
                            ).background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f)),
                )
            },
            initialIndex =
                availableCategories.indexOfFirst {
                    it.id == futureTransaction.categoryId
                },
        )

        Row(modifier = Modifier.fillMaxWidth()) {
            DatePickerField(
                label = stringResource(id = R.string.entry_future_transaction_initial_date),
                onDateChanged = { onValueChange(futureTransaction.copy(startDate = it)) },
                date = futureTransaction.startDate,
                modifier = Modifier.weight(1f),
            )

            if (futureTransaction.recurrenceType != RecurrenceType.NONE) {
                Spacer(modifier = Modifier.width(16.dp))
                DatePickerField(
                    label = stringResource(id = R.string.entry_future_transaction_final_date),
                    onDateChanged = { onValueChange(futureTransaction.copy(endDate = it)) },
                    date = futureTransaction.endDate,
                    modifier = Modifier.weight(1f),
                )
            }
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth(),
        ) {
            RadioButton(
                selected = futureTransaction.recurrenceType == RecurrenceType.NONE,
                onClick = {
                    onValueChange(futureTransaction.copy(recurrenceType = RecurrenceType.NONE))
                },
            )
            Text(
                text = "No repetition.",
                style = MaterialTheme.typography.bodyMedium,
            )
        }

        val repeatedSelected = !futureTransaction.recurrenceType.isContinuous() && futureTransaction.recurrenceType != RecurrenceType.NONE

        Column {
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                RadioButton(
                    selected = repeatedSelected,
                    onClick = {
                        if (!repeatedSelected) {
                            onValueChange(
                                futureTransaction.copy(recurrenceType = RecurrenceType.DAILY),
                            )
                        }
                    },
                )
                Text(
                    text = "Repeated at specific dates.",
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
            if (repeatedSelected) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Every ",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.weight(1f).padding(start = 32.dp),
                    )

                    Spacer(modifier = Modifier.width(16.dp))

                    LargeDropdownMenu(
                        label = stringResource(id = R.string.entry_future_transaction_recurrence_value),
                        items = listOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12"),
                        onItemSelected = { index, item ->
                            onValueChange(
                                futureTransaction.copy(
                                    recurrenceValue = item.toInt(),
                                ),
                            )
                        },
                        initialIndex = futureTransaction.recurrenceValue - 1,
                        modifier = Modifier.weight(1f),
                    )

                    Spacer(modifier = Modifier.width(16.dp))

                    val validSelections =  enumValues<RecurrenceType>().toList().filter { it != RecurrenceType.NONE && !it.isContinuous() }

                    LargeDropdownMenu(
                        label = stringResource(id = R.string.entry_future_transaction_recurrence_type),
                        items = validSelections,
                        onItemSelected = { index, item ->
                            onValueChange(
                                futureTransaction.copy(
                                    recurrenceType = item,
                                ),
                            )
                        },
                        initialIndex =
                            validSelections
                                .indexOfFirst { it == futureTransaction.recurrenceType },
                        modifier = Modifier.weight(1.3f),
                        selectedItemToString = {
                            if (futureTransaction.recurrenceValue == 1) {
                                RecurrenceTypeDescriptions.descriptions[it]!!.removeSuffix("s")
                            } else {
                                RecurrenceTypeDescriptions.descriptions[it]!!
                            }
                        },
                    )
                }
            }
        }

        val continuousSelected = futureTransaction.recurrenceType.isContinuous()

        Column {
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                RadioButton(
                    selected = futureTransaction.recurrenceType.isContinuous(),
                    onClick = {
                        if (!continuousSelected) {
                            onValueChange(futureTransaction.copy(recurrenceType = RecurrenceType.MONTHLY_CONTINUOUS))
                        }
                    },
                )
                Text(
                    text = "Spread across periods.",
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
            if (continuousSelected) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Of length ",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.weight(1f).padding(start = 32.dp),
                    )

                    Spacer(modifier = Modifier.width(16.dp))

                    LargeDropdownMenu(
                        label = stringResource(id = R.string.entry_future_transaction_recurrence_value),
                        items = listOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12"),
                        onItemSelected = { index, item ->
                            onValueChange(
                                futureTransaction.copy(
                                    recurrenceValue = item.toInt(),
                                ),
                            )
                        },
                        initialIndex = futureTransaction.recurrenceValue - 1,
                        modifier = Modifier.weight(1f),
                    )

                    Spacer(modifier = Modifier.width(16.dp))

                    val validSelections = enumValues<RecurrenceType>().toList().filter { it.isContinuous() }

                    LargeDropdownMenu(
                        label = stringResource(id = R.string.entry_future_transaction_recurrence_type),
                        items = validSelections,
                        onItemSelected = { index, item ->
                            onValueChange(
                                futureTransaction.copy(
                                    recurrenceType = item,
                                ),
                            )
                        },
                        initialIndex =
                            validSelections
                                .indexOfFirst { it == futureTransaction.recurrenceType },
                        modifier = Modifier.weight(1.3f),
                        selectedItemToString = {
                            if (futureTransaction.recurrenceValue == 1) {
                                RecurrenceTypeDescriptions.descriptions[it]!!.removeSuffix("s")
                            } else {
                                RecurrenceTypeDescriptions.descriptions[it]!!
                            }
                        },
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewFutureTransactionEntry() {
    FutureTransactionForm(
        futureTransaction =
            FutureTransaction(
                name = "Scholarship",
                categoryId = 0,
                amount = 1000f,
                currency = "USD",
                recurrenceType = RecurrenceType.WEEKLY,
                recurrenceValue = 1,
                type = TransactionType.EXPENSE,
                startDate = LocalDateTime.parse("2024-10-01T12:00:00"),
                endDate = LocalDateTime.parse("2024-11-30T12:00:00"),
                id = 0,
            ),
        availableCategories =
            listOf(
                Category(
                    id = 1,
                    name = "Food",
                    defaultType = CategoryType.Expense,
                    parentCategoryId = null,
                ),
                Category(
                    id = 2,
                    name = "Transportation",
                    defaultType = CategoryType.Expense,
                    parentCategoryId = null,
                ),
                Category(
                    id = 3,
                    name = "Rent",
                    defaultType = CategoryType.Expense,
                    parentCategoryId = null,
                ),
                Category(
                    id = 4,
                    name = "Salary",
                    defaultType = CategoryType.Income,
                    parentCategoryId = null,
                ),
            ),
        availableCurrencies =
            listOf(
                Currency("USD", 1.0f, LocalDateTime.now()),
                Currency("EUR", 1 / 1.1f, LocalDateTime.now()),
                Currency("SEK", 1 / 0.1f, LocalDateTime.now()),
            ),
        onValueChange = {},
    )
}

@Preview(showBackground = true)
@Composable
fun PreviewFutureTransactionEntryContinuous() {
    FutureTransactionForm(
        futureTransaction =
            FutureTransaction(
                name = "Scholarship",
                categoryId = 0,
                amount = 1000f,
                currency = "USD",
                recurrenceType = RecurrenceType.WEEKLY_CONTINUOUS,
                recurrenceValue = 1,
                type = TransactionType.EXPENSE,
                startDate = LocalDateTime.parse("2024-10-01T12:00:00"),
                endDate = LocalDateTime.parse("2024-11-30T12:00:00"),
                id = 0,
            ),
        availableCategories =
            listOf(
                Category(
                    id = 1,
                    name = "Food",
                    defaultType = CategoryType.Expense,
                    parentCategoryId = null,
                ),
                Category(
                    id = 2,
                    name = "Transportation",
                    defaultType = CategoryType.Expense,
                    parentCategoryId = null,
                ),
                Category(
                    id = 3,
                    name = "Rent",
                    defaultType = CategoryType.Expense,
                    parentCategoryId = null,
                ),
                Category(
                    id = 4,
                    name = "Salary",
                    defaultType = CategoryType.Income,
                    parentCategoryId = null,
                ),
            ),
        availableCurrencies =
            listOf(
                Currency("USD", 1.0f, LocalDateTime.now()),
                Currency("EUR", 1 / 1.1f, LocalDateTime.now()),
                Currency("SEK", 1 / 0.1f, LocalDateTime.now()),
            ),
        onValueChange = {},
    )
}
