package com.example.budgetahead.ui.navigation

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.budgetahead.R
import com.example.budgetahead.data.transfers.TransferDetailsScreen
import com.example.budgetahead.ui.AppViewModelProvider
import com.example.budgetahead.ui.accounts.AccountDetailsScreen
import com.example.budgetahead.ui.accounts.AccountEntryScreen
import com.example.budgetahead.ui.accounts.AccountSummaryScreen
import com.example.budgetahead.ui.accounts.AccountsSummary
import com.example.budgetahead.ui.accounts.TransferEntryScreen
import com.example.budgetahead.ui.categories.CategoriesSummary
import com.example.budgetahead.ui.categories.CategoryDetailsScreen
import com.example.budgetahead.ui.categories.CategoryEntryScreen
import com.example.budgetahead.ui.categories.CategoryOverviewScreen
import com.example.budgetahead.ui.currencies.CurrenciesScreen
import com.example.budgetahead.ui.currencies.CurrencySettingsScreen
import com.example.budgetahead.ui.overall.OverallScreen
import com.example.budgetahead.ui.transactions.FutureTransactionDetailsScreen
import com.example.budgetahead.ui.transactions.FutureTransactionEntryScreen
import com.example.budgetahead.ui.transactions.TransactionDetailsScreen
import com.example.budgetahead.ui.transactions.TransactionEntryScreen
import com.example.budgetahead.ui.transactions.TransactionsSummary

interface BudgetDestination {
    val icon: @Composable (selected: Boolean) -> Unit
    val route: String
    val screen: @Composable (navController: NavHostController) -> Unit
    val topBar: (@Composable (NavHostController) -> Unit)?
}

object OnBoarding : BudgetDestination {
    override val icon = @Composable { selected: Boolean ->
        Icon(
            painter =
                painterResource(
                    id =
                        if (selected) {
                            R.drawable.home_24dp_fill1_wght300_grad0_opsz24
                        } else {
                            R.drawable.home_24dp_fill0_wght300_grad0_opsz24
                        },
                ),
            contentDescription = null,
        )
    }
    override val route: String = "onboarding"
    override val screen: (navController: NavHostController) -> Unit
        get() = TODO("Not yet implemented")
    override val topBar: ((NavHostController) -> Unit)? = null
}

object Overview : BudgetDestination {
    override val icon = @Composable { selected: Boolean ->
        Icon(
            painter =
                painterResource(
                    id =
                        if (selected) {
                            R.drawable.home_24dp_fill1_wght300_grad0_opsz24
                        } else {
                            R.drawable.home_24dp_fill0_wght300_grad0_opsz24
                        },
                ),
            contentDescription = null,
        )
    }
    override val route = "overview"
    override val screen: @Composable (navController: NavHostController) -> Unit = {
        OverallScreen(navController = it)
    }
    override val topBar: (@Composable (NavHostController) -> Unit)? = null
}

object CashFlowOverview : BudgetDestination {
    override val icon = @Composable { selected: Boolean ->
        Icon(
            painter =
                painterResource(
                    id =
                        if (selected) {
                            R.drawable.home_24dp_fill1_wght300_grad0_opsz24
                        } else {
                            R.drawable.home_24dp_fill0_wght300_grad0_opsz24
                        },
                ),
            contentDescription = null,
        )
    }
    override val route = "cashflow_overview"
    override val screen: @Composable (navController: NavHostController) -> Unit = {
        TODO("Add here screen")
    }
    override val topBar: (@Composable (NavHostController) -> Unit)? = null

    const val dateArg = "dateArgs"

    val routeWithArgs = "$route/{$dateArg}"
}

object Accounts : BudgetDestination {
    override val icon = @Composable { selected: Boolean ->
        Icon(
            painter =
                painterResource(
                    id =
                        if (selected) {
                            R.drawable.account_balance_wallet_24dp_fill0_wght300_grad200_opsz24
                        } else {
                            R.drawable.account_balance_wallet_24dp_fill0_wght300_grad0_opsz24
                        },
                ),
            contentDescription = null,
        )
    }
    override val route = "accounts"
    override val screen: @Composable (navController: NavHostController) -> Unit = {
        AccountsSummary(navController = it)
    }
    override val topBar: (@Composable (NavHostController) -> Unit)? = null
}

