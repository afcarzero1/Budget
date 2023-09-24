package com.example.budgetapplication.ui.transactions

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.Button
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.budgetapplication.data.future_transactions.FullFutureTransaction
import com.example.budgetapplication.data.transactions.FullTransactionRecord
import com.example.budgetapplication.data.transactions.TransactionType
import com.example.budgetapplication.ui.AppViewModelProvider
import com.example.budgetapplication.ui.components.BaseRow
import com.example.budgetapplication.ui.components.ListDivider
import com.example.budgetapplication.ui.components.VerticalBar
import com.example.budgetapplication.ui.components.formatCurrencyAmount
import com.example.budgetapplication.ui.navigation.FutureTransactionDetails
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
        Column(modifier = Modifier.fillMaxWidth()) {
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
        }
    }, floatingButton = {
        FloatingActionButton(
            onClick = {
                if (showFutureTransactions) {
                    navController.navigate(FutureTransactionEntry.route)
                } else {
                    navController.navigate(TransactionEntry.route)
                }
            }, modifier = Modifier.padding(16.dp)
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
            Log.d("TransactionSummary", "Future Transaction: ${transaction.futureTransaction.id}")
            FutureTransactionRow(
                futureTransaction = transaction,
                onItemSelected = {
                    Log.d("TransactionSummary", "Details of Future Transaction ID: ${it.futureTransaction.id}")
                    navController.navigate(
                        FutureTransactionDetails.route + "/${transaction.futureTransaction.id}"
                    )
                }
            )
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

    val isExpense = transaction.transactionRecord.type == TransactionType.EXPENSE
    val color = if (isExpense) expenseColor else incomeColor

    BaseRow(
        color = color,
        title = transaction.category.name,
        subtitle = formatter.format(transaction.transactionRecord.date),
        amount = transaction.transactionRecord.amount,
        currency = transaction.account.currency.name,
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

    val formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy", Locale.getDefault())

    val isExpense = futureTransaction.futureTransaction.type == TransactionType.EXPENSE
    val color = if (isExpense) expenseColor else incomeColor
    //TODO: Assign global formatter using dependency injection
    // TODO : Create a base row that accepts different objects for title/subtitle, so it is more
    // flexible

    Row(
        modifier = Modifier
            .height(90.dp)
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        // Color bar in the left side
        VerticalBar(
            color = color, modifier = Modifier.width(2.dp)
        )

        Spacer(Modifier.width(12.dp))

        // Title and subtitle
        Column(Modifier) {
            Text(
                text = futureTransaction.category.name,
                style = MaterialTheme.typography.headlineSmall
            )

            val formattedInitialDate =
                formatter.format(futureTransaction.futureTransaction.startDate)
            val formattedFinalDate = formatter.format(futureTransaction.futureTransaction.endDate)

            Text(text = "From $formattedInitialDate", style = MaterialTheme.typography.titleSmall)
            Text(text = "To $formattedFinalDate", style = MaterialTheme.typography.titleSmall)

        }

        Spacer(Modifier.weight(1f))

        // Amount
        Column() {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = if (isExpense) "-${futureTransaction.currency.name}" else futureTransaction.currency.name,
                    style = MaterialTheme.typography.labelLarge,
                    modifier = Modifier.align(Alignment.CenterVertically)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = formatCurrencyAmount(futureTransaction.futureTransaction.amount),
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.align(Alignment.CenterVertically)
                )
                Spacer(modifier = Modifier.width(4.dp))
                IconButton(onClick = { onItemSelected(futureTransaction) }) {
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowRight,
                        contentDescription = null,
                    )
                }
            }
            Text(
                text = "Repeted ${futureTransaction.futureTransaction.recurrenceType} every ${futureTransaction.futureTransaction.recurrenceValue}",
                style = MaterialTheme.typography.labelSmall
            )
        }

        Spacer(Modifier.width(16.dp))
    }
    ListDivider()

}

private val expenseColor = Color(0xFFCD5C5C)
private val incomeColor = Color(0xFF196F3D)