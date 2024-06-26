package com.example.budgetahead.ui.overall

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.budgetahead.R
import com.example.budgetahead.data.categories.Category
import com.example.budgetahead.data.currencies.Currency
import com.example.budgetahead.ui.AppViewModelProvider
import com.example.budgetahead.ui.components.BudgetSummary
import com.example.budgetahead.ui.components.graphics.rememberMarker
import com.example.budgetahead.ui.components.inputs.YearMonthDialog
import com.example.budgetahead.ui.components.values.ValueWithIcon
import com.example.budgetahead.ui.navigation.DefaultTopBar
import com.example.budgetahead.ui.navigation.Overview
import com.example.budgetahead.ui.navigation.Transactions
import com.example.budgetahead.ui.theme.InitialScreen
import com.example.budgetahead.ui.theme.SoftGreen
import com.example.budgetahead.ui.theme.WineRed
import com.patrykandpatrick.vico.compose.axis.horizontal.rememberBottomAxis
import com.patrykandpatrick.vico.compose.axis.vertical.rememberStartAxis
import com.patrykandpatrick.vico.compose.chart.Chart
import com.patrykandpatrick.vico.compose.chart.line.lineChart
import com.patrykandpatrick.vico.compose.m3.style.m3ChartStyle
import com.patrykandpatrick.vico.compose.style.ProvideChartStyle
import com.patrykandpatrick.vico.core.axis.AxisPosition
import com.patrykandpatrick.vico.core.axis.formatter.AxisValueFormatter
import com.patrykandpatrick.vico.core.entry.entryModelOf
import com.patrykandpatrick.vico.core.entry.entryOf
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.YearMonth
import java.time.format.DateTimeFormatter

@Composable
fun OverallScreen(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    overallViewModel: OverallViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val accountsTotalBalance by overallViewModel.accountsTotalBalance.collectAsState()

    val lastExpenses by overallViewModel.lastExpenses.collectAsState()
    val lastIncomes by overallViewModel.lastIncomes.collectAsState()
    val currentTransactionsInterval by overallViewModel.currentDateRange.collectAsState()

    val expectedExpenses by overallViewModel.expectedExpenses.collectAsState()
    val expectedIncomes by overallViewModel.expectedIncomes.collectAsState()
    val expectedExpensesInterval by overallViewModel.expectedDateRange.collectAsState()

    val balances by overallViewModel.balancesByDay.collectAsState()
    val balancesInterval by overallViewModel.balanceDateRange.collectAsState()

    val monthCashFlow by overallViewModel.monthCashFlow.collectAsState()
    val extraCashFlow by overallViewModel.monthExpectedExtraCashFlow.collectAsState()

    val centralDate by overallViewModel.centralDateFlow.collectAsState()


    var showDateDialog by remember { mutableStateOf(false) }

    Log.d("OVERVIEW", "Current Transactions $currentTransactionsInterval")
    Log.d("OVERVIEW", "Expected Transactions $expectedExpensesInterval")
    Log.d("OVERVIEW", "Expected balances $balancesInterval")

    InitialScreen(navController = navController, destination = Overview, screenBody = {
        OverallScreenBody(
            currentBalance = accountsTotalBalance,
            lastExpenses = lastExpenses,
            lastIncomes = lastIncomes,
            currentTransactionsInterval = currentTransactionsInterval,
            expectedExpenses = expectedExpenses,
            expectedIncomes = expectedIncomes,
            expectedExpensesInterval = expectedExpensesInterval,
            balances = balances,
            balancesInterval = balancesInterval,
            onBudgetsEmpty = {
                navController.navigate(Transactions.route)
            },
            onBalancesDateRangeChanged = { fromDate, toDate ->
                overallViewModel.setBalanceRangeFlow(fromDate, toDate)
            },
            monthCashFlow = monthCashFlow,
            extraCashFlow = extraCashFlow,
            centralDate = centralDate
        )
    }, topBar = { budgetDestination, navHostController ->
        DefaultTopBar(currentScreen = budgetDestination, actions = {
            IconButton(onClick = { showDateDialog = true }) {
                Icon(
                    imageVector = Icons.Filled.DateRange,
                    contentDescription = "Select date range"
                )
            }
        })
    })

    YearMonthDialog(
        isOpen = showDateDialog,
        currentSelection = currentTransactionsInterval.second,
        onClose = {
            showDateDialog = false
            overallViewModel.setCentralDate(it)
        },
    )
}

