package com.example.budgetapplication.ui

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.example.budgetapplication.ui.navigation.BudgetDestination
import com.example.budgetapplication.ui.navigation.Overview
import com.example.budgetapplication.ui.theme.BudgetApplicationTheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.budgetapplication.ui.navigation.BudgetNavigationBar
import com.example.budgetapplication.ui.navigation.tabDestinations


class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BudgetApplicationTheme {
                var currentScreen: BudgetDestination by remember { mutableStateOf(Overview) }
                Log.d("MainActivity", "Current screen: ${currentScreen.route}")
                Scaffold(
                    topBar = {
                        BudgetNavigationBar(
                            allScreens = tabDestinations,
                            onTabSelected = { screen -> currentScreen = screen },
                            currentScreen = currentScreen
                        )
                    }
                ) { innerPadding ->
                    Box(modifier = Modifier.padding(innerPadding)){
                        currentScreen.screen()
                    }
                }
            }
        }
    }
}

