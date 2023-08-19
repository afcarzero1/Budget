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
            Scaffold(
                topBar = {
                    BudgetNavigationBar(
                        allScreens = tabDestinations,
                        onTabSelected = { screen -> navController.navigate(screen.route)},
                        currentScreen = Overview
                    )
                }
            ) { innerPadding ->
                Box(modifier = Modifier.padding(innerPadding)){
                    Overview.screen()
                }
            }
        }
        composable(route = Currencies.route){
            Scaffold(
                topBar = {
                    BudgetNavigationBar(
                        allScreens = tabDestinations,
                        onTabSelected = { screen -> navController.navigate(screen.route)},
                        currentScreen = Currencies
                    )
                }
            ) { innerPadding ->
                Box(modifier = Modifier.padding(innerPadding)){
                    Currencies.screen()
                }
            }
        }
        composable(route = Accounts.route){
            Scaffold(
                topBar = {
                    BudgetNavigationBar(
                        allScreens = tabDestinations,
                        onTabSelected = { screen -> navController.navigate(screen.route)},
                        currentScreen = Accounts
                    )
                }
            ) { innerPadding ->
                Box(modifier = Modifier.padding(innerPadding)){
                    Currencies.screen()
                }
            }
        }
        composable(route=Categories.route){

        }
    }
}