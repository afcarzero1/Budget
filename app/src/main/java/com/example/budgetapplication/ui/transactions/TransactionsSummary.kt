package com.example.budgetapplication.ui.transactions

import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.budgetapplication.R
import com.example.budgetapplication.data.categories.Category
import com.example.budgetapplication.data.categories.CategoryType
import com.example.budgetapplication.data.currencies.Currency
import com.example.budgetapplication.data.future_transactions.FullFutureTransaction
import com.example.budgetapplication.data.future_transactions.FutureTransaction
import com.example.budgetapplication.data.future_transactions.RecurrenceType
import com.example.budgetapplication.data.transactions.FullTransactionRecord
import com.example.budgetapplication.data.transactions.TransactionType
import com.example.budgetapplication.ui.AppViewModelProvider
import com.example.budgetapplication.ui.components.BaseRow
import com.example.budgetapplication.ui.components.ListDivider
import com.example.budgetapplication.ui.components.VerticalBar
import com.example.budgetapplication.ui.navigation.FutureTransactionDetails
import com.example.budgetapplication.ui.navigation.FutureTransactionEntry
import com.example.budgetapplication.ui.navigation.TabItem
import com.example.budgetapplication.ui.navigation.TabbedPage
import com.example.budgetapplication.ui.navigation.TransactionDetails
import com.example.budgetapplication.ui.navigation.TransactionEntry
import com.example.budgetapplication.ui.navigation.Transactions
import com.example.budgetapplication.ui.theme.InitialScreen
import com.example.budgetapplication.use_cases.IconFromReIdUseCase
import java.time.LocalDate
import java.time.LocalDateTime
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
    val baseCurrency by transactionsViewModel.baseCurrency.collectAsState()


    InitialScreen(navController = navController, destination = Transactions, screenBody = {
        val transactionsState by transactionsViewModel.transactionsUiState.collectAsState()
        val futureTransactionsState by futureTransactionsViewModel.futureTransactionsUiState.collectAsState()

        TabbedPage(tabs = listOf(
            TabItem(
                title = "Present",
                icon = {
                    Icon(
                        painter = painterResource(id = R.drawable.receipt_long_24dp_fill0_wght400_grad0_opsz24),
                        contentDescription = "Executed Transactions"
                    )
                },
                screen = {
                    if (transactionsState.transactionsList.isEmpty()) {
                        EmptyTransactionScreen()
                    } else {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            TransactionsSummaryBody(
                                transactions = transactionsState.transactionsList,
                                baseCurrency = baseCurrency,
                                navController = navController
                            )
                        }
                    }
                },
            ), TabItem(title = "Planned", icon = {
                Icon(
                    painter = painterResource(id = R.drawable.event_upcoming_24dp_fill0_wght400_grad0_opsz24),
                    contentDescription = "Planned Transactions"
                )
            }, screen = {
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
            })
        ), onTabChanged = {
            showFutureTransactions = it == 1
        })
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
    baseCurrency: String,
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    val groupedByDate = transactions.groupBy { it.transactionRecord.date.toLocalDate() }

    // Remember the scroll state
    val scrollState = rememberLazyListState()

    LazyColumn(
        modifier = modifier, state = scrollState
    ) {
        groupedByDate.entries.forEach { entry ->
            val date = entry.key
            val transactionsForDate = entry.value

            item {
                DayTransactionsGroup(transactions = transactionsForDate,
                    baseCurrency = baseCurrency,
                    date = date,
                    onItemSelected = { selectedTransaction ->
                        Log.d(
                            "TransactionsSummary",
                            "Selected transaction: ${selectedTransaction.transactionRecord.id}"
                        )
                        navController.navigate(
                            TransactionDetails.route + "/${selectedTransaction.transactionRecord.id}"
                        )
                    })
            }
        }
    }
}


@Composable
fun DayTransactionsGroup(
    transactions: List<FullTransactionRecord>,
    baseCurrency: String,
    date: LocalDate,
    onItemSelected: (FullTransactionRecord) -> Unit,
    modifier: Modifier = Modifier
) {
    val totalAmount = transactions.sumOf {
        it.transactionRecord.amount.toDouble() / it.account.currency.value.toDouble()
    }

    val formattedAmount = Currency.formatAmountStatic(baseCurrency, totalAmount.toFloat())

    Column(modifier = modifier.fillMaxWidth()) {


        // Add date as a title here
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, top = 8.dp, end = 16.dp, bottom = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom
        ) {
            Text(
                text = date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")), // or any other format you prefer
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.weight(1f) // Allocates all available space to the left
            )

            // Add formatted amount here
            Text(
                text = formattedAmount,
                style = MaterialTheme.typography.bodySmall,
                color = LocalContentColor.current.copy(alpha = 0.6f), // 0.6f is for 60% opacity, adjust as needed
            )
        }

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp),
        ) {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                transactions.forEach { transaction ->
                    TransactionRow(
                        transaction = transaction, onItemSelected = onItemSelected
                    )
                    ListDivider()
                }
            }
        }
    }
}


