package com.example.budgetapplication.ui.navigation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun BudgetNavigationBar(
    allScreens: List<BudgetDestination>,
    onTabSelected: (BudgetDestination) -> Unit,
    currentScreen: BudgetDestination
) {
    BottomAppBar(
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            allScreens.forEach { screen ->
                val isSelected = screen == currentScreen
                IconButton(
                    onClick = {
                        if (!isSelected) {
                            onTabSelected(screen)
                        }
                    }
                ) {
                    screen.icon(isSelected)
                }
            }
        }
    }
}


@Composable
@Preview(showBackground = true, name = "Budget Navigation Bar Preview")
fun PreviewBudgetNavigationBar() {
    // Sample data for previewing
    val sampleScreens = tabDestinations
    val currentScreen = sampleScreens.first()

    MaterialTheme {
        BudgetNavigationBar(
            allScreens = sampleScreens,
            onTabSelected = {}, // No-op for preview purposes
            currentScreen = currentScreen
        )
    }
}