package com.example.budgetahead.ui.accounts

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.budgetahead.R
import com.example.budgetahead.data.accounts.Account
import com.example.budgetahead.data.accounts.FullAccount
import com.example.budgetahead.data.currencies.Currency
import com.example.budgetahead.ui.AppViewModelProvider
import com.example.budgetahead.ui.components.BaseRow
import com.example.budgetahead.ui.components.ColorAssigner
import com.example.budgetahead.ui.components.PieChart
import com.example.budgetahead.ui.components.TextPiece
import com.example.budgetahead.ui.components.graphics.convertLongToColor
import com.example.budgetahead.ui.navigation.AccountEntry
import com.example.budgetahead.ui.navigation.AccountSummary
import com.example.budgetahead.ui.navigation.AccountTransferEntry
import com.example.budgetahead.ui.navigation.Accounts
import com.example.budgetahead.ui.theme.InitialScreen

@Suppress("ktlint:standard:function-naming")
@Composable
fun AccountsSummary(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    viewModel: AccountsViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val accountsTotalBalance by viewModel.accountsTotalBalance.collectAsState()

    InitialScreen(navController = navController, destination = Accounts, screenBody = {
        val accountsState by viewModel.accountsUiState.collectAsState()

        if (accountsState.accountsList.isEmpty()) {
            EmptyAccountScreen()
        } else {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                AccountsSummaryBody(
                    accounts = accountsState.accountsList,
                    accountsTotalBalance = accountsTotalBalance,
                    accountColorAssigner = viewModel.accountsColorAssigner,
                    onAccountClick = {
                        navController.navigate("${AccountSummary.route}/${it.id}")
                    },
                    onAccountTransfer = {
                        navController.navigate(AccountTransferEntry.route)
                    }
                )
            }
        }
    }, floatingButton = {
        FloatingActionButton(
            onClick = {
                navController.navigate(AccountEntry.route)
            },
            modifier = Modifier.padding(16.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.Add,
                contentDescription = "Add Account"
            )
        }
    })
}

@Suppress("ktlint:standard:function-naming")
@Composable
fun AccountsSummaryBody(
    accounts: List<FullAccount>,
    accountsTotalBalance: Pair<Currency, Float>,
    accountColorAssigner: ColorAssigner,
    onAccountClick: (account: Account) -> Unit,
    onAccountTransfer: () -> Unit
) {
    val amountByCurrency: MutableMap<String, Float> = mutableMapOf()

    val baseCurrency = accountsTotalBalance.first

    for (account in accounts) {
        amountByCurrency[account.account.currency] =
            amountByCurrency.getOrElse(account.account.currency, { 0f }) + account.balance
    }

    Column {
        // TODO: Show here the total amount for each currency
        Box(
            modifier = Modifier.fillMaxSize() // This will make the Box occupy all available space
        ) {
            Card(
                colors =
                CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation =
                CardDefaults.cardElevation(
                    defaultElevation = 2.dp
                ),
                modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Column {
                    PieChart(
                        data = accounts,
                        chartBarWidth = 10.dp,
                        radiusOuter = 120.dp,
                        middleText =
                        listOf(
                            TextPiece(
                                text =
                                buildAnnotatedString {
                                    withStyle(
                                        style =
                                        SpanStyle(
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                    ) {
                                        append("Total")
                                    }
                                }
                            ),
                            TextPiece(
                                text =
                                buildAnnotatedString {
                                    withStyle(
                                        style =
                                        SpanStyle(
                                            fontWeight = FontWeight.Normal,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                    ) {
                                        append(
                                            accountsTotalBalance.first.formatAmount(
                                                accountsTotalBalance.second
                                            )
                                        )
                                    }
                                }
                            )
                        ),
                        itemToWeight = {
                            if (it.balance >
                                0
                            ) {
                                it.balance * (1 / it.currency.value)
                            } else {
                                0f
                            }
                        },
                        itemDetails = {
                            Row(
                                modifier =
                                Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 8.dp)
                                    .clickable {
                                        onAccountClick(it.account)
                                    },
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(
                                    modifier =
                                    Modifier
                                        .weight(1f)
                                        .padding(start = 8.dp)
                                ) {
                                    Text(
                                        modifier = Modifier.padding(start = 15.dp),
                                        text = it.account.name,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                        fontSize = MaterialTheme.typography.bodyLarge.fontSize,
                                        fontWeight = MaterialTheme.typography.bodyLarge.fontWeight
                                    )
                                    Text(
                                        modifier = Modifier.padding(start = 15.dp),
                                        text =
                                        baseCurrency.formatAmount(
                                            if (it.balance >
                                                0
                                            ) {
                                                it.balance * (1 / it.currency.value)
                                            } else {
                                                0f
                                            }
                                        ),
                                        fontWeight = MaterialTheme.typography.bodySmall.fontWeight,
                                        fontSize = MaterialTheme.typography.bodySmall.fontSize,
                                        color = Color.Gray
                                    )
                                }
                                Column(
                                    modifier =
                                    Modifier
                                        .padding(vertical = 8.dp)
                                        .align(Alignment.CenterVertically)
                                ) {
                                    Text(
                                        text = it.currency.formatAmount(it.balance),
                                        fontWeight = MaterialTheme.typography.bodyMedium.fontWeight,
                                        fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        },
                        itemToColor = {
                            convertLongToColor(it.account.color)
                        }
                    )
                }
            }

            FloatingActionButton(
                onClick = onAccountTransfer,
                modifier =
                Modifier
                    .align(Alignment.BottomStart)
                    .padding(32.dp) // This positions the FAB to the left (start) and a bit up from the bottom, with some padding
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.switch_icon),
                    contentDescription = "Transfer",
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

@Suppress("ktlint:standard:function-naming")
@Composable
private fun AccountRow(
    accountWithTransactions: FullAccount,
    color: Color,
    onAccountClick: (account: Account) -> Unit
) {
    // TODO: move navigation code to appropriate place
    BaseRow(
        color = color,
        title = accountWithTransactions.account.name,
        subtitle = "",
        amount = accountWithTransactions.balance,
        currency = accountWithTransactions.account.currency,
        negative = false,
        holdedItem = accountWithTransactions,
        onItemSelected = {
            onAccountClick(it.account)
        }
    )
}

@Suppress("ktlint:standard:function-naming")
@Composable
fun EmptyAccountScreen() {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "No accounts yet. Create one by clicking the + button",
            modifier = Modifier.padding(16.dp)
        )
    }
}
