package com.example.budgetapplication.ui.overall

import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue

import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.budgetapplication.data.accounts.FullAccount
import com.example.budgetapplication.data.categories.Category
import com.example.budgetapplication.data.currencies.Currency
import com.example.budgetapplication.data.transactions.FullTransactionRecord
import com.example.budgetapplication.ui.AppViewModelProvider
import com.example.budgetapplication.ui.components.PieChart
import com.example.budgetapplication.ui.navigation.Overview
import com.example.budgetapplication.ui.theme.InitialScreen
import java.lang.Math.abs
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
    val expectedExpenses by overallViewModel.expectedExpenses.collectAsState()

    val balances by overallViewModel.balancesByDay.collectAsState()

    InitialScreen(navController = navController, destination = Overview, screenBody = {
        OverallScreenBody(
            currenctBalance = accountsTotalBalance,
            accounts = accounts.accountsList,
            lastExpenses = lastExpenses,
            expectedExpenses = expectedExpenses,
            balances = balances
        )
    }
    )
}

@Composable
fun OverallScreenBody(
    currenctBalance: Pair<Currency, Float>,
    accounts: List<FullAccount>,
    lastExpenses: Map<YearMonth, Map<Category, Float>>,
    expectedExpenses: Map<YearMonth, Map<Category, Float>>,
    balances: Map<LocalDate, Float>
) {

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
    ) {
        OverallAccountsCard(
            accounts = accounts,
            currentBalance = currenctBalance
        )

        OverallExpensesCard(lastMonthExpenses = lastExpenses)

        OverallExpectedExpensesCard(expenses = expectedExpenses)

        OverallBalancesCard(balances = balances)
    }

}


@Composable
fun OverallAccountsCard(
    accounts: List<FullAccount>,
    currentBalance: Pair<Currency, Float>
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
                middleText = "${currentBalance.first.name} ${currentBalance.second}",
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
                            text = "${it.balance} ${it.currency.name}",
                            fontWeight = MaterialTheme.typography.bodySmall.fontWeight,
                            fontSize = MaterialTheme.typography.bodySmall.fontSize,
                            color = Color.Gray
                        )
                    }
                },
                itemToColor = {
                    colorFromNameHash(it.account.name)
                }
            )
        }
    }
}

@Composable
fun OverallExpensesCard(
    lastMonthExpenses: Map<YearMonth, Map<Category, Float>>
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
                .padding(16.dp)
        ) {
            lastMonthExpenses.forEach { (yearMonth, expensesMap) ->
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
fun OverallIncomesCard(
    incomes: Map<YearMonth, Map<Category, Float>>
) {


}

@Composable
fun OverallExpectedExpensesCard(
    expenses: Map<YearMonth, Map<Category, Float>>
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
                .padding(16.dp)
        ) {

            Text(
                text = "Expected Expenses",
                fontSize = MaterialTheme.typography.headlineSmall.fontSize
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
fun OverallExpectedIncomesCard(

) {

}


@Composable
fun OverallBalancesCard(
    balances: Map<LocalDate, Float>
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
            .padding(16.dp) //TODO: make this card a template in components
    ) {

        Column {
            Text(
                text = "Balances",
                fontSize = MaterialTheme.typography.headlineSmall.fontSize
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

// Function to generate a color based on the hash of the name
fun colorFromNameHash(name: String): Color {
    val colors = listOf(
        Color(0xFFBB86FC),
        Color(0xFF6200EE),
        Color(0xFF3700B3),
        Color(0xFF03DAC5),
        Color(0xFF007BFF)
    )
    // Use a hash of the name to generate a color
    val hash = name.hashCode()
    val index = abs(hash) % colors.size
    return colors[index]
}