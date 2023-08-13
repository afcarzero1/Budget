package com.example.budgetapplication.ui.accounts

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.example.budgetapplication.data.accounts.AccountWithTransactions
import com.example.budgetapplication.ui.components.BaseRow
import com.example.budgetapplication.ui.components.SummaryPage

@Composable
fun AccountsSummary(
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