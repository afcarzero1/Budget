package com.example.budgetapplication.ui.transactions

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.budgetapplication.data.transactions.FullTransactionRecord
import com.example.budgetapplication.ui.AppViewModelProvider
import com.example.budgetapplication.ui.components.BaseRow
import com.example.budgetapplication.ui.components.ListDivider
import com.example.budgetapplication.ui.navigation.AccountEntry
import com.example.budgetapplication.ui.navigation.Accounts
import com.example.budgetapplication.ui.navigation.TransactionEntry
import com.example.budgetapplication.ui.navigation.Transactions
import com.example.budgetapplication.ui.theme.InitialScreen

@Composable
fun TransactionsSummary(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    viewModel: TransactionsSummaryViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    InitialScreen(
        navController = navController,
        destination = Transactions,
        screenBody = {
            val transactionsState by viewModel.transactionsUiState.collectAsState()

            if (transactionsState.transactionsList.isEmpty()) {
                EmptyTransactionScreen()
            } else {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    TransactionsSummaryBody(
                        transactions = transactionsState.transactionsList,
                        navController = navController
                    )
                }
            }

        },
        floatingButton = {
            FloatingActionButton(
                onClick = {
                    navController.navigate(TransactionEntry.route)
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
fun TransactionsSummaryBody(
    transactions: List<FullTransactionRecord>,
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
    ) {
        transactions.forEach { transaction ->
            TransactionRow(
                transaction = transaction,
                onItemSelected = {
                    TODO("Not yet implemented")
                }
            )
            ListDivider()
        }
    }
}

@Composable
fun EmptyTransactionScreen() {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "No transactions yet. Create one by clicking the + button",
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Composable
private fun TransactionRow(
    transaction: FullTransactionRecord,
    onItemSelected: (FullTransactionRecord) -> Unit = {},
) {
    BaseRow(
        color = Color(0xFFE0E0E0),
        title = transaction.transactionRecord.name,
        subtitle = transaction.category.name,
        amount = transaction.transactionRecord.amount,
        currency = transaction.account.currency,
        negative = transaction.transactionRecord.type == "Expense",
        holdedItem = transaction,
        onItemSelected = onItemSelected
    )
}

