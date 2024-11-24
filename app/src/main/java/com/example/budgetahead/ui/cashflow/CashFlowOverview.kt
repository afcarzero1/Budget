package com.example.budgetahead.ui.cashflow

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.budgetahead.R
import com.example.budgetahead.data.currencies.Currency
import com.example.budgetahead.ui.AppViewModelProvider
import com.example.budgetahead.ui.navigation.SecondaryScreenTopBar
import com.example.budgetahead.ui.overall.CashFlow
import com.example.budgetahead.ui.overall.CashFlowCard
import com.example.budgetahead.ui.overall.SubdividedValue
import com.example.budgetahead.ui.transactions.GroupOfTransactionsAndTransfers
import com.example.budgetahead.ui.transactions.TransactionsSummaryBody
import java.time.LocalDateTime
import java.time.YearMonth

@Composable
fun CashFlowOverviewPage(
    navController: NavHostController,
    cashFlowOverviewViewModel: CashFlowOverviewViewModel =
        viewModel(factory = AppViewModelProvider.Factory),
) {
    val centralDate by cashFlowOverviewViewModel.dateToShowFlow.collectAsState()
    val monthCashFlow by cashFlowOverviewViewModel.executedCashFlow.collectAsState()
    val expectedCashFlow by cashFlowOverviewViewModel.expectedCashFlow.collectAsState()
    val plannedCashFlow by cashFlowOverviewViewModel.plannedCashFlow.collectAsState()

    val pendingTransactions by cashFlowOverviewViewModel.pendingTransactions.collectAsState()

    Scaffold(
        topBar = {
            SecondaryScreenTopBar(
                navigateBack = { navController.popBackStack() },
                title = stringResource(R.string.cashflow_overview),
            )
        },
    ) {
        CashFlowOverview(
            centralDate = centralDate,
            monthCashFlow = monthCashFlow,
            monthExpectedCashFlow = expectedCashFlow,
            monthPlannedCashFlow = plannedCashFlow,
            baseCurrency = monthCashFlow.currency,
            pendingTransactions = pendingTransactions,
            modifier = Modifier.padding(it),
        )
    }
}

@Composable
fun CashFlowOverview(
    centralDate: YearMonth,
    monthCashFlow: CashFlow,
    monthPlannedCashFlow: CashFlow,
    monthExpectedCashFlow: CashFlow,
    pendingTransactions: List<GroupOfTransactionsAndTransfers>,
    baseCurrency: Currency,
    modifier: Modifier = Modifier,
) {
    var isPlannedView by remember { mutableStateOf(true) }
    Column(
        modifier = modifier,
    ) {
        CashFlowCard(
            title = "Cashflow",
            registeredCashFlow = monthCashFlow,
            modifier = Modifier.fillMaxWidth().padding(8.dp),
            yearMonth = centralDate,
        )
        Card(
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 2.dp,
            ),
            modifier = Modifier.padding(8.dp)
        ){
            Box(modifier = Modifier.fillMaxWidth()) {
                IconButton(
                    onClick = { isPlannedView = !isPlannedView },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.sync_alt_24dp_5f6368_fill0_wght400_grad0_opsz24), // Replace with a desired icon
                        contentDescription = "Switch View"
                    )
                }
                Column(modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)) {
                    SubdividedValue(
                        title = if (isPlannedView) "Planned" else "Projected",
                        positiveValue = if (isPlannedView) monthPlannedCashFlow.ingoing else monthExpectedCashFlow.ingoing,
                        negativeValue = if (isPlannedView) monthPlannedCashFlow.outgoing else monthExpectedCashFlow.outgoing,
                        currency = baseCurrency,
                        smaller = true,
                    )
                }
            }
        }


        Row(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(
                text = "Pending Transactions",
                style = MaterialTheme.typography.titleMedium,
            )
        }

        Spacer(
            modifier = Modifier.height(8.dp),
        )

        TransactionsSummaryBody(
            pendingTransactions.reversed(),
            baseCurrency = baseCurrency.name,
            navController = null,
            modifier = Modifier.padding(horizontal = 16.dp),
        )
    }
}


@Preview
@Composable
fun CashFlowOverviewPreview(){

   CashFlowOverview(
        centralDate= YearMonth.now(),
        monthCashFlow= CashFlow(1000f, 1200f, Currency("USD", 1.0f, LocalDateTime.now())),
        monthPlannedCashFlow= CashFlow(1000f, 1200f, Currency("USD", 1.0f, LocalDateTime.now())),
        monthExpectedCashFlow= CashFlow(1000f, 1200f, Currency("USD", 1.0f, LocalDateTime.now())),
        pendingTransactions = listOf(),
        baseCurrency= Currency("USD", 1.0f, LocalDateTime.now()),
        modifier = Modifier.fillMaxWidth()
    )

}
