package com.example.budgetapplication.ui.overall

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.stringResource
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

    val accounts by overallViewModel.accountsUiState.collectAsState()
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
        OverallScreenBody(
            currenctBalance = accountsTotalBalance,
            accounts = accounts.accountsList,
            accountsColorAssigner = overallViewModel.accountsColorAssigner,
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
            }
        )
    }
    )
}

@Composable
fun OverallScreenBody(
    currenctBalance: Pair<Currency, Float>,
    accounts: List<FullAccount>,
    accountsColorAssigner: ColorAssigner,
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
        OverallAccountsCard(
            accounts = accounts,
            accountColorAssigner = accountsColorAssigner,
            currentBalance = currenctBalance,
        )

        BudgetsCard(
            expenses = lastExpenses,
            expectedExpenses = expectedExpenses,
            baseCurrency = currenctBalance.first,
        )

        OverallTransactionsCard(
            expenses = lastExpenses,
            incomes = lastIncomes,
            transactionsInterval = currentTransactionsInterval,
            baseCurrency = currenctBalance.first,
            onRangeChanged = onCurrentDateRangeChanged
        )

        OverallExpectedCard(
            expenses = expectedExpenses,
            incomes = expectedIncomes,
            expensesInterval = expectedExpensesInterval,
            baseCurrency = currenctBalance.first,
            onRangeChanged = onExpectedDateRangeChanged
        )

        OverallBalancesCard(
            balances = balances,
            balancesDateRange = balancesInterval,
            baseCurrency = currenctBalance.first,
            onRangeChanged = onBalancesDateRangeChanged
        )
    }

}


@Composable
fun OverallAccountsCard(
    accounts: List<FullAccount>,
    accountColorAssigner: ColorAssigner,
    currentBalance: Pair<Currency, Float>,
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Column {
            PieChart(
                data = accounts,
                middleText = currentBalance.first.formatAmount(currentBalance.second),
                itemToWeight = { if (it.balance > 0) it.balance * (1 / it.currency.value) else 0f },
                itemDetails = {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            modifier = Modifier.padding(start = 15.dp),
                            text = it.account.name,
                            fontSize = MaterialTheme.typography.bodyLarge.fontSize,
                            fontWeight = MaterialTheme.typography.bodyLarge.fontWeight
                        )
                        Text(
                            modifier = Modifier.padding(start = 15.dp),
                            text = it.currency.formatAmount(it.balance),
                            fontWeight = MaterialTheme.typography.bodySmall.fontWeight,
                            fontSize = MaterialTheme.typography.bodySmall.fontSize,
                            color = Color.Gray
                        )
                    }
                },
                itemToColor = {
                    accountColorAssigner.assignColor(it.account.name)
                }
            )
        }
    }
}


