package com.example.budgetapplication.ui.theme

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.budgetapplication.ui.navigation.BudgetDestination
import com.example.budgetapplication.ui.navigation.BudgetNavHost
import com.example.budgetapplication.ui.navigation.BudgetNavigationBar
import com.example.budgetapplication.ui.navigation.BudgetTopBar
import com.example.budgetapplication.ui.navigation.tabDestinations


@Composable
fun BudgetApplicationApp(navController: NavHostController = rememberNavController()) {
    BudgetNavHost(navController = navController)
}

@Composable
fun InitialScreen(
    navController: NavHostController,
    destination: BudgetDestination,
    screenBody: @Composable () -> Unit,
    floatingButton : @Composable () -> Unit = {},
    topBar: (@Composable (BudgetDestination, NavHostController) -> Unit)? = null
) {
    Scaffold(
        bottomBar = {
            BudgetNavigationBar(
                allScreens = tabDestinations,
                onTabSelected = { screen -> navController.navigate(screen.route)},
                currentScreen = destination
            )
        },
        topBar = {
            topBar?.invoke(destination, navController) ?: BudgetTopBar(currentScreen = destination, navHostController = navController)
        },
        floatingActionButton = floatingButton
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)){
            screenBody()
        }
    }
}