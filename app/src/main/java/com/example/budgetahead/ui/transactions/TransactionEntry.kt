package com.example.budgetahead.ui.transactions

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.budgetahead.R
import com.example.budgetahead.data.accounts.Account
import com.example.budgetahead.data.categories.Category
import com.example.budgetahead.data.categories.CategoryType
import com.example.budgetahead.data.transactions.TransactionRecord
import com.example.budgetahead.data.transactions.TransactionType
import com.example.budgetahead.ui.AppViewModelProvider
import com.example.budgetahead.ui.components.DatePickerField
import com.example.budgetahead.ui.components.LargeDropdownMenu
import com.example.budgetahead.ui.components.inputs.FloatOutlinedText
import com.example.budgetahead.ui.navigation.SecondaryScreenTopBar
import com.example.budgetahead.use_cases.IconFromReIdUseCase
import java.time.LocalDateTime
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
        SecondaryScreenTopBar(
            navigateBack = navigateBack,
            titleResId = R.string.entry_transaction_title
        )
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
            modifier =
            Modifier
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
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.medium))
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            FloatOutlinedText(
                record = transactionRecord,
                onValueChange = { transactionRecord, newValue ->
                    onValueChange(transactionRecord.copy(amount = newValue))
                },
                recordToId = {
                    it.id
                },
                recordToFloat = { it.amount },
                modifier =
                Modifier
                    .weight(1.5f)
                    .padding(end = 8.dp)
            )
            DatePickerField(
                label = stringResource(id = R.string.entry_transaction_date),
                onDateChanged = { onValueChange(transactionRecord.copy(date = it)) },
                date = transactionRecord.date,
                modifier =
                Modifier
                    .weight(1f)
                    .padding(start = 8.dp)
            )
        }

        LargeDropdownMenu(
            label = stringResource(id = R.string.entry_transaction_account),
            items = availableAccounts.map { it.name },
            onItemSelected = { index, item ->
                onValueChange(transactionRecord.copy(accountId = availableAccounts[index].id))
            },
            initialIndex = availableAccounts.indexOfFirst { it.id == transactionRecord.accountId }
        )
        LargeDropdownMenu(
            label = stringResource(id = R.string.entry_transaction_category),
            items = availableCategories,
            onItemSelected = { index, item ->
                onValueChange(
                    transactionRecord.copy(
                        categoryId = availableCategories[index].id,
                        type =
                        if (item.defaultType ==
                            CategoryType.Expense
                        ) {
                            TransactionType.EXPENSE
                        } else {
                            TransactionType.INCOME
                        }
                    )
                )
            },
            selectedItemToString = {
                it.name
            },
            leadingIcon = {
                val iconResourceId =
                    IconFromReIdUseCase(LocalContext.current).getCategoryIconResId(
                        it.iconResId
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
                            CircleShape
                        ).background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f))
                )
            },
            initialIndex =
            availableCategories.indexOfFirst {
                it.id == transactionRecord.categoryId
            }
        )

        OutlinedTextField(
            value = transactionRecord.name,
            onValueChange = { onValueChange(transactionRecord.copy(name = it)) },
            label = { Text(text = stringResource(R.string.entry_transaction_name)) },
            colors =
            TextFieldDefaults.outlinedTextFieldColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer
            ),
            modifier = Modifier.fillMaxWidth(),
            enabled = true,
            singleLine = true
        )
    }
}

@Preview(showBackground = true)
@Composable
fun TransactionFormPreview() {
    val sampleAccount =
        Account(id = 1, name = "Bank Account", initialBalance = 1000f, currency = "USD")
    val sampleCategory =
        Category(
            id = 1,
            name = "Groceries",
            defaultType = CategoryType.Expense,
            parentCategoryId = null
        )
    val sampleTransaction =
        TransactionRecord(
            id = 1,
            name = "Grocery Shopping",
            type = TransactionType.EXPENSE,
            accountId = 1,
            categoryId = 1,
            amount = 150f,
            date = LocalDateTime.now()
        )

    TransactionForm(
        transactionRecord = sampleTransaction,
        availableAccounts = listOf(sampleAccount),
        availableCategories = listOf(sampleCategory),
        onValueChange = {}
    )
}
