package com.example.budgetapplication.ui.accounts

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.budgetapplication.ui.AppViewModelProvider


@Composable
fun AccountSummaryScreen(
    navigateBack: () -> Unit,
    viewModel: AccountSummaryViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val accountUiState by viewModel.accountState.collectAsState()

    Column() {


        Card() {

            Row {
                Text(
                    modifier = Modifier.padding(start = 15.dp),
                    text = accountUiState.accountWithTransactions.account.name,
                    fontSize = MaterialTheme.typography.bodyLarge.fontSize,
                    fontWeight = MaterialTheme.typography.bodyLarge.fontWeight
                )

                Column() {
                    Text(
                        modifier = Modifier.padding(start = 15.dp),
                        text = accountUiState.accountWithTransactions.currency.formatAmount(
                            accountUiState.accountWithTransactions.balance
                        ),
                        fontWeight = MaterialTheme.typography.bodySmall.fontWeight,
                        fontSize = MaterialTheme.typography.bodySmall.fontSize
                    )

                    // TODO: Add here small how much more it has gained/lost in the current month
                }
            }
        }

        //TODO: Add here income vs income bar chart


    }
}