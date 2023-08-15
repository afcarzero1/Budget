package com.example.budgetapplication.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.budgetapplication.R
import com.example.budgetapplication.ui.accounts.AccountsSummary
import com.example.budgetapplication.ui.currencies.CurrenciesSummary

interface BudgetDestination {
    val icon: @Composable (tint: Color) -> Unit
    val route: String
    val screen: @Composable () -> Unit
}

object Overview : BudgetDestination {
    override val icon = @Composable { tint: Color ->
        Icon(Icons.Filled.Home, contentDescription = null, tint= tint)
    }
    override val route = "overview"
    override val screen: @Composable () -> Unit = { Text("Under development") }
}

object Accounts : BudgetDestination {
    override val icon = @Composable { tint: Color ->
        Icon(painter = painterResource(id = R.drawable.bank), contentDescription = null, tint= tint)
    }
    override val route = "accounts"
    override val screen: @Composable () -> Unit = { AccountsSummary() }
}

object Currencies: BudgetDestination {
    override val icon = @Composable { tint: Color ->
        Icon(Icons.Filled.Home, contentDescription = null, tint= tint)
    }
    override val route = "currencies"
    override val screen: @Composable () -> Unit = { CurrenciesSummary() }
}

object Categories: BudgetDestination {
    override val icon = @Composable { tint: Color ->
        Icon(Icons.Filled.Home, contentDescription = null, tint= tint)
    }
    override val route = "categories"
    override val screen: @Composable () -> Unit = { Text(text = "Under development") }
}

val tabDestinations = listOf(Overview, Accounts, Currencies, Categories)
