package com.example.budgetapplication.ui.overall

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.budgetapplication.R
import com.example.budgetapplication.data.accounts.FullAccount
import com.example.budgetapplication.data.categories.Category
import com.example.budgetapplication.data.currencies.Currency
import com.example.budgetapplication.ui.AppViewModelProvider
import com.example.budgetapplication.ui.components.BudgetSummary
import com.example.budgetapplication.ui.components.ColorAssigner
import com.example.budgetapplication.ui.components.DateRangeSelector
import com.example.budgetapplication.ui.components.PieChart
import com.example.budgetapplication.ui.components.graphics.rememberMarker
import com.example.budgetapplication.ui.navigation.Overview
import com.example.budgetapplication.ui.theme.InitialScreen
import com.patrykandpatrick.vico.compose.axis.horizontal.rememberBottomAxis
import com.patrykandpatrick.vico.compose.axis.vertical.rememberStartAxis
import com.patrykandpatrick.vico.compose.chart.Chart
import com.patrykandpatrick.vico.compose.chart.column.columnChart
import com.patrykandpatrick.vico.compose.chart.line.lineChart
import com.patrykandpatrick.vico.compose.m3.style.m3ChartStyle
import com.patrykandpatrick.vico.compose.style.ProvideChartStyle
import com.patrykandpatrick.vico.core.axis.AxisPosition
import com.patrykandpatrick.vico.core.axis.formatter.AxisValueFormatter
import com.patrykandpatrick.vico.core.chart.column.ColumnChart
import com.patrykandpatrick.vico.core.component.shape.LineComponent
import com.patrykandpatrick.vico.core.entry.ChartEntryModelProducer
import com.patrykandpatrick.vico.core.entry.entryModelOf
import com.patrykandpatrick.vico.core.entry.entryOf
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import kotlin.math.abs

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

    InitialScreen(navController = navController, destination = Overview, screenBody = {
        OverallScreenBody(currentBalance = accountsTotalBalance,
            lastExpenses = lastExpenses,
            lastIncomes = lastIncomes,
            currentTransactionsInterval = currentTransactionsInterval,
            expectedExpenses = expectedExpenses,
            expectedIncomes = expectedIncomes,
            expectedExpensesInterval = expectedExpensesInterval,
            balances = balances,
            balancesInterval = balancesInterval,
            onExpectedDateRangeChanged = { fromDate, toDate ->
                overallViewModel.setExpectedRangeFlow(fromDate, toDate)
            },
            onCurrentDateRangeChanged = { fromDate, toDate ->
                overallViewModel.setCurrentRangeFlow(fromDate, toDate)
            },
            onBalancesDateRangeChanged = { fromDate, toDate ->
                overallViewModel.setBalanceRangeFlow(fromDate, toDate)
            })
    })
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
    onCurrentDateRangeChanged: (fromDate: YearMonth, toDate: YearMonth) -> Unit = { _, _ -> },
    onExpectedDateRangeChanged: (fromDate: YearMonth, toDate: YearMonth) -> Unit = { _, _ -> },
    onBalancesDateRangeChanged: (fromDate: YearMonth, toDate: YearMonth) -> Unit = { _, _ -> },
) {

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
    ) {
        OverallTransactionsCard(
            expenses = lastExpenses,
            incomes = lastIncomes,
            transactionsInterval = currentTransactionsInterval,
            baseCurrency = currentBalance.first,
            onRangeChanged = onCurrentDateRangeChanged
        )

        BudgetsCard(
            expenses = lastExpenses,
            expectedExpenses = expectedExpenses,
            baseCurrency = currentBalance.first,
        )


        OverallExpectedCard(
            expenses = expectedExpenses,
            incomes = expectedIncomes,
            expensesInterval = expectedExpensesInterval,
            baseCurrency = currentBalance.first,
            onRangeChanged = onExpectedDateRangeChanged
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
fun BudgetsCard(
    expenses: Map<YearMonth, Map<Category, Float>>,
    expectedExpenses: Map<YearMonth, Map<Category, Float>>,
    baseCurrency: Currency
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
            Column(modifier = Modifier.padding(16.dp)) {
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

                val lastMonthExpenses = expenses[lastMonth] ?: emptyMap()

                BudgetSummary(
                    expenses = lastMonthExpenses,
                    expectedExpenses = expectedExpenses[lastMonth] ?: emptyMap(),
                    baseCurrency = baseCurrency
                )

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
    onRangeChanged: (fromDate: YearMonth, toDate: YearMonth) -> Unit = { _, _ -> }
) {
    TemporalChartByCategory(
        expenses = expenses,
        incomes = incomes,
        transactionsInterval = transactionsInterval,
        baseCurrency = baseCurrency,
        onRangeChanged = onRangeChanged,
        titleResId = R.string.transactions_title
    )
}

@Composable
fun OverallExpectedCard(
    expenses: Map<YearMonth, Map<Category, Float>>,
    incomes: Map<YearMonth, Map<Category, Float>>,
    expensesInterval: Pair<YearMonth, YearMonth>,
    baseCurrency: Currency,
    onRangeChanged: (fromDate: YearMonth, toDate: YearMonth) -> Unit = { _, _ -> }
) {
    TemporalChartByCategory(
        expenses = expenses,
        incomes = incomes,
        transactionsInterval = expensesInterval,
        baseCurrency = baseCurrency,
        titleResId = R.string.overall_expenses_title,
        onRangeChanged = onRangeChanged
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
                    fontWeight = MaterialTheme.typography.headlineSmall.fontWeight,
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
                    startAxis = rememberStartAxis(),
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