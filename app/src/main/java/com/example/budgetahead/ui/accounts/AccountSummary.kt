package com.example.budgetahead.ui.accounts

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.budgetahead.R
import com.example.budgetahead.ui.AppViewModelProvider
import com.example.budgetahead.ui.navigation.AccountDetails
import com.example.budgetahead.ui.navigation.SecondaryScreenTopBar

@Composable
fun AccountSummary(
    navController: NavHostController,
    viewModel: AccountSummaryViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val accountUiState by viewModel.accountState.collectAsState()
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            SecondaryScreenTopBar(
                navigateBack = { navController.popBackStack() },
                titleResId = R.string.details_account_title,
                actions = {
                    IconButton(
                        modifier = Modifier.padding(horizontal = 8.dp),
                        onClick = {
                            navController.navigate(
                                "${AccountDetails.route}/${accountUiState.accountWithTransactions.account.id}"
                            )
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Edit,
                            contentDescription = stringResource(R.string.edit),
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }
            )
        }
    ) {
        Column(
            modifier = Modifier.padding(it)
        ) {
            Card(
                colors =
                CardDefaults.cardColors(
                    contentColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Row {
                    Text(
                        modifier = Modifier.padding(start = 15.dp),
                        text = accountUiState.accountWithTransactions.account.name,
                        fontSize = MaterialTheme.typography.bodyLarge.fontSize,
                        fontWeight = MaterialTheme.typography.bodyLarge.fontWeight
                    )

                    Column {
                        Text(
                            modifier = Modifier.padding(start = 15.dp),
                            text =
                            accountUiState.accountWithTransactions.currency.formatAmount(
                                accountUiState.accountWithTransactions.balance
                            ),
                            fontWeight = MaterialTheme.typography.bodySmall.fontWeight,
                            fontSize = MaterialTheme.typography.bodySmall.fontSize
                        )
                    }
                }
            }

            // TODO: Add here income vs income bar chart
        }
    }
}
