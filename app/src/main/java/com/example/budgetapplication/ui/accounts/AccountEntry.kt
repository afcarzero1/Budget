package com.example.budgetapplication.ui.accounts

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.budgetapplication.ui.components.LargeDropdownMenu
import com.example.budgetapplication.ui.components.graphics.AvailableColors
import com.example.budgetapplication.ui.components.graphics.ColorPicker
import com.example.budgetapplication.ui.components.graphics.convertColorToLong
import com.example.budgetapplication.ui.components.graphics.convertLongToColor
import com.example.budgetapplication.ui.components.inputs.FloatOutlinedText
import com.example.budgetapplication.ui.navigation.SecondaryScreenTopBar
import java.time.LocalDateTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountEntryScreen(
    navigateBack: () -> Unit,
    viewModel: AccountsEntryViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val coroutineScope = rememberCoroutineScope()
    val currenciesListState by viewModel.currenciesListState.collectAsState()


    Scaffold(
        topBar = {
            SecondaryScreenTopBar(
                navigateBack = navigateBack,
                titleResId = R.string.entry_account_title
            )
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

    Surface(
        color = MaterialTheme.colorScheme.background,
        modifier = Modifier.fillMaxSize()
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
                shape = MaterialTheme.shapes.large,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = stringResource(R.string.entry_account_save))
            }
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

    val colors = OutlinedTextFieldDefaults.colors(
        focusedContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
        unfocusedContainerColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.05f),
        disabledContainerColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f),
    )
    Log.d("AccountForm", convertLongToColor(account.color).toString())
    Column(
        modifier = modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.large))
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            OutlinedTextField(
                value = account.name,
                onValueChange = { onValueChange(account.copy(name = it)) },
                label = { Text(text = stringResource(R.string.entry_account_name)) },
                colors = colors,
                modifier = Modifier.padding(end = 16.dp),
                enabled = enabled,
                singleLine = true
            )
            ColorPicker(
                color = convertLongToColor(account.color),
                options = AvailableColors.colorsList,
                onColorChanged = {
                    onValueChange(account.copy(color = convertColorToLong(it)))
                },
                modifier = Modifier
                    .padding(top = 8.dp, bottom = 4.dp)
            )
        }
        FloatOutlinedText(
            record = account,
            label = { Text("Initial Balance") },
            onValueChange = { account: Account, newAmount: Float ->
                onValueChange(account.copy(initialBalance = newAmount))
            },
            recordToId = {
                it.id
            },
            recordToFloat = {
                it.initialBalance
            },
            modifier = Modifier.fillMaxWidth(),
            colors = colors
        )
        LargeDropdownMenu(
            label = stringResource(id = R.string.entry_account_currency),
            items = availableCurrencies.map { it.name },
            onItemSelected = { index, item -> onValueChange(account.copy(currency = item)) },
            initialIndex = currencyIndex,
            colors = colors
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