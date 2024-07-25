package com.example.budgetahead.ui.transactions

import android.util.Log
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import com.example.budgetahead.R
import com.example.budgetahead.data.accounts.Account
import com.example.budgetahead.data.accounts.AccountWithCurrency
import com.example.budgetahead.data.categories.Category
import com.example.budgetahead.data.categories.CategoryType
import com.example.budgetahead.data.currencies.Currency
import com.example.budgetahead.data.future_transactions.FullFutureTransaction
import com.example.budgetahead.data.future_transactions.FutureTransaction
import com.example.budgetahead.data.future_transactions.RecurrenceType
import com.example.budgetahead.data.future_transactions.RecurrenceTypeDescriptions
import com.example.budgetahead.data.transactions.FullTransactionRecord
import com.example.budgetahead.data.transactions.TransactionType
import com.example.budgetahead.data.transfers.Transfer
import com.example.budgetahead.data.transfers.TransferWithAccounts
import com.example.budgetahead.ui.AppViewModelProvider
import com.example.budgetahead.ui.components.ListDivider
import com.example.budgetahead.ui.components.VerticalBar
import com.example.budgetahead.ui.components.buttons.BudgetFloatingButton
import com.example.budgetahead.ui.navigation.FutureTransactionDetails
import com.example.budgetahead.ui.navigation.FutureTransactionEntry
import com.example.budgetahead.ui.navigation.TabItem
import com.example.budgetahead.ui.navigation.TabbedPage
import com.example.budgetahead.ui.navigation.TransactionDetails
import com.example.budgetahead.ui.navigation.TransactionEntry
import com.example.budgetahead.ui.navigation.Transactions
import com.example.budgetahead.ui.navigation.TransferDetails
import com.example.budgetahead.ui.theme.InitialScreen
import com.example.budgetahead.use_cases.IconFromReIdUseCase
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun TransactionsSummary(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    transactionsViewModel: TransactionsSummaryViewModel =
        viewModel(factory = AppViewModelProvider.Factory),
    futureTransactionsViewModel: FutureTransactionsSummaryViewModel =
        viewModel(factory = AppViewModelProvider.Factory),
) {
    val showFutureTransactions = transactionsViewModel.onFutureTransactionsScreen
    val baseCurrency by transactionsViewModel.baseCurrency.collectAsState()
    val transactionsState by transactionsViewModel.transactionsUiState.collectAsState()
    val futureTransactionsState by futureTransactionsViewModel.futureTransactionsUiState.collectAsState()
    Log.d("TRANSACTIONS SUMMARY", "Show future: $showFutureTransactions")

    InitialScreen(
        navController = navController,
        destination = Transactions,
        screenBody = {
            TransactionsAndPlannedSummaryBody(
                transactions = transactionsState.groupedTransactionsAndTransfers,
                futureTransactions = futureTransactionsState.futureTransactionsList,
                baseCurrency = baseCurrency,
                navController = navController,
                onTabChanged = { transactionsViewModel.toggleScreen(it == 1) },
            )
        },
        floatingButton = {
            BudgetFloatingButton(
                onClick = {
                    if (showFutureTransactions) {
                        navController.navigate(FutureTransactionEntry.route)
                    } else {
                        navController.navigate(TransactionEntry.route)
                    }
                },
                contentDescription = "Add Transaction",
            )
        },
    )
}

@Composable
fun TransactionsAndPlannedSummaryBody(
    transactions: List<GroupOfTransactionsAndTransfers>,
    futureTransactions: List<FullFutureTransaction>,
    baseCurrency: String,
    navController: NavHostController,
    onTabChanged: (Int) -> Unit,
    modifier: Modifier = Modifier,
    dividerColor: Color = MaterialTheme.colorScheme.background,
) {
    TabbedPage(
        tabs =
            listOf(
                TabItem(
                    title = "Present",
                    icon = {
                        Icon(
                            painter =
                                painterResource(
                                    id = R.drawable.receipt_long_24dp_fill0_wght400_grad0_opsz24,
                                ),
                            contentDescription = "Executed Transactions",
                        )
                    },
                    screen = {
                        if (transactions.isEmpty()) {
                            EmptyTransactionScreen()
                        } else {
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                verticalArrangement = Arrangement.SpaceBetween,
                            ) {
                                TransactionsSummaryBody(
                                    transactions = transactions,
                                    baseCurrency = baseCurrency,
                                    navController = navController,
                                    dividerColor = dividerColor,
                                )
                            }
                        }
                    },
                ),
                TabItem(
                    title = "Planned",
                    icon = {
                        Icon(
                            painter =
                                painterResource(
                                    id = R.drawable.event_upcoming_24dp_fill0_wght400_grad0_opsz24,
                                ),
                            contentDescription = "Planned Transactions",
                        )
                    },
                    screen = {
                        if (futureTransactions.isEmpty()) {
                            EmptyTransactionScreen()
                        } else {
                            Column(
                                modifier =
                                    Modifier
                                        .fillMaxWidth()
                                        .padding(4.dp),
                                verticalArrangement = Arrangement.SpaceBetween,
                            ) {
                                FutureTransactionsSummaryBody(
                                    futureTransactions = futureTransactions,
                                    navController = navController,
                                )
                            }
                        }
                    },
                ),
            ),
        onTabChanged = onTabChanged,
        modifier = modifier,
    )
}

