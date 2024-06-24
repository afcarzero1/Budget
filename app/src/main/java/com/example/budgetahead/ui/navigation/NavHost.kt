package com.example.budgetahead.ui.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.budgetahead.ui.AppViewModelProvider
import com.example.budgetahead.ui.currencies.CurrenciesScreen
import com.example.budgetahead.ui.currencies.CurrencySettingsScreen
import com.example.budgetahead.ui.onboarding.OnBoardingScreen
import com.example.budgetahead.ui.onboarding.OnBoardingViewModel


@Composable
fun BudgetNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    startDestination: BudgetDestination? = null
) {
    NavHost(
        navController = navController,
        startDestination = startDestination?.route ?: Overview.route,
        modifier = modifier
    ) {

        composable(route = OnBoarding.route) {
            val viewModel: OnBoardingViewModel = viewModel(factory = AppViewModelProvider.Factory)
            OnBoardingScreen(
                onEvent = viewModel::onEvent
            )
        }

        composable(route = Overview.route) {
            Overview.screen(navController)
        }
        composable(route = Currencies.route) {
            CurrenciesScreen(navHostController = navController)
        }
        composable(route = CurrenciesSettings.route) {
            CurrencySettingsScreen(navController = navController)
        }
        composable(
            route = Accounts.route,
        ) {
            Accounts.screen(navController)
        }
        composable(
            route = AccountEntry.route,
        ) {
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
            route = TransferDetails.routeWithArgs,
            arguments = listOf(
                navArgument(TransferDetails.transferIdArg) {
                    type = NavType.IntType
                }
            )
        ) {
            TransferDetails.screen(navController)
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
            ),
            enterTransition = {
                fadeIn(
                    animationSpec = tween(
                        1000, easing = LinearEasing
                    )
                ) + slideIntoContainer(
                    animationSpec = tween(1000, easing = LinearOutSlowInEasing),
                    towards = AnimatedContentTransitionScope.SlideDirection.Start
                )
            }
        ) {
            FutureTransactionDetails.screen(navController)
        }
    }
}