@Composable
fun FutureTransactionsSummaryBody(
    futureTransactions: List<FullFutureTransaction>,
    navController: NavHostController,
    modifier: Modifier = Modifier
) {

    val scrollState = rememberLazyListState()

    LazyColumn(
        modifier = modifier, state = scrollState
    ) {
        items(futureTransactions.size) { index ->
            val transaction = futureTransactions[index]
            Log.d("TransactionSummary", "Future Transaction: ${transaction.futureTransaction.id}")
            FutureTransactionRow(futureTransaction = transaction, onItemSelected = {
                Log.d(
                    "TransactionSummary",
                    "Details of Future Transaction ID: ${it.futureTransaction.id}"
                )
                navController.navigate(
                    FutureTransactionDetails.route + "/${transaction.futureTransaction.id}"
                )
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

    val isExpense = transaction.transactionRecord.type == TransactionType.EXPENSE
    val color = if (isExpense) expenseColor else incomeColor
    val formattedAmount = Currency.formatAmountStatic(
        transaction.account.currency.name,
        transaction.transactionRecord.amount
    )

    Row(
        modifier = Modifier
            .height(68.dp)
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Color bar in the left side
        VerticalBar(
            color = color,
            modifier = Modifier.width(2.dp)
        )

        Spacer(Modifier.width(12.dp))
        // Title and subtitle
        Column(Modifier) {
            Row{
                val icon =
                    IconFromReIdUseCase(LocalContext.current).getCategoryIconResId(
                        transaction.category.iconResId
                    )
                Icon(
                    painter = painterResource(id = icon),
                    contentDescription = "Category Icon",
                    tint = color.copy(alpha = 0.5f)
                )
                Text(
                    text = transaction.category.name,
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }



            Text(
                text = formatter.format(transaction.transactionRecord.date),
                style = MaterialTheme.typography.titleSmall
            )

        }
        Spacer(Modifier.weight(1f))
        // Amount
        Row(
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = if (isExpense) "-" else " ",
                style = MaterialTheme.typography.labelLarge,
                modifier = Modifier.align(Alignment.CenterVertically)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = formattedAmount,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.align(Alignment.CenterVertically)
            )
            Spacer(modifier = Modifier.width(4.dp))
            IconButton(onClick = { onItemSelected(transaction) }) {
                Icon(
                    imageVector = Icons.Default.KeyboardArrowRight,
                    contentDescription = null,
                )
            }
        }
        Spacer(Modifier.width(16.dp))
    }
    ListDivider()
}

@Composable
private fun FutureTransactionRow(
    futureTransaction: FullFutureTransaction,
    onItemSelected: (FullFutureTransaction) -> Unit = {},
) {
    val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale.getDefault())

    val isExpense = futureTransaction.futureTransaction.type == TransactionType.EXPENSE
    val color = if (isExpense) expenseColor else incomeColor

    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .height(90.dp)
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            // Color bar in the left side
            VerticalBar(
                color = color, modifier = Modifier.width(2.dp)
            )

            Spacer(Modifier.width(8.dp))

            // Title and subtitle
            Column(Modifier.weight(1f)) {

                Row(verticalAlignment = Alignment.CenterVertically) {
                    val icon =
                        IconFromReIdUseCase(LocalContext.current).getCategoryIconResId(
                            futureTransaction.category.iconResId
                        )
                    Icon(
                        painter = painterResource(id = icon),
                        contentDescription = "Category Icon",
                        tint = color
                    )

                    Text(
                        text = futureTransaction.category.name,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }


                val formattedInitialDate =
                    formatter.format(futureTransaction.futureTransaction.startDate)
                val formattedFinalDate =
                    formatter.format(futureTransaction.futureTransaction.endDate)


                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        painterResource(id = R.drawable.date_range_24dp_fill0_wght400_grad0_opsz24),
                        contentDescription = "Date",
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = formattedInitialDate,
                        style = MaterialTheme.typography.bodySmall,
                        fontSize = 14.sp
                    )
                    Icon(
                        imageVector = Icons.Filled.ArrowForward,
                        contentDescription = "To",
                        modifier = Modifier.size(8.dp)
                    )
                    Text(
                        text = formattedFinalDate,
                        style = MaterialTheme.typography.bodySmall,
                        fontSize = 14.sp
                    )
                }

                if (futureTransaction.futureTransaction.recurrenceType != RecurrenceType.NONE) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            painterResource(id = R.drawable.event_repeat_24dp_fill0_wght400_grad0_opsz24),
                            contentDescription = "Event repeated",
                            modifier = Modifier.size(16.dp)
                        )
                        Text(text = futureTransaction.futureTransaction.recurrenceType.name.lowercase())
                    }
                }


            }

            // Amount
            Column() {
                Row(
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    Text(
                        text = if (isExpense) "-" else " ",
                        style = MaterialTheme.typography.labelLarge,
                        modifier = Modifier.align(Alignment.CenterVertically)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = futureTransaction.currency.formatAmount(futureTransaction.futureTransaction.amount),
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
            }
            Spacer(Modifier.width(16.dp))
        }
    }

    ListDivider()
}


@Preview
@Composable
fun PreviewFutureTransactionRow(
) {
    val futureTransaction = FutureTransaction(
        id = 0,
        name = "Gym",
        type = TransactionType.EXPENSE,
        categoryId = 0,
        amount = 50f,
        currency = "EUR",
        startDate = LocalDateTime.now(),
        endDate = LocalDateTime.now().plusMonths(3),
        recurrenceType = RecurrenceType.MONTHLY,
        recurrenceValue = 1
    )

    val fullFutureTransaction = FullFutureTransaction(
        futureTransaction = futureTransaction, category = Category(
            id = 0,
            name = "Sports",
            defaultType = CategoryType.Expense,
            parentCategoryId = null,
            iconResId = "school"
        ), currency = Currency(
            "EUR", 1.0f, LocalDateTime.now()
        )
    )

    FutureTransactionRow(futureTransaction = fullFutureTransaction)
}


private val expenseColor = Color(0xFFCD5C5C)
private val incomeColor = Color(0xFF196F3D)