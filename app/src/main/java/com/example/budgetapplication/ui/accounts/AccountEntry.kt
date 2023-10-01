package com.example.budgetapplication.ui.accounts

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
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.budgetapplication.R
import com.example.budgetapplication.data.accounts.Account
import com.example.budgetapplication.data.currencies.Currency
import com.example.budgetapplication.ui.AppViewModelProvider
import kotlinx.coroutines.launch
import androidx.compose.runtime.getValue
import com.example.budgetapplication.ui.components.LargeDropdownMenu
import java.time.LocalDateTime

@Composable
fun AccountEntryScreen(
    navigateBack: () -> Unit,
    viewModel: AccountsEntryViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val coroutineScope = rememberCoroutineScope()
    val currenciesListState by viewModel.currenciesListState.collectAsState()


    Scaffold(
        topBar = {
            Surface(
                Modifier
                    .height(dimensionResource(id = R.dimen.tab_height))
                    .fillMaxWidth()
            ) {
                Text(
                    text = stringResource(R.string.entry_account_title),
                    modifier = Modifier.padding(dimensionResource(id = R.dimen.medium))
                )
            }
        }
    ) { innerPadding ->
        AccountEntryBody(
            accountUiState = viewModel.accountUiState,
            onAccountValueChange = viewModel::updateUiState,
            onSaveClick = {
                coroutineScope.launch {
                    Log.d(
                        "AccountEntryScreen",
                        "Saving account: ${viewModel.accountUiState.account}"
                    )
                    viewModel.saveAccount()
                    navigateBack()
                }
            },
            availableCurrencies = currenciesListState,
            modifier = Modifier
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .fillMaxWidth()
        )
    }
}

@Composable
fun AccountEntryBody(
    accountUiState: AccountUiState,
    availableCurrencies: List<Currency>,
    onAccountValueChange: (Account) -> Unit,
    onSaveClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.large)),
        modifier = modifier.padding(dimensionResource(id = R.dimen.medium))
    ) {
        AccountForm(
            account = accountUiState.account,
            availableCurrencies = availableCurrencies,
            onValueChange = onAccountValueChange,
            modifier = Modifier.fillMaxWidth()
        )
        Button(
            onClick = onSaveClick,
            enabled = accountUiState.isValid,
            shape = MaterialTheme.shapes.small,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = stringResource(R.string.entry_account_save))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountForm(
    account: Account,
    availableCurrencies: List<Currency>,
    modifier: Modifier = Modifier,
    onValueChange: (Account) -> Unit = {},
    enabled: Boolean = true,
) {

    val currencyIndex = availableCurrencies.indexOfFirst { it.name == account.currency }

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.large))
    ) {
        OutlinedTextField(
            value = account.name,
            onValueChange = { onValueChange(account.copy(name = it)) },
            label = { Text(text = stringResource(R.string.entry_account_name)) },
            colors = TextFieldDefaults.outlinedTextFieldColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer
            ),
            modifier = Modifier.fillMaxWidth(),
            enabled = enabled,
            singleLine = true
        )
        OutlinedTextField(
            value = account.initialBalance.toString(),
            onValueChange = { onValueChange(account.copy(initialBalance = it.toFloat())) },
            label = { Text(text = stringResource(R.string.entry_account_initial_balance)) },
            colors = TextFieldDefaults.outlinedTextFieldColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer
            ),
            modifier = Modifier.fillMaxWidth(),
            enabled = enabled,
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )
        LargeDropdownMenu<String>(
            label = stringResource(id = R.string.entry_account_currency),
            items = availableCurrencies.map { it.name },
            onItemSelected = { index, item ->  onValueChange(account.copy(currency = item))},
            initialIndex = currencyIndex,
        )
    }
}


@Preview
@Composable
fun AccountFormPreview() {
    val account = Account(
        id = 0,
        name = "Account 1",
        initialBalance = 100f,
        currency = "USD",
        color = 0x000000
    )

    val availableCurrencies = listOf(
        Currency("USD", 1f, LocalDateTime.now()),
        Currency("EUR", 1.2f, LocalDateTime.now())
    )

    AccountForm(account = account, availableCurrencies = availableCurrencies)

}