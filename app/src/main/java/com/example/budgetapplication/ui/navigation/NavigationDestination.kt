package com.example.budgetapplication.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavHostController
import com.example.budgetapplication.R
import com.example.budgetapplication.ui.accounts.AccountDetailsScreen
import com.example.budgetapplication.ui.accounts.AccountEntryScreen
import com.example.budgetapplication.ui.accounts.AccountsSummary
import com.example.budgetapplication.ui.currencies.CurrenciesScreen
import com.example.budgetapplication.ui.theme.InitialScreen

interface BudgetDestination {
    val icon: @Composable (tint: Color) -> Unit
    val route: String
    val screen: @Composable (navController: NavHostController) -> Unit
}

object Overview : BudgetDestination {
    override val icon = @Composable { tint: Color ->
        Icon(Icons.Filled.Home, contentDescription = null, tint = tint)
    }
    override val route = "overview"
    override val screen: @Composable (navController: NavHostController) -> Unit = {
        InitialScreen(
            navController = it,
            destination = Overview,
            screenBody = {
                Text(text = "Under development")
            }
        )
    }
}

object Accounts : BudgetDestination {
    override val icon = @Composable { tint: Color ->
        Icon(
            painter = painterResource(id = R.drawable.bank),
            contentDescription = null,
            tint = tint
        )
    }
    override val route = "accounts"
    override val screen: @Composable (navController: NavHostController) -> Unit = {
        AccountsSummary(navController = it)
    }
}

object AccountEntry: BudgetDestination{
    override val icon = @Composable { tint: Color ->
        Icon(
            painter = painterResource(id = R.drawable.bank),
            contentDescription = null,
            tint = tint
        )
    }
    override val route = "accountEntry"
    override val screen: @Composable (navController: NavHostController) -> Unit = {
        AccountEntryScreen(
            navigateBack = { it.popBackStack() }
        )
    }
}

object AccountDetails: BudgetDestination{
    override val icon = @Composable { tint: Color ->
        Icon(
            painter = painterResource(id = R.drawable.bank),
            contentDescription = null,
            tint = tint
        )
    }
    override val route: String = "accountDetails"

    const val accountIdArg = "accountId"

    val routeWithArgs = "$route/{$accountIdArg}"

    override val screen: @Composable (navController: NavHostController) -> Unit =
    {
        AccountDetailsScreen(navigateBack = { it.popBackStack()})
    }

}

object Currencies : BudgetDestination {
    override val icon = @Composable { tint: Color ->
        Icon(Icons.Filled.Home, contentDescription = null, tint = tint)
    }
    override val route = "currencies"
    override val screen: @Composable (navController: NavHostController) -> Unit = {
        CurrenciesScreen(navHostController = it)
    }
}

object Categories : BudgetDestination {
    override val icon = @Composable { tint: Color ->
        Icon(Icons.Filled.Home, contentDescription = null, tint = tint)
    }
    override val route = "categories"
    override val screen: @Composable (navController: NavHostController) -> Unit = {
        InitialScreen(
            navController = it,
            destination = Categories,
            screenBody = {
                Text(text = "Under development")
            }
        )
    }
}

val tabDestinations = listOf(Overview, Accounts, Currencies, Categories)