@Composable
fun BudgetsCard(
    expenses: Map<YearMonth, Map<Category, Float>>,
    expectedExpenses: Map<YearMonth, Map<Category, Float>>,
    baseCurrency: Currency
) {
    // Determine the last month available in the data
    val lastMonth = (expectedExpenses.keys).maxOrNull()
    Log.d("OverallScreen", "Attempting to show budgets of: $lastMonth")

    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        lastMonth?.let {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Budgets",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

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
    var showDialog by remember { mutableStateOf(false) }
    var showDateDialog by remember { mutableStateOf(false) }
    var selectedCategoryMap by remember { mutableStateOf<Map<Category, Float>>(mapOf()) }

    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp) //TODO: make this card a template in components
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = stringResource(R.string.transactions_title),
                    fontSize = MaterialTheme.typography.headlineSmall.fontSize,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                IconButton(
                    onClick = { showDateDialog = true },
                    modifier = Modifier.padding(start = 8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.DateRange,
                        contentDescription = "Calendar",
                        tint = MaterialTheme.colorScheme.onSecondaryContainer,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            MonthExpensesChart(expenses = expenses, incomes = incomes)

            Spacer(modifier = Modifier.height(16.dp))

            expenses.forEach { (yearMonth, expensesMap) ->
                val totalExpenses = expensesMap.values.sum()
                val incomesMap = incomes[yearMonth] ?: mapOf()
                val totalIncomes = incomesMap.values.sum()

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Date
                    Text(
                        text = "${yearMonth.year}-${yearMonth.monthValue}",
                        style = MaterialTheme.typography.bodyLarge
                    )

                    // Total Expenses
                    Text(
                        text = baseCurrency.formatAmount(totalExpenses),
                        color = Color(0xFFCF3B2A),  // Color indicating expense
                        style = MaterialTheme.typography.bodyMedium
                    )

                    // Total Incomes
                    Text(
                        text = baseCurrency.formatAmount(totalIncomes),
                        color = Color(0xFF68A462),  // Color indicating income
                        style = MaterialTheme.typography.bodyMedium
                    )

                    // Icon for details
                    Icon(
                        imageVector = Icons.Default.ArrowForward,  // You can choose any other appropriate icon.
                        contentDescription = "Details",
                        modifier = Modifier
                            .size(24.dp)
                            .clickable {
                                selectedCategoryMap =
                                    expensesMap + incomesMap
                                showDialog = true
                            }
                    )
                }
            }
        }
    }

    DateRangeDialog(
        isOpen = showDateDialog,
        currentSelection = transactionsInterval,
        onClose = {
            showDateDialog = false
            onRangeChanged(it.first, it.second)
                  },
    )

    CategoryDialog(
        isOpen = showDialog,
        onClose = { showDialog = false },
        categoryMap = selectedCategoryMap
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

    var showCategoriesDialog by remember { mutableStateOf(false) }
    var showDateRangeDialog by remember { mutableStateOf(false) }
    var selectedCategoryMap by remember { mutableStateOf<Map<Category, Float>>(mapOf()) }

    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp) //TODO: make this card a template in components
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = stringResource(R.string.expected_expenses_title),
                    fontSize = MaterialTheme.typography.headlineSmall.fontSize,
                    fontWeight = MaterialTheme.typography.headlineSmall.fontWeight,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
                IconButton(
                    onClick = {
                        showDateRangeDialog = true
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

            MonthExpensesChart(expenses = expenses, incomes = incomes)

            Spacer(modifier = Modifier.height(16.dp))

            expenses.forEach { (yearMonth, expensesMap) ->
                val totalExpenses = expensesMap.values.sum()
                val incomesMap = incomes[yearMonth] ?: mapOf()
                val totalIncomes = incomesMap.values.sum()

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Date
                    Text(
                        text = "${yearMonth.year}-${yearMonth.monthValue}",
                        style = MaterialTheme.typography.bodyLarge
                    )

                    // Total Expenses
                    Text(
                        text = baseCurrency.formatAmount(totalExpenses),
                        color = Color(0xFFCF3B2A),  // Color indicating expense
                        style = MaterialTheme.typography.bodyMedium
                    )

                    // Total Incomes
                    Text(
                        text = baseCurrency.formatAmount(totalIncomes),
                        color = Color(0xFF68A462),  // Color indicating income
                        style = MaterialTheme.typography.bodyMedium
                    )

                    // Icon for details
                    Icon(
                        imageVector = Icons.Default.ArrowForward,  // You can choose any other appropriate icon.
                        contentDescription = "Details",
                        modifier = Modifier
                            .size(24.dp)
                            .clickable {
                                selectedCategoryMap =
                                    expensesMap + incomesMap
                                showCategoriesDialog = true
                            }
                    )
                }
            }
        }
    }
    CategoryDialog(
        isOpen = showCategoriesDialog,
        onClose = { showCategoriesDialog = false },
        categoryMap = selectedCategoryMap
    )
    if(showDateRangeDialog){
        DateRangeDialog(
            isOpen = showDateRangeDialog,
            onClose = {
                showDateRangeDialog = false
                onRangeChanged(it.first, it.second)
            },
            currentSelection = expensesInterval,
        )
    }
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
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.Start
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
                        valueFormatter = horizontalAxisValueFormatter,
                        labelRotationDegrees = 90f
                    ),
                    marker = rememberMarker(),
                )
            }
        }
    }

    if(showRangeDialog){
        DateRangeDialog(
            isOpen = showRangeDialog,
            currentSelection = balancesDateRange ,
            onClose = {
                onRangeChanged(it.first, it.second)
                showRangeDialog = false
            }
        )
    }
}

