package com.example.budgetapplication.ui.navigation

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType

import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BudgetNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Overview.route,
        modifier = modifier
    ) {
        composable(route = Overview.route) {
            Overview.screen(navController)
        }
        composable(route = Currencies.route) {
            Currencies.screen(navController)
        }
        composable(route = Accounts.route) {
            Accounts.screen(navController)
        }
        composable(route = AccountEntry.route) {
            AccountEntry.screen(navController)
        }
        composable(
            route = AccountDetails.routeWithArgs,
            arguments = listOf(
                navArgument(AccountDetails.accountIdArg) {
                    type = NavType.IntType
                }
            )
        ) {
            AccountDetails.screen(navController)
        }
        composable(route = Categories.route) {
            Categories.screen(navController)
        }
        composable(route = CategoryEntry.route) {
            CategoryEntry.screen(navController)
        }

    }
}