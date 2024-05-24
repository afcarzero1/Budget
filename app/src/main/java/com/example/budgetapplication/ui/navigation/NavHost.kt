package com.example.budgetapplication.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType

import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.budgetapplication.ui.AppViewModelProvider
import com.example.budgetapplication.ui.currencies.CurrenciesScreen
import com.example.budgetapplication.ui.currencies.CurrenciesSummary
import com.example.budgetapplication.ui.currencies.CurrenciesViewModel
import com.example.budgetapplication.ui.currencies.CurrencySettingsScreen


@Composable
fun BudgetNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    val currenciesViewModel: CurrenciesViewModel = viewModel(factory = AppViewModelProvider.Factory)


    NavHost(
        navController = navController,
        startDestination = Overview.route,
        modifier = modifier
    ) {
        composable(route = Overview.route) {
            Overview.screen(navController)
        }
        composable(route = Currencies.route) {
            CurrenciesScreen(navHostController = navController, viewModel = currenciesViewModel)
        }
        composable(route = CurrenciesSettings.route) {
            CurrencySettingsScreen(navController = navController, viewModel = currenciesViewModel)
        }
        composable(route = Accounts.route) {
            Accounts.screen(navController)
        }
        composable(route = AccountEntry.route) {
            AccountEntry.screen(navController)
        }
        composable(route = AccountTransferEntry.route) {
            AccountTransferEntry.screen(navController)
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

        composable(
            route = CategoryDetails.routeWithArgs,
            arguments = listOf(
                navArgument(CategoryDetails.categoryIdArg) {
                    type = NavType.IntType
                }
            )
        ) {
            CategoryDetails.screen(navController)
        }

        composable(route = Transactions.route) {
            Transactions.screen(navController)
        }

        composable(route = TransactionEntry.route) {
            TransactionEntry.screen(navController)
        }

        composable(
            route = TransactionDetails.routeWithArgs,
            arguments = listOf(
                navArgument(TransactionDetails.transactionIdArg) {
                    type = NavType.IntType
                }
            )
        ) {
            TransactionDetails.screen(navController)
        }

        composable(
            route = FutureTransactionEntry.route
        ) {
            FutureTransactionEntry.screen(navController)
        }

        composable(
            route = FutureTransactionDetails.routeWithArgs,
            arguments = listOf(
                navArgument(FutureTransactionDetails.futureTransactionIdArg) {
                    type = NavType.IntType
                }
            )
        ) {
            FutureTransactionDetails.screen(navController)
        }
    }
}