object AccountEntry : BudgetDestination {
    override val icon = @Composable { selected: Boolean ->
        Icon(
            painter = painterResource(id = R.drawable.bank),
            contentDescription = null,
        )
    }
    override val route = "accountEntry"
    override val screen: @Composable (navController: NavHostController) -> Unit = {
        AccountEntryScreen(
            navigateBack = {
                it.popBackStack()
            },
        )
    }
    override val topBar: (@Composable (NavHostController) -> Unit)? = null
}

object AccountTransferEntry : BudgetDestination {
    override val icon = @Composable { selected: Boolean ->
        Icon(
            painter = painterResource(id = R.drawable.bank),
            contentDescription = null,
        )
    }
    override val route = "transferEntry"
    override val screen: @Composable (navController: NavHostController) -> Unit = {
        TransferEntryScreen(
            navigateBack = { it.popBackStack() },
        )
    }
    override val topBar: (@Composable (NavHostController) -> Unit)? = null
}

object AccountSummary : BudgetDestination {
    override val icon = @Composable { selected: Boolean ->
        Icon(
            painter = painterResource(id = R.drawable.bank),
            contentDescription = null,
        )
    }
    override val route: String = "accountSummary"

    const val accountIdArg = "accountId"

    val routeWithArgs = "$route/{$accountIdArg}"

    override val screen: @Composable (navController: NavHostController) -> Unit =
        {
            AccountSummaryScreen(navController = it)
        }
    override val topBar: (@Composable (NavHostController) -> Unit)? = null
}

object AccountDetails : BudgetDestination {
    override val icon = @Composable { selected: Boolean ->
        Icon(
            painter = painterResource(id = R.drawable.bank),
            contentDescription = null,
        )
    }
    override val route: String = "accountDetails"

    const val accountIdArg = "accountId"

    val routeWithArgs = "$route/{$accountIdArg}"

    override val screen: @Composable (navController: NavHostController) -> Unit =
        {
            AccountDetailsScreen(navigateBack = { it.popBackStack() })
        }
    override val topBar: (@Composable (NavHostController) -> Unit)? = null
}

object Currencies : BudgetDestination {
    override val icon = @Composable { selected: Boolean ->
        Icon(
            painter =
                painterResource(
                    id =
                        if (selected) {
                            R.drawable.currency_exchange_24dp_fill0_wght300_grad200_opsz24
                        } else {
                            R.drawable.currency_exchange_24dp_fill0_wght300_grad0_opsz24
                        },
                ),
            contentDescription = null,
        )
    }
    override val route = "currencies"
    override val screen: @Composable (navController: NavHostController) -> Unit = {
        CurrenciesScreen(navHostController = it, viewModel(factory = AppViewModelProvider.Factory))
    }
    override val topBar: (@Composable (NavHostController) -> Unit) = @Composable {
    }
}

object CurrenciesSettings : BudgetDestination {
    override val icon = @Composable { selected: Boolean ->
        Icon(imageVector = Icons.Filled.Check, contentDescription = "yes")
    }
    override val route: String = "currencies settings"
    override val screen: @Composable (navController: NavHostController) -> Unit = {
        CurrencySettingsScreen(navController = it)
    }
    override val topBar: ((NavHostController) -> Unit)? = null
}

object Categories : BudgetDestination {
    override val icon = @Composable { selected: Boolean ->
        Icon(
            painter =
                painterResource(
                    id =
                        if (selected) {
                            R.drawable.category_24dp_fill0_wght300_grad200_opsz24
                        } else {
                            R.drawable.category_24dp_fill0_wght300_grad0_opsz24
                        },
                ),
            contentDescription = null,
        )
    }
    override val route = "categories"
    override val screen: @Composable (navController: NavHostController) -> Unit = {
        CategoriesSummary(navController = it)
    }
    override val topBar: (@Composable (NavHostController) -> Unit)? = null
}

object CategoryEntry : BudgetDestination {
    override val icon = @Composable { selected: Boolean ->
        Icon(Icons.Filled.Home, contentDescription = null)
    }
    override val route: String = "categoryEntry"
    override val screen: @Composable (navController: NavHostController) -> Unit = {
        CategoryEntryScreen(navigateBack = { it.popBackStack() })
    }
    override val topBar: (@Composable (NavHostController) -> Unit)? = null
}

object CategoryOverview : BudgetDestination {
    override val icon = @Composable { selected: Boolean ->
        Icon(
            painter = painterResource(id = R.drawable.bank),
            contentDescription = null,
        )
    }
    override val route: String = "categorySummary"

