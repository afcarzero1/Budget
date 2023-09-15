package com.example.budgetapplication.ui.transactions

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.budgetapplication.data.future_transactions.FullFutureTransaction
import com.example.budgetapplication.data.transactions.FullTransactionRecord
import com.example.budgetapplication.ui.AppViewModelProvider
import com.example.budgetapplication.ui.components.BaseRow
import com.example.budgetapplication.ui.components.ListDivider
import com.example.budgetapplication.ui.navigation.FutureTransactionEntry
import com.example.budgetapplication.ui.navigation.TransactionDetails
import com.example.budgetapplication.ui.navigation.TransactionEntry
import com.example.budgetapplication.ui.navigation.Transactions
import com.example.budgetapplication.ui.theme.InitialScreen
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun TransactionsSummary(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    transactionsViewModel: TransactionsSummaryViewModel = viewModel(factory = AppViewModelProvider.Factory),
    futureTransactionsViewModel: FutureTransactionsSummaryViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    var showFutureTransactions by remember { mutableStateOf(false) }


    InitialScreen(navController = navController, destination = Transactions, screenBody = {
        val transactionsState by transactionsViewModel.transactionsUiState.collectAsState()
        val futureTransactionsState by futureTransactionsViewModel.futureTransactionsUiState.collectAsState()

        // Selector for showing future transactions
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
            Button(
                onClick = { showFutureTransactions = false },
                enabled = showFutureTransactions,
                modifier = Modifier
                    .padding(end = 8.dp)
                    .weight(1f)
            ) {
                Text(text = "Present")
            }
            Button(
                onClick = { showFutureTransactions = true },
                enabled = !showFutureTransactions,
                modifier = Modifier
                    .padding(start = 8.dp)
                    .weight(1f)
            ) {
                Text(text = "Future")
            }
        }

        if (showFutureTransactions) {
            if (futureTransactionsState.futureTransactionsList.isEmpty()) {
                EmptyTransactionScreen()
            } else {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    FutureTransactionsSummaryBody(
                        futureTransactions = futureTransactionsState.futureTransactionsList,
                        navController = navController
                    )
                }
            }
        } else {
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
        }
    }, floatingButton = {
        FloatingActionButton(
            onClick = {
                if (showFutureTransactions) {
                    navController.navigate(FutureTransactionEntry.route)
                } else {
                    navController.navigate(TransactionEntry.route)
                }
            },
            modifier = Modifier.padding(16.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.Add, contentDescription = "Add Transaction"
            )
        }
    })
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
            TransactionRow(transaction = transaction, onItemSelected = {
                navController.navigate(
                    TransactionDetails.route + "/${it.transactionRecord.id}"
                )
            })
            ListDivider()
        }
    }
}


@Composable
fun FutureTransactionsSummaryBody(
    futureTransactions: List<FullFutureTransaction>,
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
    ) {
        futureTransactions.forEach { transaction ->
            FutureTransactionRow(futureTransaction = transaction, onItemSelected = {
                TODO("Navigate to future transaction detail")
            })
            ListDivider()
        }
    }
}

@Composable
fun EmptyTransactionScreen() {
    Column(
        modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.Center
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
    val formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy", Locale.getDefault())

    val isExpense = transaction.transactionRecord.type == "Expense"
    val color = if (isExpense) expenseColor else incomeColor

    BaseRow(
        color = color,
        title = transaction.category.name,
        subtitle = formatter.format(transaction.transactionRecord.date),
        amount = transaction.transactionRecord.amount,
        currency = transaction.account.currency,
        negative = isExpense,
        holdedItem = transaction,
        onItemSelected = onItemSelected
    )
}

@Composable
private fun FutureTransactionRow(
    futureTransaction: FullFutureTransaction,
    onItemSelected: (FullFutureTransaction) -> Unit = {},
) {

}

private val expenseColor = Color(0xFFCD5C5C)
private val incomeColor = Color(0xFF196F3D)