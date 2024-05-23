package com.example.budgetapplication.ui.navigation

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.budgetapplication.R
import com.example.budgetapplication.ui.accounts.AccountDetailsScreen
import com.example.budgetapplication.ui.accounts.AccountEntryScreen
import com.example.budgetapplication.ui.accounts.AccountsSummary
import com.example.budgetapplication.ui.accounts.TransferEntryScreen
import com.example.budgetapplication.ui.categories.CategoriesSummary
import com.example.budgetapplication.ui.categories.CategoryDetailsScreen
import com.example.budgetapplication.ui.categories.CategoryEntryScreen
import com.example.budgetapplication.ui.currencies.CurrenciesScreen
import com.example.budgetapplication.ui.overall.OverallScreen
import com.example.budgetapplication.ui.theme.InitialScreen
import com.example.budgetapplication.ui.transactions.FutureTransactionDetailsScreen
import com.example.budgetapplication.ui.transactions.FutureTransactionEntryScreen
import com.example.budgetapplication.ui.transactions.TransactionDetailsScreen
import com.example.budgetapplication.ui.transactions.TransactionEntryScreen
import com.example.budgetapplication.ui.transactions.TransactionsSummary

interface BudgetDestination {
    val icon: @Composable (selected: Boolean) -> Unit
    val route: String
    val screen: @Composable (navController: NavHostController) -> Unit
}

object Overview : BudgetDestination {
    override val icon = @Composable { selected: Boolean ->
        Icon(
            painter = painterResource(
                id = if (selected) R.drawable.home_24dp_fill1_wght300_grad0_opsz24 else
                    R.drawable.home_24dp_fill0_wght300_grad0_opsz24
            ),
            contentDescription = null
        )
    }
    override val route = "overview"
    override val screen: @Composable (navController: NavHostController) -> Unit = {
        OverallScreen(navController = it)
    }
}

object Accounts : BudgetDestination {
    override val icon = @Composable { selected: Boolean ->
        Icon(
            painter = painterResource(
                id = if (selected) R.drawable.account_balance_wallet_24dp_fill0_wght300_grad200_opsz24 else
                    R.drawable.account_balance_wallet_24dp_fill0_wght300_grad0_opsz24
            ),
            contentDescription = null,
        )
    }
    override val route = "accounts"
    override val screen: @Composable (navController: NavHostController) -> Unit = {
        AccountsSummary(navController = it)
    }
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
            navigateBack = { it.popBackStack() }
        )
    }
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
            navigateBack = { it.popBackStack() }
        )
    }
}


object AccountDetails : BudgetDestination {
    override val icon = @Composable { selected: Boolean ->
        Icon(
            painter = painterResource(id = R.drawable.bank),
            contentDescription = null
        )
    }
    override val route: String = "accountDetails"

    const val accountIdArg = "accountId"

    val routeWithArgs = "$route/{$accountIdArg}"

    override val screen: @Composable (navController: NavHostController) -> Unit =
        {
            AccountDetailsScreen(navigateBack = { it.popBackStack() })
        }

}

object Currencies : BudgetDestination {
    override val icon = @Composable { selected: Boolean ->
        Icon(
            painter = painterResource(
                id = if (selected) R.drawable.currency_exchange_24dp_fill0_wght300_grad200_opsz24 else
                    R.drawable.currency_exchange_24dp_fill0_wght300_grad0_opsz24
            ),
            contentDescription = null,
        )
    }
    override val route = "currencies"
    override val screen: @Composable (navController: NavHostController) -> Unit = {
        CurrenciesScreen(navHostController = it)
    }
}

object Categories : BudgetDestination {
    override val icon = @Composable { selected: Boolean ->
        Icon(
            painter = painterResource(
                id = if (selected) R.drawable.category_24dp_fill0_wght300_grad200_opsz24 else
                    R.drawable.category_24dp_fill0_wght300_grad0_opsz24
            ),
            contentDescription = null,
        )
    }
    override val route = "categories"
    override val screen: @Composable (navController: NavHostController) -> Unit = {
        CategoriesSummary(navController = it)
    }
}

object CategoryEntry : BudgetDestination {
    override val icon = @Composable { selected: Boolean ->
        Icon(Icons.Filled.Home, contentDescription = null)
    }
    override val route: String = "categoryEntry"
    override val screen: @Composable (navController: NavHostController) -> Unit = {
        CategoryEntryScreen(navigateBack = { it.popBackStack() })
    }
}

object CategoryDetails : BudgetDestination {
    override val icon = @Composable { selected: Boolean ->
        Icon(
            painter = painterResource(id = R.drawable.bank),
            contentDescription = null
        )
    }
    override val route: String = "categoryDetails"

    const val categoryIdArg = "categoryId"

    val routeWithArgs = "$route/{$categoryIdArg}"

    override val screen: @Composable (navController: NavHostController) -> Unit =
        {
            CategoryDetailsScreen(navigateBack = { it.popBackStack() })
        }
}


object Transactions : BudgetDestination {
    override val icon = @Composable { selected: Boolean ->
        Icon(
            painter = painterResource(id = R.drawable.switch_icon),
            contentDescription = null,
            modifier = Modifier.size(24.dp)
        )
    }
    override val route: String = "transactions"
    override val screen: @Composable (navController: NavHostController) -> Unit = {
        TransactionsSummary(navController = it)
    }
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
}


val tabDestinations = listOf(Overview, Accounts, Transactions, Currencies, Categories)