@Composable
fun OverallScreenBody(
    currentBalance: Pair<Currency, Float>,
    lastExpenses: Map<YearMonth, Map<Category, Float>>,
    lastIncomes: Map<YearMonth, Map<Category, Float>>,
    currentTransactionsInterval: Pair<YearMonth, YearMonth>,
    expectedExpenses: Map<YearMonth, Map<Category, Float>>,
    expectedIncomes: Map<YearMonth, Map<Category, Float>>,
    expectedExpensesInterval: Pair<YearMonth, YearMonth>,
    balances: Map<LocalDate, Float>,
    balancesInterval: Pair<YearMonth, YearMonth>,
    monthCashFlow: CashFlow,
    extraCashFlow: CashFlow,
    centralDate: YearMonth,
    onBudgetsEmpty: () -> Unit,
    onBalancesDateRangeChanged: (fromDate: YearMonth, toDate: YearMonth) -> Unit = { _, _ -> },
) {

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
    ) {
        CashFlowCard(
            registeredCashFlow = monthCashFlow,
            extraCashFlow = extraCashFlow,
            modifier = Modifier.fillMaxWidth(),
            yearMonth = centralDate
        )

        OverallTransactionsCard(
            expenses = lastExpenses,
            incomes = lastIncomes,
            transactionsInterval = currentTransactionsInterval,
            baseCurrency = currentBalance.first,
        )

        BudgetsCard(
            expenses = lastExpenses,
            expectedExpenses = expectedExpenses,
            baseCurrency = currentBalance.first,
            onBudgetsEmpty = onBudgetsEmpty
        )

        OverallExpectedCard(
            expenses = expectedExpenses,
            incomes = expectedIncomes,
            expensesInterval = expectedExpensesInterval,
            baseCurrency = currentBalance.first,
        )

        OverallBalancesCard(
            balances = balances,
            balancesDateRange = balancesInterval,
            baseCurrency = currentBalance.first,
            onRangeChanged = onBalancesDateRangeChanged
        )
    }

}


@Composable
fun CashFlowCard(
    yearMonth: YearMonth,
    registeredCashFlow: CashFlow,
    extraCashFlow: CashFlow,
    modifier: Modifier = Modifier
) {
    val totalProjectedCashflow =
        (registeredCashFlow.ingoing + extraCashFlow.ingoing) - (registeredCashFlow.outgoing + extraCashFlow.outgoing)
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
        ), elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        ), modifier = modifier
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Row(
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(start = 8.dp),
                ) {
                    Text(
                        text = "Projected cashflow",
                        maxLines = 1,
                        modifier = Modifier.padding(8.dp),
                        style = MaterialTheme.typography.labelMedium.copy(
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(
                                alpha = 0.9f
                            )
                        )
                    )
                    ValueWithIcon(
                        value = totalProjectedCashflow,
                        currency = registeredCashFlow.currency,
                        textStyle = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.SemiBold)
                    )
                }

                Text(
                    text = yearMonth.toString(),
                    style = MaterialTheme.typography.bodySmall,
                    color = LocalContentColor.current.copy(alpha = 0.6f),
                    modifier = Modifier
                        .padding(top = 4.dp)
                )
            }
            Spacer(Modifier.height(16.dp))
        }
    }
}


