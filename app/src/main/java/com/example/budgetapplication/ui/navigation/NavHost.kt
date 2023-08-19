package com.example.budgetapplication.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController

import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.budgetapplication.ui.currencies.CurrenciesSummary
import com.example.budgetapplication.ui.theme.InitialScreen


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BudgetNavHost(
    navController : NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Overview.route,
        modifier = modifier
    ){
        composable(route = Overview.route){
            Overview.screen(navController)
        }
        composable(route = Currencies.route){
            Currencies.screen(navController)
        }
        composable(route = Accounts.route){
            Accounts.screen(navController)
        }
        composable(route=Categories.route){
            Categories.screen(navController)
        }

        composable(route=AccountEntry.route){
            AccountEntry.screen(navController)
        }
    }
}