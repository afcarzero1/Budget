package com.example.budgetapplication.ui.overall

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import com.example.budgetapplication.data.currencies.Currency
import com.example.budgetapplication.data.transactions.FullTransactionRecord
import com.example.budgetapplication.ui.AppViewModelProvider
import com.example.budgetapplication.ui.components.PieChart
import com.example.budgetapplication.ui.navigation.Overview
import com.example.budgetapplication.ui.theme.InitialScreen
import java.lang.Math.abs
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

    InitialScreen(navController = navController, destination = Overview, screenBody = {
        OverallScreenBody(
            currenctBalance = accountsTotalBalance,
            accounts = accounts.accountsList,
            lastExpenses = lastExpenses
        )
    })
}

@Composable
fun OverallScreenBody(
    currenctBalance: Pair<Currency, Float>,
    accounts: List<FullAccount>,
    lastExpenses: Map<YearMonth, Float>
    ) {

    Column {
        OverallAccountsCard(
            accounts = accounts,
            currentBalance = currenctBalance
        )

        OverallExpensesCard(lastMonthExpenses = lastExpenses)


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
                itemToWeight = {it.balance * (1 / it.currency.value)},
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
    lastMonthExpenses : Map<YearMonth, Float>
){
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
    ){





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