@Composable
fun CashFlowColumn(
    pastExpense: Float,
    upcomingExpense: Float,
    pastIncome: Float,
    upcomingIncome: Float,
    currency: Currency,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .weight(1f)
                .padding(start = 8.dp)
                .heightIn(max = 212.dp)
        ) {
            Surface(
                color = MaterialTheme.colorScheme.secondaryContainer,
                contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .padding(2.dp)
                    .fillMaxWidth()
            ) {
                DoubleValueText(
                    title = "Past",
                    value = Pair(pastIncome, pastExpense),
                    currency = currency,
                    styleTitle = MaterialTheme.typography.titleLarge,
                    styleValue = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(16.dp)
                )
            }

            DoubleValueText(
                title = "Upcoming",
                value = Pair(upcomingIncome, upcomingExpense),
                currency = currency,
                styleTitle = MaterialTheme.typography.titleMedium,
                styleValue = MaterialTheme.typography.bodyMedium
            )
        }
        Column(
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .heightIn(max = 212.dp)
                .weight(1f)
                .fillMaxHeight()
        ) {
            val projectedCashflow =
                (pastIncome + upcomingIncome) - (pastExpense + upcomingExpense)
            Surface(
                color = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .padding(2.dp)
                    .fillMaxWidth()
            ) {
                ValueText(
                    title = "Projected",
                    value = projectedCashflow,
                    currency = currency,
                    positive = projectedCashflow > 0,
                    styleTitle = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Medium),
                    styleValue = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium),
                    showIcon = true,
                    modifier = Modifier.padding(16.dp)
                )
            }

        }
    }
}


@Composable
fun ValueText(
    title: String?,
    value: Float,
    currency: Currency,
    positive: Boolean,
    modifier: Modifier = Modifier,
    styleTitle: TextStyle = MaterialTheme.typography.titleMedium,
    styleValue: TextStyle = MaterialTheme.typography.bodySmall,
    showIcon: Boolean = false
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        title?.let {
            Text(
                text = title,
                style = styleTitle,
                modifier = Modifier.padding(8.dp)
            )
        }
        Row(modifier = Modifier.wrapContentWidth()) {
            if (showIcon) {
                if (positive) {
                    Icon(
                        imageVector = Icons.Filled.KeyboardArrowUp,
                        contentDescription = "income",
                        tint = SoftGreen
                    )
                } else {
                    Icon(
                        imageVector = Icons.Filled.KeyboardArrowDown,
                        contentDescription = "income",
                        tint = WineRed,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
            }
            Text(
                text = currency.formatAmount(value),
                style = styleValue,
                color = if (value == 0f) {
                    if (positive) SoftGreen else WineRed
                } else MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(end = 8.dp)
            )
        }
    }
}

@Composable
fun DoubleValueText(
    title: String?,
    value: Pair<Float, Float>,
    currency: Currency,
    modifier: Modifier = Modifier,
    styleTitle: TextStyle = MaterialTheme.typography.titleMedium,
    styleValue: TextStyle = MaterialTheme.typography.bodyMedium
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        title?.let {
            Text(
                text = title,
                style = styleTitle,
                modifier = Modifier.padding(8.dp)
            )
        }
        Column(modifier = Modifier.wrapContentWidth()) {
            ValueText(
                title = null,
                value = value.first,
                currency = currency,
                positive = true,
                styleValue = styleValue
            )

            ValueText(
                title = null,
                value = value.second,
                currency = currency,
                positive = false,
                styleValue = styleValue
            )
        }
    }
}


@Preview
@Composable
fun PreviewCashflowCard() {
    CashFlowCard(
        registeredCashFlow = CashFlow(
            outgoing = 2000f, ingoing = 1500f, currency = Currency("USD", 1.0f, LocalDateTime.now())
        ),
        extraCashFlow = CashFlow(
            outgoing = 500f, ingoing = 100f, currency = Currency("USD", 1.0f, LocalDateTime.now())
        ),
        yearMonth = YearMonth.now()
    )
}


