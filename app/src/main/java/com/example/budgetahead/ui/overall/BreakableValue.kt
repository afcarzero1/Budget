package com.example.budgetahead.ui.overall

import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.budgetahead.R
import com.example.budgetahead.data.categories.Category
import com.example.budgetahead.data.currencies.Currency
import com.example.budgetahead.ui.components.DateRangeSelector
import com.patrykandpatrick.vico.compose.axis.horizontal.rememberBottomAxis
import com.patrykandpatrick.vico.compose.axis.vertical.rememberStartAxis
import com.patrykandpatrick.vico.compose.chart.Chart
import com.patrykandpatrick.vico.compose.chart.column.columnChart
import com.patrykandpatrick.vico.compose.m3.style.m3ChartStyle
import com.patrykandpatrick.vico.compose.style.ProvideChartStyle
import com.patrykandpatrick.vico.core.axis.AxisPosition
import com.patrykandpatrick.vico.core.axis.formatter.AxisValueFormatter
import com.patrykandpatrick.vico.core.chart.column.ColumnChart
import com.patrykandpatrick.vico.core.component.shape.LineComponent
import com.patrykandpatrick.vico.core.entry.ChartEntryModelProducer
import com.patrykandpatrick.vico.core.entry.entryOf
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import kotlin.math.abs

@Composable
fun TemporalChartByCategory(
    expenses: Map<YearMonth, Map<Category, Float>>,
    incomes: Map<YearMonth, Map<Category, Float>>,
    transactionsInterval: Pair<YearMonth, YearMonth>,
    baseCurrency: Currency,
    @StringRes titleResId: Int,
    onRangeChanged: ((fromDate: YearMonth, toDate: YearMonth) -> Unit)? = null,
) {
    var showDateDialog by remember { mutableStateOf(false) }
    var showDetails by remember { mutableStateOf(false) }

    Card(
        colors =
            CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface,
            ),
        elevation =
            CardDefaults.cardElevation(
                defaultElevation = 2.dp,
            ),
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(16.dp),
    ) {
        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Row(
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
            ) {
                Text(
                    text = stringResource(titleResId),
                    fontSize = MaterialTheme.typography.headlineSmall.fontSize,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(start = 8.dp),
                )
                Spacer(modifier = Modifier.weight(1f))
                Row(
                    modifier = Modifier.padding(start = 16.dp, end = 8.dp),
                ) {
                    IconButton(
                        onClick = { showDetails = !showDetails },
                    ) {
                        Icon(
                            painter =
                                if (!showDetails) {
                                    painterResource(
                                        id = R.drawable.toc_24dp_fill0_wght400_grad0_opsz24,
                                    )
                                } else {
                                    painterResource(
                                        id = R.drawable.monitoring_24dp_fill0_wght400_grad0_opsz24,
                                    )
                                },
                            contentDescription = "Calendar",
                            tint = MaterialTheme.colorScheme.onSecondaryContainer,
                            modifier = Modifier.size(24.dp),
                        )
                    }
                    onRangeChanged?.let {
                        IconButton(
                            onClick = { showDateDialog = true },
                        ) {
                            Icon(
                                imageVector = Icons.Default.DateRange,
                                contentDescription = "Calendar",
                                tint = MaterialTheme.colorScheme.onSecondaryContainer,
                                modifier = Modifier.size(24.dp),
                            )
                        }
                    }
                }
            }

            AnimatedVisibility(visible = showDetails) {
                Column(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .wrapContentHeight(),
                ) {
                    expenses.forEach { (yearMonth, expensesMap) ->
                        val incomesMap = incomes[yearMonth] ?: mapOf()
                        Card(
                            modifier = Modifier.padding(vertical = 8.dp),
                            colors =
                                CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                                ),
                        ) {
                            Row(
                                modifier =
                                    Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                horizontalArrangement = Arrangement.Start,
                            ) {
                                Text(
                                    text =
                                        DateTimeFormatter
                                            .ofPattern("MMMM yyyy")
                                            .format(yearMonth),
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.SemiBold,
                                )
                            }

                            Row(
                                modifier =
                                    Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                            ) {
                                BreakableValue(
                                    breakedValue = expensesMap,
                                    baseCurrency = baseCurrency,
                                    color = Color(0xFFCF3B2A),
                                    modifier = Modifier.padding(horizontal = 8.dp),
                                )

                                BreakableValue(
                                    breakedValue = incomesMap,
                                    baseCurrency = baseCurrency,
                                    color = Color(0xFF68A462),
                                    modifier = Modifier.padding(horizontal = 8.dp),
                                )
                            }
                        }
                    }
                }
            }

            AnimatedVisibility(
                visible = !showDetails,
                enter =
                    fadeIn(animationSpec = tween(500)) +
                        expandVertically(
                            animationSpec =
                                tween(
                                    500,
                                ),
                        ),
                exit =
                    fadeOut(animationSpec = tween(500)) +
                        shrinkVertically(
                            animationSpec =
                                tween(
                                    500,
                                ),
                        ),
            ) {
                MonthExpensesChart(
                    expenses = expenses,
                    incomes = incomes,
                    baseCurrency = baseCurrency,
                )
            }
        }
    }
    onRangeChanged?.let {
        DateRangeDialog(
            isOpen = showDateDialog,
            currentSelection = transactionsInterval,
            onClose = {
                showDateDialog = false
                onRangeChanged(it.first, it.second)
            },
        )
    }
}

