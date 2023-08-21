package com.example.budgetapplication.ui.accounts

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.budgetapplication.data.accounts.AccountWithTransactions
import com.example.budgetapplication.ui.AppViewModelProvider
import com.example.budgetapplication.ui.components.BaseRow
import com.example.budgetapplication.ui.components.SummaryPage
import com.example.budgetapplication.ui.navigation.AccountDetails
import com.example.budgetapplication.ui.navigation.AccountEntry
import com.example.budgetapplication.ui.navigation.Accounts
import com.example.budgetapplication.ui.theme.InitialScreen


@Composable
fun AccountsSummary(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    viewModel: AccountsViewModel = viewModel(factory = AppViewModelProvider.Factory)
){
    InitialScreen(
        navController = navController,
        destination = Accounts,
        screenBody = {
            val accountsState by viewModel.accountsUiState.collectAsState()

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                AccountsSummaryBody(
                    accounts = accountsState.accountsList,
                    navController = navController
                )
            }
        },
        floatingButton = {
            FloatingActionButton(
                onClick = {
                    navController.navigate(AccountEntry.route)
                },
                modifier = Modifier.padding(16.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = "Add Account"
                )
            }
        }
    )
}

@Composable
fun AccountsSummaryBody(
    accounts: List<AccountWithTransactions>,
    navController: NavHostController
) {
    SummaryPage(
        items = accounts,
        rows = { accountWithTransactions ->
            AccountRow(
                accountWithTransactions,
                Color(accountWithTransactions.account.color),
                navController
            )
        },
    )
}

@Composable
private fun AccountRow(
    accountWithTransactions: AccountWithTransactions,
    color: Color,
    navController: NavHostController
){
    BaseRow(
        color = color,
        title = accountWithTransactions.account.name,
        subtitle = "",
        amount = accountWithTransactions.balance,
        currency = accountWithTransactions.account.currency,
        negative = false,
        onItemSelected = {
            navController.navigate("${AccountDetails.route}/${accountWithTransactions.account.id}")
        }
    )
}