@Composable
fun BudgetsCard(
    expenses: Map<YearMonth, Map<Category, Float>>,
    expectedExpenses: Map<YearMonth, Map<Category, Float>>,
    baseCurrency: Currency,
    onBudgetsEmpty: () -> Unit
) {
    // Determine the last month available in the data
    val lastMonth = (expenses.keys).maxOrNull()
    Log.d("OverallScreen", "Attempting to show budgets of: $lastMonth")

    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ), elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        ), modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        lastMonth?.let {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    Text(
                        text = "Budgets",
                        style = MaterialTheme.typography.headlineSmall,
                        modifier = Modifier
                            .padding(bottom = 16.dp)
                            .weight(1f),
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    Text(
                        text = lastMonth.toString(),
                        style = MaterialTheme.typography.bodyLarge,
                        color = LocalContentColor.current.copy(alpha = 0.6f),
                        modifier = Modifier
                            .padding(bottom = 16.dp)
                            .weight(0.5f)
                    )
                }

                expectedExpenses[lastMonth]?.let {
                    val lastMonthExpenses = expenses[lastMonth] ?: emptyMap()

                    if (it.isEmpty()) {
                        BudgetSummaryPlaceholder(
                            onLinkClicked = onBudgetsEmpty
                        )
                    } else {
                        BudgetSummary(
                            expenses = lastMonthExpenses,
                            expectedExpenses = it,
                            baseCurrency = baseCurrency
                        )
                    }


                } ?: run {
                    Log.d("BudgetCard", "Showing placeholder")
                    BudgetSummaryPlaceholder(onLinkClicked = {})
                }
            }
        } ?: Text(
            text = "No data available.",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Composable
fun OverallTransactionsCard(
    expenses: Map<YearMonth, Map<Category, Float>>,
    incomes: Map<YearMonth, Map<Category, Float>>,
    transactionsInterval: Pair<YearMonth, YearMonth>,
    baseCurrency: Currency,
) {
    TemporalChartByCategory(
        expenses = expenses,
        incomes = incomes,
        transactionsInterval = transactionsInterval,
        baseCurrency = baseCurrency,
        titleResId = R.string.transactions_title
    )
}

@Composable
fun OverallExpectedCard(
    expenses: Map<YearMonth, Map<Category, Float>>,
    incomes: Map<YearMonth, Map<Category, Float>>,
    expensesInterval: Pair<YearMonth, YearMonth>,
    baseCurrency: Currency,
) {
    TemporalChartByCategory(
        expenses = expenses,
        incomes = incomes,
        transactionsInterval = expensesInterval,
        baseCurrency = baseCurrency,
        titleResId = R.string.overall_expenses_title,
    )
}

@Composable
fun OverallBalancesCard(
    balances: Map<LocalDate, Float>,
    balancesDateRange: Pair<YearMonth, YearMonth>,
    baseCurrency: Currency,
    onRangeChanged: (fromDate: YearMonth, toDate: YearMonth) -> Unit = { _, _ -> }
) {
    val sortedBalances = balances.entries.sortedBy { it.key }
    val transformedData = sortedBalances.associate {
        it.key.toEpochDay().toFloat() to it.value
    }
    val chartEntryModel = entryModelOf(transformedData.keys.zip(transformedData.values, ::entryOf))
    val epochDaysList = transformedData.keys.toList()
    val dateTimeFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("dd MMM")
    val horizontalAxisValueFormatter =
        AxisValueFormatter<AxisPosition.Horizontal.Bottom> { value, _ ->
            // Find the index of this value in the list
            val index = epochDaysList.indexOf(value)

            // Only format every 4th date
            if (index != -1) {
                dateTimeFormatter.format(LocalDate.ofEpochDay(value.toLong()))
            } else {
                ""
            }
        }
    val startAxisValueFormatter = AxisValueFormatter<AxisPosition.Vertical.Start> { value, _ ->

        baseCurrency.formatAmount(value)
    }

    var showRangeDialog by remember { mutableStateOf(false) }

    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ), elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        ), modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp), horizontalAlignment = Alignment.Start
        ) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    text = stringResource(R.string.balances_title),
                    fontSize = MaterialTheme.typography.headlineSmall.fontSize,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
                //calendar icon to the right
                IconButton(
                    onClick = {
                        showRangeDialog = true
                    },
                ) {

                    Icon(
                        imageVector = Icons.Default.DateRange,
                        contentDescription = "Calendar",
                        tint = MaterialTheme.colorScheme.onSecondaryContainer,
                        modifier = Modifier.size(24.dp)
                    )

                }
            }

            ProvideChartStyle(chartStyle = m3ChartStyle()) {
                Chart(
                    chart = lineChart(),
                    model = chartEntryModel,
                    startAxis = rememberStartAxis(
                        valueFormatter = startAxisValueFormatter
                    ),
                    bottomAxis = rememberBottomAxis(
                        valueFormatter = horizontalAxisValueFormatter, labelRotationDegrees = 90f
                    ),
                    marker = rememberMarker(),
                )
            }
        }
    }

    if (showRangeDialog) {
        DateRangeDialog(isOpen = showRangeDialog, currentSelection = balancesDateRange, onClose = {
            onRangeChanged(it.first, it.second)
            showRangeDialog = false
        })
    }
}