@Composable
fun TransactionsSummaryBody(
    transactions: List<GroupOfTransactionsAndTransfers>,
    baseCurrency: String,
    navController: NavHostController,
    modifier: Modifier = Modifier,
    dividerColor: Color = MaterialTheme.colorScheme.background,
) {
    // Remember the scroll state
    val scrollState = rememberLazyListState()

    LazyColumn(
        modifier = modifier,
        state = scrollState,
    ) {
        transactions.forEach {
            item {
                DayTransactionsGroup(
                    transactions = it.transactions,
                    transfers = it.transfers,
                    baseCurrency = baseCurrency,
                    date = it.date,
                    onTransactionSelected = { selectedTransaction ->
                        Log.d(
                            "TransactionsSummary",
                            "Selected transaction: ${selectedTransaction.transactionRecord.id}",
                        )
                        navController.navigate(
                            TransactionDetails.route +
                                "/${selectedTransaction.transactionRecord.id}",
                        )
                    },
                    onTransferSelected = {
                        navController.navigate(
                            TransferDetails.route + "/${it.id}",
                        )
                    },
                    dividerColor = dividerColor,
                )
            }
        }
    }
}

@Composable
fun DayTransactionsGroup(
    transactions: List<FullTransactionRecord>,
    transfers: List<TransferWithAccounts>,
    baseCurrency: String,
    date: LocalDate,
    onTransactionSelected: (FullTransactionRecord) -> Unit,
    onTransferSelected: (Transfer) -> Unit,
    modifier: Modifier = Modifier,
    dividerColor: Color = MaterialTheme.colorScheme.background,
) {
    val totalAmount =
        transactions.sumOf {
            it.transactionRecord.amount.toDouble() /
                it.account.currency.value
                    .toDouble()
        }

    val formattedAmount = Currency.formatAmountStatic(baseCurrency, totalAmount.toFloat())

    // Combine and sort transactions and transfers by date
    val items =
        (
            transactions.map { ItemWrapper(it.transactionRecord.date, it, true) } +
                transfers.map { ItemWrapper(it.transfer.date, it, false) }
        ).sortedBy { it.date }

    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, top = 8.dp, end = 16.dp, bottom = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom,
        ) {
            Text(
                text = date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.weight(1f),
            )
            Text(
                text = formattedAmount,
                style = MaterialTheme.typography.bodySmall,
                color = LocalContentColor.current.copy(alpha = 0.6f),
            )
        }

        Card(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(4.dp),
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
            ) {
                items.forEachIndexed { index, item ->
                    if (item.isTransaction) {
                        TransactionRow(
                            transaction = item.data as FullTransactionRecord,
                            onItemSelected = { onTransactionSelected(it) },
                        )
                    } else {
                        TransferRow(
                            transfer = item.data as TransferWithAccounts,
                            onTransferSelected = { onTransferSelected(it) },
                        )
                    }
                    if (index < items.size - 1) {
                        Divider(color = dividerColor, thickness = 1.dp)
                    }
                }
            }
        }
    }
}

data class ItemWrapper(
    val date: LocalDateTime,
    val data: Any,
    val isTransaction: Boolean,
)

