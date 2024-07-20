package com.example.budgetahead.ui.categories

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.budgetahead.R
import com.example.budgetahead.data.categories.Category
import com.example.budgetahead.data.currencies.Currency
import com.example.budgetahead.ui.AppViewModelProvider
import com.example.budgetahead.ui.navigation.CategoryDetails
import com.example.budgetahead.ui.navigation.SecondaryScreenTopBar
import com.example.budgetahead.ui.transactions.GroupOfTransactionsAndTransfers
import com.example.budgetahead.ui.transactions.TransactionsSummaryBody

@Composable
fun CategoryOverviewScreen(
    navController: NavHostController,
    viewModel: CategoryOverviewViewModel = viewModel(factory = AppViewModelProvider.Factory)
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
                                "${CategoryDetails.route}/${categoryUiState.category.id}"
                            )
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Edit,
                            contentDescription = stringResource(R.string.edit),
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                },
                containerColor = MaterialTheme.colorScheme.primary
            )
        }
    ) {
        CategorySummaryBody(
            category = categoryUiState.category,
            categoryTransactions = categoryUiState.transactions,
            baseCurrency = baseCurrency,
            navController = navController,
            modifier = Modifier
                .padding(it)
                .fillMaxSize()
        )
    }
}

@Composable
fun CategorySummaryBody(
    category: Category,
    categoryTransactions: List<GroupOfTransactionsAndTransfers>,
    baseCurrency: String,
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.background(
            MaterialTheme.colorScheme.background
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    bottom = 16.dp
                )
                .background(
                    color = MaterialTheme.colorScheme.secondary
                ),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                modifier = Modifier.padding(horizontal = 16.dp),
                text = category.name,
                color = MaterialTheme.colorScheme.onSecondary,
                style = MaterialTheme.typography.titleLarge
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                modifier = Modifier.padding(horizontal = 16.dp),
                text = Currency.formatAmountStatic(
                    baseCurrency,
                    categoryTransactions.sumOf {
                        it.transactions.sumOf { it.transactionRecord.amount.toDouble() }
                    }.toFloat()
                ),
                color = MaterialTheme.colorScheme.onSecondary,
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold)
            )

            Spacer(modifier = Modifier.height(32.dp))
        }

        Spacer(modifier = Modifier.height(8.dp))

        TransactionsSummaryBody(
            transactions = categoryTransactions,
            baseCurrency = baseCurrency,
            navController = navController,
            modifier = Modifier.padding(horizontal = 16.dp),
            dividerColor = MaterialTheme.colorScheme.background
        )
    }
}
