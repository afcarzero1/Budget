package com.example.budgetapplication.ui.accounts

import androidx.compose.material3.FloatingActionButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.budgetapplication.data.accounts.AccountWithTransactions
import com.example.budgetapplication.ui.AppViewModelProvider
import com.example.budgetapplication.ui.components.BaseRow
import com.example.budgetapplication.ui.components.SummaryPage
import androidx.compose.runtime.getValue

@Composable
fun AccountsSummary(
    modifier: Modifier = Modifier,
    viewModel: AccountsViewModel = viewModel(factory = AppViewModelProvider.Factory)
){
    val accountsState by viewModel.accountsUiState.collectAsState()

    AccountsSummaryBody(accounts = accountsState.accountsList)
}




@Composable
fun AccountsSummaryBody(
    accounts: List<AccountWithTransactions>
) {
    SummaryPage(
        items = accounts,
        colors = { accountWithTransactions -> Color(accountWithTransactions.account.color)},
        amounts = {accountWithTransactions -> accountWithTransactions.balance },
        circleLabel = "Total Balance",
        rows = { accountWithTransactions ->
            AccountRow(accountWithTransactions, Color(accountWithTransactions.account.color))
        }
    )

}

@Composable
private fun AccountRow(
    accountWithTransactions: AccountWithTransactions,
    color: Color,
){
    BaseRow(
        color = color,
        title = accountWithTransactions.account.name,
        subtitle = "",
        amount = accountWithTransactions.balance,
        currency = accountWithTransactions.account.currency,
        negative = false
    )
}