@Composable
fun FutureTransactionsSummaryBody(
    futureTransactions: List<FullFutureTransaction>,
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {
    val scrollState = rememberLazyListState()

    LazyColumn(
        modifier = modifier,
        state = scrollState,
    ) {
        items(futureTransactions.size) { index ->
            val transaction = futureTransactions[index]
            Log.d("TransactionSummary", "Future Transaction: ${transaction.futureTransaction.id}")
            FutureTransactionRow(futureTransaction = transaction, onItemSelected = {
                Log.d(
                    "TransactionSummary",
                    "Details of Future Transaction ID: ${it.futureTransaction.id}",
                )
                navController.navigate(
                    FutureTransactionDetails.route + "/${transaction.futureTransaction.id}",
                )
            })
            ListDivider()
        }
    }
}

@Composable
fun EmptyTransactionScreen() {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = "No transactions yet. Create one by clicking the + button",
            modifier = Modifier.padding(16.dp),
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
    val formattedAmount =
        Currency.formatAmountStatic(
            transaction.account.currency.name,
            transaction.transactionRecord.amount,
        )

    Row(
        modifier =
            Modifier
                .height(68.dp)
                .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // Color bar in the left side
        VerticalBar(
            color = color,
            modifier = Modifier.width(2.dp),
        )
        Spacer(Modifier.width(12.dp))
        // Title and subtitle
        Column(Modifier) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                transaction.category?.let {
                }
                val iconResId =
                    transaction.category?.let {
                        IconFromReIdUseCase(LocalContext.current).getCategoryIconResId(it.iconResId)
                    } ?: R.drawable.change_circle_24dp_fill0_wght200_grad0_opsz24
                Icon(
                    painter = painterResource(id = iconResId),
                    contentDescription = "Category Icon",
                    tint = color.copy(alpha = 0.5f),
                    modifier = Modifier.padding(end = 8.dp),
                )
                Text(
                    text = transaction.category?.name ?: "Transfer",
                    style = MaterialTheme.typography.headlineSmall,
                )
            }
            Text(
                text = formatter.format(transaction.transactionRecord.date),
                style = MaterialTheme.typography.titleSmall,
            )
        }
        Spacer(Modifier.weight(1f))
        // Amount
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = if (isExpense) "-" else " ",
                style = MaterialTheme.typography.labelLarge,
                modifier = Modifier.align(Alignment.CenterVertically),
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = formattedAmount,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.align(Alignment.CenterVertically),
            )
            Spacer(modifier = Modifier.width(4.dp))
        }
        IconButton(onClick = { onItemSelected(transaction) }) {
            Icon(
                imageVector = Icons.Default.KeyboardArrowRight,
                contentDescription = null,
            )
        }
    }
    // ListDivider()
}

@Composable
private fun TransferRow(
    transfer: TransferWithAccounts,
    onTransferSelected: (Transfer) -> Unit,
) {
    val formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy", Locale.getDefault())
    val formattedAmountSource =
        Currency.formatAmountStatic(
            transfer.sourceAccount.currency.name,
            transfer.transfer.amountSource,
        )
    val formattedAmountDestination =
        Currency.formatAmountStatic(
            transfer.destinationAccount.currency.name,
            transfer.transfer.amountDestination,
        )

    Row(
        modifier =
            Modifier
                .height(68.dp)
                .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        VerticalBar(
            color = Color.Blue.copy(alpha = 0.2f),
            modifier = Modifier.width(2.dp),
        )
        Spacer(Modifier.width(12.dp))
        Icon(
            painter =
                painterResource(
                    id = R.drawable.change_circle_24dp_fill0_wght200_grad0_opsz24,
                ),
            contentDescription = "Category Icon",
            tint = Color.Blue.copy(alpha = 0.5f),
            modifier = Modifier.padding(end = 8.dp),
        )
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(2f),
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = transfer.sourceAccount.account.name,
                    style = MaterialTheme.typography.headlineSmall,
                )
                Text(
                    text = formattedAmountSource,
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
            Icon(
                imageVector = Icons.Filled.ArrowForward,
                contentDescription = "To",
                modifier = Modifier.padding(horizontal = 8.dp),
            )
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = transfer.destinationAccount.account.name,
                    style = MaterialTheme.typography.headlineSmall,
                )
                Text(
                    text = formattedAmountDestination,
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
        }
        IconButton(
            onClick = { onTransferSelected(transfer.transfer) },
        ) {
            Icon(
                imageVector = Icons.Default.KeyboardArrowRight,
                contentDescription = null,
            )
        }
    }
    // ListDivider()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FutureTransactionRow(
    futureTransaction: FullFutureTransaction,
    onItemSelected: (FullFutureTransaction) -> Unit = {},
) {
    val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale.getDefault())

    val isExpense = futureTransaction.futureTransaction.type == TransactionType.EXPENSE
    val color = if (isExpense) expenseColor else incomeColor

    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = {
            onItemSelected(futureTransaction)
        },
    ) {
        Row(
            modifier =
                Modifier
                    .height(90.dp)
                    .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            // Color bar in the left side
            VerticalBar(
                color = color,
                modifier = Modifier.width(2.dp),
            )

            Spacer(Modifier.width(8.dp))
            Column {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    val icon =
                        IconFromReIdUseCase(LocalContext.current).getCategoryIconResId(
                            futureTransaction.category.iconResId,
                        )
                    Row(
                        modifier = Modifier.weight(2f),
                        horizontalArrangement = Arrangement.Start,
                        verticalAlignment = Alignment.Bottom,
                    ) {
                        Icon(
                            painter = painterResource(id = icon),
                            contentDescription = "Category Icon",
                            tint = color,
                        )

                        Text(
                            text = futureTransaction.category.name,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(start = 8.dp),
                        )
                    }

                    // Amount
                    Row(
                        modifier = Modifier.weight(1f),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.Bottom,
                    ) {
                        Text(
                            text = if (isExpense) "-" else " ",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.align(Alignment.CenterVertically),
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text =
                                futureTransaction.currency.formatAmount(
                                    futureTransaction.futureTransaction.amount,
                                ),
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.align(Alignment.CenterVertically),
                        )
                    }
                }

                val formattedInitialDate =
                    formatter.format(futureTransaction.futureTransaction.startDate)
                val formattedFinalDate =
                    formatter.format(futureTransaction.futureTransaction.endDate)

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        painterResource(id = R.drawable.date_range_24dp_fill0_wght400_grad0_opsz24),
                        contentDescription = "Date",
                        modifier = Modifier.size(16.dp),
                    )
                    Text(
                        text = formattedInitialDate,
                        style = MaterialTheme.typography.bodySmall,
                        fontSize = 14.sp,
                    )
                    if (futureTransaction.futureTransaction.recurrenceType != RecurrenceType.NONE) {
                        Icon(
                            imageVector = Icons.Filled.ArrowForward,
                            contentDescription = "To",
                            modifier = Modifier.size(8.dp),
                        )
                        Text(
                            text = formattedFinalDate,
                            style = MaterialTheme.typography.bodySmall,
                            fontSize = 14.sp,
                        )
                    }
                }

                if (futureTransaction.futureTransaction.recurrenceType != RecurrenceType.NONE) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(
                            painterResource(
                                id = R.drawable.event_repeat_24dp_fill0_wght400_grad0_opsz24,
                            ),
                            contentDescription = "Event repeated",
                            modifier = Modifier.size(16.dp),
                        )
                        val text = if(futureTransaction.futureTransaction.recurrenceType.isContinuous())"On periods of" else "Every"
                        var recurrenceDescription = RecurrenceTypeDescriptions.descriptions[futureTransaction.futureTransaction.recurrenceType]?:"Unknown"

                        if(futureTransaction.futureTransaction.recurrenceValue == 1){
                            recurrenceDescription=recurrenceDescription.removeSuffix("s")
                        }

                        Text(
                            text = "$text ${futureTransaction.futureTransaction.recurrenceValue} $recurrenceDescription",
                        )
                    }
                }
            }
            // Amount
            Spacer(Modifier.width(2.dp))
        }
    }

    ListDivider()
}