@Composable
fun BreakableValue(
    breakedValue: Map<Category, Float>,
    baseCurrency: Currency,
    color: Color,
    modifier: Modifier = Modifier,
) {
    val totalValue = breakedValue.values.sum()
    var expanded by remember { mutableStateOf(false) }

    Column(modifier = modifier) {
        Row(
            modifier = Modifier.wrapContentWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = baseCurrency.formatAmount(totalValue),
                color = color,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(end = 4.dp),
            )
            IconButton(
                onClick = {
                    expanded = !expanded
                },
                modifier = Modifier.padding(horizontal = 4.dp),
            ) {
                Icon(
                    imageVector = if (expanded) Icons.Filled.KeyboardArrowUp else Icons.Filled.ArrowDropDown,
                    contentDescription = "Dropdown",
                )
            }
        }

        if (expanded) {
            breakedValue.forEach { (category, value) ->
                Column(
                    modifier =
                        Modifier
                            .wrapContentWidth()
                            .padding(8.dp),
                    horizontalAlignment = Alignment.Start,
                    verticalArrangement = Arrangement.Center,
                ) {
                    Text(
                        text = category.name,
                        style = MaterialTheme.typography.labelLarge,
                    )
                    Text(
                        text = baseCurrency.formatAmount(value),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Light,
                        color = color,
                    )
                }
            }
        }
    }
}

@Composable
fun MonthExpensesChart(
    expenses: Map<YearMonth, Map<Category, Float>>,
    incomes: Map<YearMonth, Map<Category, Float>>,
    baseCurrency: Currency,
) {
    val sortedExpenses = expenses.entries.sortedBy { it.key }
    val sortedIncomes = incomes.entries.sortedBy { it.key }

    val transformedExpenses =
        sortedExpenses.mapIndexed { index, it ->
            index to abs(it.value.values.sum())
        }
    val transformedIncomes =
        sortedIncomes.mapIndexed { index, it ->
            index to abs(it.value.values.sum())
        }

    val indexToDate =
        sortedExpenses
            .mapIndexed { index, it ->
                index to it.key
            }.toMap()

    val chartEntryModelProducer =
        ChartEntryModelProducer(
            transformedExpenses.map { (k, v) -> entryOf(k, v) },
            transformedIncomes.map { (k, v) -> entryOf(k, v) },
        )

    val dateTimeFormatter = DateTimeFormatter.ofPattern("MM y")

    val horizontalAxisValueFormatter =
        AxisValueFormatter<AxisPosition.Horizontal.Bottom> { value, _ ->
            // Find the index of this value in the list
            val date = indexToDate[value.toInt()]

            date?.let { dateTimeFormatter.format(it) } ?: ""
        }
    val startAxisValueFormatter =
        AxisValueFormatter<AxisPosition.Vertical.Start> { value, _ ->

            baseCurrency.formatAmount(value)
        }

    val columnChart =
        columnChart(
            columns =
                listOf(
                    LineComponent(
                        color = Color(0xFFCC3333).toArgb(),
                        thicknessDp = 3f,
                    ),
                    LineComponent(
                        color = Color(0xFF33CC33).toArgb(),
                        thicknessDp = 3f,
                    ),
                ),
            mergeMode = ColumnChart.MergeMode.Grouped,
        )

    ProvideChartStyle(chartStyle = m3ChartStyle()) {
        Chart(
            chart = columnChart,
            chartModelProducer = chartEntryModelProducer,
            startAxis =
                rememberStartAxis(
                    valueFormatter = startAxisValueFormatter,
                ),
            bottomAxis =
                rememberBottomAxis(
                    valueFormatter = horizontalAxisValueFormatter,
                    labelRotationDegrees = 90f,
                ),
            runInitialAnimation = false,
        )
    }
}

@Composable
fun DateRangeDialog(
    isOpen: Boolean,
    currentSelection: Pair<YearMonth, YearMonth>,
    onClose: (Pair<YearMonth, YearMonth>) -> Unit,
) {
    var startDate by remember { mutableStateOf(currentSelection.first) }
    var endDate by remember { mutableStateOf(currentSelection.second) }

    if (isOpen) {
        AlertDialog(
            onDismissRequest = { onClose(currentSelection) },
            title = {
                Text(
                    text = "Select Date Range",
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                )
            },
            text = {
                DateRangeSelector(
                    startDate = startDate,
                    endDate = endDate,
                    onRangeChanged = { start, end ->
                        startDate = start
                        endDate = end
                    },
                    modifier = Modifier.fillMaxWidth(),
                )
            },
            confirmButton = {
                Button(onClick = {
                    onClose(Pair(startDate, endDate))
                }) {
                    Text("Close")
                }
            },
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            titleContentColor = MaterialTheme.colorScheme.primary,
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewDateRangeDialog() {
    DateRangeDialog(
        isOpen = true,
        currentSelection = Pair(YearMonth.now(), YearMonth.now().plusMonths(1)),
        onClose = {},
    )
}
