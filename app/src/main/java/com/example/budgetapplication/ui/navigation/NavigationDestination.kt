package com.example.budgetapplication.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.budgetapplication.ui.currencies.CurrenciesSummary

interface BudgetDestination {
    val icon: ImageVector
    val route: String
    val screen: @Composable () -> Unit
}


object Overview : BudgetDestination {
    override val icon = Icons.Filled.Home
    override val route = "overview"
    override val screen: @Composable () -> Unit = { Text("Under development") }
}

object Accounts : BudgetDestination {
    override val icon = Icons.Filled.Home
    override val route = "accounts"
    override val screen: @Composable () -> Unit = { Text(text = "Under development") }
}


object Currencies: BudgetDestination {
    override val icon = Icons.Filled.KeyboardArrowUp
    override val route = "currencies"
    override val screen: @Composable () -> Unit = { CurrenciesSummary() }
}

object Categories: BudgetDestination {
    override val icon = Icons.Filled.KeyboardArrowUp
    override val route = "categories"
    override val screen: @Composable () -> Unit = { Text(text = "Under development") }
}

val tabDestinations = listOf(Overview, Accounts, Currencies, Categories)