@Composable
fun MonthExpensesChart(
    expenses: Map<YearMonth, Map<Category, Float>>,
    incomes: Map<YearMonth, Map<Category, Float>>
){
    val sortedExpenses = expenses.entries.sortedBy { it.key }
    val sortedIncomes = incomes.entries.sortedBy { it.key }

    val transformedExpenses = sortedExpenses.mapIndexed { index, it ->
        index to abs(it.value.values.sum())
    }
    val transformedIncomes = sortedIncomes.mapIndexed { index, it ->
        index to abs(it.value.values.sum())
    }

    val indexToDate = sortedExpenses.mapIndexed() { index, it ->
        index to it.key
    }.toMap()

    val chartEntryModelProducer = ChartEntryModelProducer(
        transformedExpenses.map { (k,v) -> entryOf(k,v) },
        transformedIncomes.map { (k,v) -> entryOf(k,v) },
    )

    val dateTimeFormatter = DateTimeFormatter.ofPattern("MM y")

    val horizontalAxisValueFormatter =
        AxisValueFormatter<AxisPosition.Horizontal.Bottom> { value, _ ->
            // Find the index of this value in the list
            val date = indexToDate[value.toInt()]

            date?.let { dateTimeFormatter.format(it) } ?: ""
        }

    val columnChart = columnChart(
        columns = listOf(
            LineComponent(
                color = Color(0xFFCC3333).toArgb(),
                thicknessDp = 3f
            ),
            LineComponent(
                color = Color(0xFF33CC33).toArgb(),
                thicknessDp = 3f
            )
        ),
        mergeMode = ColumnChart.MergeMode.Grouped,
    )

    ProvideChartStyle(chartStyle = m3ChartStyle()) {
        Chart(
            chart = columnChart,
            chartModelProducer = chartEntryModelProducer,
            startAxis = rememberStartAxis(),
            bottomAxis = rememberBottomAxis(
                valueFormatter = horizontalAxisValueFormatter,
                labelRotationDegrees = 90f
            ),
        )
    }

}


@Composable
fun DateRangeDialog(
    isOpen: Boolean,
    currentSelection: Pair<YearMonth, YearMonth>,
    onClose: (Pair<YearMonth, YearMonth>) -> Unit,
){
    var startDate by remember { mutableStateOf(currentSelection.first) }
    var endDate by remember { mutableStateOf(currentSelection.second) }

    if (isOpen){
        AlertDialog(
            onDismissRequest = {onClose(currentSelection)},
            title = { Text(text = "Select Date Range") },
            text = {
                DateRangeSelector(
                    startDate = startDate,
                    endDate = endDate ,
                    onRangeChanged = { start, end ->
                        startDate = start
                        endDate = end
                    }
                )
            },
            confirmButton = {
                Button(onClick = {
                    onClose(Pair(startDate, endDate))
                }) {
                    Text("Close")
                }
            },
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
        )
    }
}

@Composable
fun CategoryDialog(
    isOpen: Boolean,
    onClose: () -> Unit,
    categoryMap: Map<Category, Float>
) {
    if (isOpen) {
        AlertDialog(
            onDismissRequest = onClose,
            title = { Text(text = "Category Details") },
            text = {
                Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                    categoryMap.forEach { (category, value) ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = category.name,
                                style = MaterialTheme.typography.bodyLarge
                            )
                            Text(
                                text = value.toString(),
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(onClick = onClose) {
                    Text("Close")
                }
            }
        )
    }
}
