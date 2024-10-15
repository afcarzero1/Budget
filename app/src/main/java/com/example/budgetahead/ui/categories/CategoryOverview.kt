package com.example.budgetahead.ui.categories

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.budgetahead.R
import com.example.budgetahead.data.categories.Category
import com.example.budgetahead.data.currencies.Currency
import com.example.budgetahead.data.future_transactions.FullFutureTransaction
import com.example.budgetahead.ui.AppViewModelProvider
import com.example.budgetahead.ui.navigation.CategoryDetails
import com.example.budgetahead.ui.navigation.SecondaryScreenTopBar
import com.example.budgetahead.ui.transactions.FutureTransactionsSummaryBody
import com.example.budgetahead.ui.transactions.GroupOfTransactionsAndTransfers
import com.example.budgetahead.ui.transactions.TransactionsSummaryBody

@Composable
fun CategoryOverviewScreen(
    navController: NavHostController,
    viewModel: CategoryOverviewViewModel = viewModel(factory = AppViewModelProvider.Factory),
) {
    val categoryUiState by viewModel.categoryState.collectAsState()
    val baseCurrency by viewModel.baseCurrency.collectAsState()

    Scaffold(
        topBar = {
            SecondaryScreenTopBar(
                navigateBack = { navController.popBackStack() },
                title = categoryUiState.category.name,
                actions = {
                    IconButton(
                        modifier = Modifier.padding(horizontal = 8.dp),
                        onClick = {
                            navController.navigate(
                                "${CategoryDetails.route}/${categoryUiState.category.id}",
                            )
                        },
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Edit,
                            contentDescription = stringResource(R.string.edit),
                            tint = MaterialTheme.colorScheme.onPrimary,
                        )
                    }
                },
                containerColor = MaterialTheme.colorScheme.primary,
            )
        },
    ) {
        CategorySummaryBody(
            category = categoryUiState.category,
            categoryTransactions = categoryUiState.transactions,
            categoryPlannedTransactions = categoryUiState.plannedTransactions,
            baseCurrency = baseCurrency,
            navController = navController,
            modifier =
                Modifier
                    .padding(it)
                    .fillMaxSize(),
        )
    }
}

@Composable
fun CategorySummaryBody(
    category: Category,
    categoryTransactions: List<GroupOfTransactionsAndTransfers>,
    categoryPlannedTransactions: List<FullFutureTransaction>,
    baseCurrency: String,
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier =
            modifier.background(
                MaterialTheme.colorScheme.background,
            ),
    ) {
        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .background(
                        color = MaterialTheme.colorScheme.primaryContainer,
                    ),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                modifier = Modifier.padding(horizontal = 16.dp),
                text = category.name,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                style = MaterialTheme.typography.titleLarge,
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                modifier = Modifier.padding(horizontal = 16.dp),
                text =
                    Currency.formatAmountStatic(
                        baseCurrency,
                        categoryTransactions
                            .sumOf {
                                it.transactions.sumOf { it.transactionRecord.amount.toDouble() }
                            }.toFloat(),
                    ),
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
            )

            Spacer(modifier = Modifier.height(32.dp))
        }

        SmallTabbedTransactions(
            transactions = categoryTransactions,
            baseCurrency = baseCurrency,
            navController = navController,
            futureTransactions = categoryPlannedTransactions,
            onTabChanged = {},
            dividerColor = MaterialTheme.colorScheme.background,
        )
    }
}

@Composable
fun SmallTabbedTransactions(
    transactions: List<GroupOfTransactionsAndTransfers>,
    baseCurrency: String,
    navController: NavHostController,
    futureTransactions: List<FullFutureTransaction>,
    modifier: Modifier = Modifier,
    onTabChanged: (Int) -> Unit = {},
    dividerColor: Color = MaterialTheme.colorScheme.background,
) {
    val tabs = listOf("Present", "Planned")
    var tabIndex by remember { mutableStateOf(0) }

    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.primaryContainer),
        ) {
            TabRow(
                selectedTabIndex = tabIndex,
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.padding(horizontal = 18.dp),
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        content = {
                            Row {
                                when (index) {
                                    0 ->
                                        Icon(
                                            painter =
                                                painterResource(
                                                    id = R.drawable.receipt_long_24dp_fill0_wght400_grad0_opsz24,
                                                ),
                                            contentDescription = "Executed Transactions",
                                            modifier = Modifier.size(16.dp),
                                        )
                                    1 ->
                                        Icon(
                                            painter =
                                                painterResource(
                                                    id = R.drawable.event_upcoming_24dp_fill0_wght400_grad0_opsz24,
                                                ),
                                            contentDescription = "Planned Transactions",
                                            modifier = Modifier.size(16.dp),
                                        )
                                }
                                Spacer(modifier = Modifier.width(4.dp))

                                Text(title)
                            }
                        },
                        selected = tabIndex == index,
                        onClick = {
                            tabIndex = index
                            onTabChanged(tabIndex)
                        },
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
        when (tabIndex) {
            0 ->
                TransactionsSummaryBody(
                    transactions = transactions,
                    baseCurrency = baseCurrency,
                    navController = navController,
                    dividerColor = dividerColor,
                    modifier = Modifier.padding(horizontal = 16.dp),
                )
            1 ->
                FutureTransactionsSummaryBody(
                    futureTransactions = futureTransactions,
                    navController = navController,
                    modifier = Modifier.padding(horizontal = 16.dp),
                )
        }
    }
}
