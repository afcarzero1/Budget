package com.example.budgetapplication.ui.overall

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment

import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.budgetapplication.R
import com.example.budgetapplication.data.accounts.FullAccount
import com.example.budgetapplication.data.categories.Category
import com.example.budgetapplication.data.currencies.Currency
import com.example.budgetapplication.ui.AppViewModelProvider
import com.example.budgetapplication.ui.components.ColorAssigner
import com.example.budgetapplication.ui.components.DateRangeSelector
import com.example.budgetapplication.ui.components.PieChart
import com.example.budgetapplication.ui.navigation.Overview
import com.example.budgetapplication.ui.theme.InitialScreen
import java.time.LocalDate
import java.time.YearMonth

@Composable
fun OverallScreen(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    overallViewModel: OverallViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {

    val accounts by overallViewModel.accountsUiState.collectAsState()
    val accountsTotalBalance by overallViewModel.accountsTotalBalance.collectAsState()

    val lastExpenses by overallViewModel.lastExpenses.collectAsState()
    val currentTransactionsInterval by overallViewModel.currentDateRange.collectAsState()

    val expectedExpenses by overallViewModel.expectedExpenses.collectAsState()
    val expectedExpensesInterval by overallViewModel.expectedDateRange.collectAsState()

    val balances by overallViewModel.balancesByDay.collectAsState()
    val balancesInterval by overallViewModel.balanceDateRange.collectAsState()

    InitialScreen(navController = navController, destination = Overview, screenBody = {
        OverallScreenBody(
            currenctBalance = accountsTotalBalance,
            accounts = accounts.accountsList,
            accountsColorAssigner = overallViewModel.accountsColorAssigner,
            lastExpenses = lastExpenses,
            currentTransactionsInterval = currentTransactionsInterval,
            expectedExpenses = expectedExpenses,
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
    currentTransactionsInterval: Pair<YearMonth, YearMonth>,
    expectedExpenses: Map<YearMonth, Map<Category, Float>>,
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

        OverallTransactionsCard(
            expenses = lastExpenses,
            transactionsInterval = currentTransactionsInterval,
            onRangeChanged = onCurrentDateRangeChanged
        )

        OverallExpectedCard(
            expenses = expectedExpenses,
            expensesInterval = expectedExpensesInterval,
            onRangeChanged = onExpectedDateRangeChanged
        )

        OverallBalancesCard(
            balances = balances,
            balancesDateRange = balancesInterval,
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
                itemToWeight = { it.balance * (1 / it.currency.value) },
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
fun OverallTransactionsCard(
    expenses: Map<YearMonth, Map<Category, Float>>,
    transactionsInterval: Pair<YearMonth, YearMonth>,
    onRangeChanged: (fromDate: YearMonth, toDate: YearMonth) -> Unit = { _, _ -> }
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
            .padding(16.dp) //TODO: make this card a template in components
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(R.string.expenses_title),
                fontSize = MaterialTheme.typography.headlineSmall.fontSize,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            DateRangeSelector(
                startDate = transactionsInterval.first,
                endDate = transactionsInterval.second,
                onRangeChanged = onRangeChanged,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            expenses.forEach { (yearMonth, expensesMap) ->
                val totalExpenses = expensesMap.values.sum()

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "${yearMonth.year}-${yearMonth.monthValue}")
                    Text(text = "$totalExpenses")
                }
            }
        }
    }
}

@Composable
fun OverallExpectedCard(
    expenses: Map<YearMonth, Map<Category, Float>>,
    expensesInterval: Pair<YearMonth, YearMonth>,
    onRangeChanged: (fromDate: YearMonth, toDate: YearMonth) -> Unit = { _, _ -> }
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
            .padding(16.dp) //TODO: make this card a template in components
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text = stringResource(R.string.expected_expenses_title),
                fontSize = MaterialTheme.typography.headlineSmall.fontSize,
                fontWeight = MaterialTheme.typography.headlineSmall.fontWeight,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            DateRangeSelector(
                startDate = expensesInterval.first,
                endDate = expensesInterval.second,
                onRangeChanged = onRangeChanged,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            expenses.forEach { (yearMonth, expensesMap) ->
                val totalExpenses = expensesMap.values.sum()

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "${yearMonth.year}-${yearMonth.monthValue}")
                    expensesMap.forEach { (category, value) ->
                        Column() {
                            Text(text = category.name)
                            Text(text = value.toString())
                        }
                    }
                    Text(text = "$totalExpenses")

                }
            }
        }
    }


}

@Composable
fun OverallBalancesCard(
    balances: Map<LocalDate, Float>,
    balancesDateRange: Pair<YearMonth, YearMonth>,
    onRangeChanged: (fromDate: YearMonth, toDate: YearMonth) -> Unit = { _, _ -> }
) {

    val sortedBalances = balances.entries.sortedBy { it.key }
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
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(R.string.balances_title),
                fontSize = MaterialTheme.typography.headlineSmall.fontSize,
                fontWeight = MaterialTheme.typography.headlineSmall.fontWeight,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            DateRangeSelector(
                startDate = balancesDateRange.first,
                endDate = balancesDateRange.second,
                onRangeChanged = onRangeChanged,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            sortedBalances.forEach { (date, balance) ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = date.toString())
                    Text(text = balance.toString())
                }
            }
        }
    }
}