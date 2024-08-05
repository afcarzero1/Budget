package com.example.budgetahead.ui.accounts

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.budgetahead.R
import com.example.budgetahead.data.accounts.Account
import com.example.budgetahead.data.accounts.AccountWithTransactions
import com.example.budgetahead.data.currencies.Currency
import com.example.budgetahead.ui.AppViewModelProvider
import com.example.budgetahead.ui.components.graphics.AvailableColors
import com.example.budgetahead.ui.components.graphics.convertColorToLong
import com.example.budgetahead.ui.components.graphics.convertLongToColor
import com.example.budgetahead.ui.navigation.AccountDetails
import com.example.budgetahead.ui.navigation.SecondaryScreenTopBar
import com.example.budgetahead.ui.transactions.GroupOfTransactionsAndTransfers
import com.example.budgetahead.ui.transactions.TransactionsSummaryBody
import java.time.LocalDateTime

@Composable
fun AccountSummaryScreen(
    navController: NavHostController,
    viewModel: AccountSummaryViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val accountUiState by viewModel.accountState.collectAsState()
    val baseCurrency by viewModel.baseCurrency.collectAsState()

    Scaffold(
        topBar = {
            SecondaryScreenTopBar(
                navigateBack = { navController.popBackStack() },
                titleResId = null,
                actions = {
                    IconButton(
                        modifier = Modifier.padding(horizontal = 8.dp),
                        onClick = {
                            navController.navigate(
                                "${AccountDetails.route}/${accountUiState.accountWithTransactions.account.id}"
                            )
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Edit,
                            contentDescription = stringResource(R.string.edit),
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                },
                containerColor =
                convertLongToColor(
                    accountUiState.accountWithTransactions.account.color
                ).copy(alpha = 0.8f)
            )
        }
    ) {
        AccountSummaryBody(
            accountWithTransactions = accountUiState.accountWithTransactions,
            accountCurrency = accountUiState.currency,
            baseCurrency = baseCurrency,
            transactionsAndTransfers = accountUiState.transactionsAndTransfers,
            navController = navController,
            modifier =
            Modifier
                .padding(it)
                .fillMaxSize()
        )
    }
}

@Composable
fun AccountSummaryBody(
    accountWithTransactions: AccountWithTransactions,
    accountCurrency: Currency,
    baseCurrency: String,
    transactionsAndTransfers: List<GroupOfTransactionsAndTransfers>,
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    val backgroundColor =
        convertLongToColor(
            accountWithTransactions.account.color
        ).copy(alpha = 0.15f)
    Column(
        modifier =
        modifier.background(
            backgroundColor
        )
    ) {
        Column(
            modifier =
            Modifier
                .fillMaxWidth()
                .padding(
                    bottom = 16.dp
                ).background(
                    color =
                    convertLongToColor(
                        accountWithTransactions.account.color
                    ).copy(alpha = 0.8f)
                ),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                modifier = Modifier.padding(horizontal = 16.dp),
                text = accountWithTransactions.account.name,
                color = MaterialTheme.colorScheme.onSecondary,
                style = MaterialTheme.typography.titleLarge
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                modifier = Modifier.padding(horizontal = 16.dp),
                text =
                accountCurrency.formatAmount(
                    accountWithTransactions.balance
                ),
                color = MaterialTheme.colorScheme.onSecondary,
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold)
            )

            Spacer(modifier = Modifier.height(32.dp))
        }

        Spacer(modifier = Modifier.height(8.dp))

        TransactionsSummaryBody(
            transactions = transactionsAndTransfers,
            baseCurrency = baseCurrency,
            navController = navController,
            modifier = Modifier.padding(horizontal = 16.dp),
            dividerColor = backgroundColor
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewAccountSummaryBody() {
    AccountSummaryBody(
        accountWithTransactions =
        AccountWithTransactions(
            account =
            Account(
                id = 0,
                name = "Bank Account",
                currency = "EUR",
                initialBalance = 0f,
                color = convertColorToLong(AvailableColors.colorsList[1]),
                hidden = false
            ),
            transactionRecords = listOf()
        ),
        accountCurrency =
        Currency(
            name = "EUR",
            value = 1.09f,
            updatedTime = LocalDateTime.now()
        ),
        baseCurrency = "USD",
        transactionsAndTransfers = listOf(),
        navController = rememberNavController()
    )
}