@Preview
@Composable
fun PreviewTransferRow() {
    val transfer =
        TransferWithAccounts(
            transfer =
                Transfer(
                    amountSource = 10.0f,
                    amountDestination = 15.0f,
                    sourceAccountId = 0,
                    destinationAccountId = 1,
                    id = 0,
                    date = LocalDateTime.now(),
                    destinationAccountTransactionId = 10,
                    sourceAccountTransactionId = 11,
                ),
            sourceAccount =
                AccountWithCurrency(
                    account =
                        Account(
                            id = 0,
                            name = "Bank",
                            currency = "USD",
                            initialBalance = 5000.0f,
                        ),
                    Currency(
                        "USD",
                        1.0f,
                        updatedTime = LocalDateTime.now(),
                    ),
                ),
            destinationAccount =
                AccountWithCurrency(
                    account =
                        Account(
                            id = 1,
                            name = "Bank",
                            currency = "EUR",
                            initialBalance = 4000.0f,
                        ),
                    Currency(
                        "EUR",
                        1.1f,
                        updatedTime = LocalDateTime.now(),
                    ),
                ),
        )
    TransferRow(transfer = transfer, onTransferSelected = {})
}

@Preview
@Composable
fun PreviewFutureTransactionRow() {
    val futureTransaction =
        FutureTransaction(
            id = 0,
            name = "Gym",
            type = TransactionType.EXPENSE,
            categoryId = 0,
            amount = 50f,
            currency = "EUR",
            startDate = LocalDateTime.now(),
            endDate = LocalDateTime.now().plusMonths(3),
            recurrenceType = RecurrenceType.MONTHLY,
            recurrenceValue = 1,
        )

    val fullFutureTransaction =
        FullFutureTransaction(
            futureTransaction = futureTransaction,
            category =
                Category(
                    id = 0,
                    name = "Sports",
                    defaultType = CategoryType.Expense,
                    parentCategoryId = null,
                    iconResId = "school",
                ),
            currency =
                Currency(
                    "EUR",
                    1.0f,
                    LocalDateTime.now(),
                ),
        )

    FutureTransactionRow(futureTransaction = fullFutureTransaction)
}

private val expenseColor = Color(0xFFCD5C5C)
private val incomeColor = Color(0xFF196F3D)
