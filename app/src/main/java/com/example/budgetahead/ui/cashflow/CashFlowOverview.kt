package com.example.budgetahead.ui.cashflow

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.budgetahead.R
import com.example.budgetahead.ui.AppViewModelProvider
import com.example.budgetahead.ui.navigation.SecondaryScreenTopBar
import com.example.budgetahead.ui.overall.CashFlow
import com.example.budgetahead.ui.overall.CashFlowCard
import com.example.budgetahead.ui.transactions.GroupOfTransactionsAndTransfers
import com.example.budgetahead.ui.transactions.TransactionsSummaryBody
import java.time.YearMonth

@Composable
fun CashFlowOverviewPage(
    navController: NavHostController,
    cashFlowOverviewViewModel: CashFlowOverviewViewModel =
        viewModel(factory = AppViewModelProvider.Factory)
) {
    val centralDate by cashFlowOverviewViewModel.dateToShowFlow.collectAsState()
    val monthCashFlow by cashFlowOverviewViewModel.executedCashFlow.collectAsState()
    val expectedCashFlow by cashFlowOverviewViewModel.expectedCashFlow.collectAsState()

    val pendingTransactions by cashFlowOverviewViewModel.pendingTransactions.collectAsState()
    val baseCurrency by cashFlowOverviewViewModel.baseCurrency.collectAsState()

    Scaffold(
        topBar = {
            SecondaryScreenTopBar(
                navigateBack = { navController.popBackStack() },
                title = stringResource(R.string.cashflow_overview)
            )
        }
    ) {
        CashFlowOverview(
            centralDate = centralDate,
            monthCashFlow = monthCashFlow,
            monthExpectedCashFlow = expectedCashFlow,
            monthPlannedCashFlow = expectedCashFlow,
            baseCurrency = baseCurrency,
            pendingTransactions = pendingTransactions,
            modifier = Modifier.padding(it)
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
    baseCurrency: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
    ) {
        CashFlowCard(
            title = "Cashflow",
            registeredCashFlow = monthCashFlow,
            modifier = Modifier.fillMaxWidth(),
            yearMonth = centralDate
        )

        Row {
            CashFlowCard(
                title = "Planned",
                registeredCashFlow = monthPlannedCashFlow,
                modifier = Modifier.weight(1f),
                yearMonth = centralDate
            )
            CashFlowCard(
                title = "Expected",
                registeredCashFlow = monthExpectedCashFlow,
                modifier = Modifier.weight(1f),
                yearMonth = centralDate
            )
        }

        TransactionsSummaryBody(
            pendingTransactions,
            baseCurrency = baseCurrency,
            navController = null
        )
    }
}