    const val categoryIdArg = "categoryId"

    val routeWithArgs = "$route/{$categoryIdArg}"

    override val screen: @Composable (navController: NavHostController) -> Unit =
        {
            CategoryOverviewScreen(navController = it)
        }
    override val topBar: (@Composable (NavHostController) -> Unit)? = null
}

object CategoryDetails : BudgetDestination {
    override val icon = @Composable { selected: Boolean ->
        Icon(
            painter = painterResource(id = R.drawable.bank),
            contentDescription = null,
        )
    }
    override val route: String = "categoryDetails"

    const val categoryIdArg = "categoryId"

    val routeWithArgs = "$route/{$categoryIdArg}"

    override val screen: @Composable (navController: NavHostController) -> Unit =
        {
            CategoryDetailsScreen(navigateBack = { it.popBackStack() })
        }
    override val topBar: (@Composable (NavHostController) -> Unit)? = null
}

object Transactions : BudgetDestination {
    override val icon = @Composable { selected: Boolean ->
        Icon(
            painter = painterResource(id = R.drawable.switch_icon),
            contentDescription = null,
            modifier = Modifier.size(24.dp),
        )
    }
    override val route: String = "transactions"
    override val screen: @Composable (navController: NavHostController) -> Unit = {
        TransactionsSummary(navController = it)
    }
    override val topBar: (@Composable (NavHostController) -> Unit)? = null
}

object TransactionEntry : BudgetDestination {
    override val icon = @Composable { selected: Boolean ->
        Icon(
            painter = painterResource(id = R.drawable.transactions),
            contentDescription = null,
        )
    }
    override val route: String = "transactionEntry"
    override val screen: @Composable (navController: NavHostController) -> Unit = {
        TransactionEntryScreen(navigateBack = { it.popBackStack() })
    }
    override val topBar: (@Composable (NavHostController) -> Unit)? = null
}

object TransactionDetails : BudgetDestination {
    override val icon = @Composable { selected: Boolean ->
        Icon(
            painter = painterResource(id = R.drawable.bank),
            contentDescription = null,
        )
    }
    override val route: String = "transactionDetails"

    const val transactionIdArg = "transactionId"

    val routeWithArgs = "$route/{$transactionIdArg}"

    override val screen: @Composable (navController: NavHostController) -> Unit =
        {
            TransactionDetailsScreen(navigateBack = { it.popBackStack() })
        }
    override val topBar: (@Composable (NavHostController) -> Unit)? = null
}

object TransferDetails : BudgetDestination {
    override val icon = @Composable { selected: Boolean ->
        Icon(
            painter = painterResource(id = R.drawable.bank),
            contentDescription = null,
        )
    }
    override val route: String = "transferDetails"

    const val transferIdArg = "transferId"

    val routeWithArgs = "$route/{$transferIdArg}"

    override val screen: @Composable (navController: NavHostController) -> Unit = {
        TransferDetailsScreen(navigateBack = { it.popBackStack() })
    }
    override val topBar: ((NavHostController) -> Unit)? = null
}

object FutureTransactionEntry : BudgetDestination {
    override val icon = @Composable { selected: Boolean ->
        Icon(
            painter = painterResource(id = R.drawable.transactions),
            contentDescription = null,
        )
    }
    override val route: String = "futureTransactionEntry"
    override val screen: @Composable (navController: NavHostController) -> Unit = {
        FutureTransactionEntryScreen(navigateBack = { it.popBackStack() })
    }
    override val topBar: (@Composable (NavHostController) -> Unit)? = null
}

object FutureTransactionDetails : BudgetDestination {
    override val icon = @Composable { selected: Boolean ->
        Icon(
            painter = painterResource(id = R.drawable.bank),
            contentDescription = null,
        )
    }
    override val route: String = "futureTransactionDetails"

    const val futureTransactionIdArg = "futureTransactionId"

    val routeWithArgs = "$route/{$futureTransactionIdArg}"

    override val screen: @Composable (navController: NavHostController) -> Unit =
        {
            FutureTransactionDetailsScreen(navigateBack = { it.popBackStack() })
        }
    override val topBar: (@Composable (NavHostController) -> Unit)? = null
}

val tabDestinations = listOf(Overview, Accounts, Transactions, Currencies, Categories)
