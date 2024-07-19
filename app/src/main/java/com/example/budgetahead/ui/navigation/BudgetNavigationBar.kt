package com.example.budgetahead.ui.navigation

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController

@Composable
fun BudgetNavigationBar(
    allScreens: List<BudgetDestination>,
    onTabSelected: (BudgetDestination) -> Unit,
    currentScreen: BudgetDestination
) {
    BottomAppBar(
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        Row(
            modifier =
            Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            allScreens.forEach { screen ->
                val isSelected = screen == currentScreen
                IconButton(
                    onClick = { if (!isSelected) onTabSelected(screen) },
                    // Set the icon tint color based on the selection state
                    colors =
                    IconButtonDefaults.iconButtonColors(
                        contentColor = if (isSelected) MaterialTheme.colorScheme.onSecondaryContainer else MaterialTheme.colorScheme.onSurface
                    ),
                    modifier =
                    Modifier
                        .then(
                            if (isSelected) {
                                Modifier
                                    .background(
                                        MaterialTheme.colorScheme.secondaryContainer,
                                        CircleShape
                                    )
                                    .padding(4.dp)
                                    .clip(CircleShape)
                            } else {
                                Modifier
                            }
                        )
                ) {
                    screen.icon(isSelected)
                }
            }
        }
    }
}

@Composable
fun BudgetTopBar(currentScreen: BudgetDestination, navHostController: NavHostController) {
    currentScreen.topBar?.invoke(navHostController) ?: DefaultTopBar(currentScreen = currentScreen)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DefaultTopBar(currentScreen: BudgetDestination, actions: @Composable (() -> Unit)? = null) {
    TopAppBar(
        title = {
            Text(
                text = currentScreen.route.lowercase().replaceFirstChar { it.titlecase() },
                style = MaterialTheme.typography.headlineSmall
            )
        },
        /*navigationIcon = {
            IconButton(onClick = { }) {
                Icon(
                    imageVector = Icons.Filled.Menu,
                    contentDescription = "Menu",
                )
            }
        }*/
        actions = {
            actions?.invoke()
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SecondaryScreenTopBar(
    navigateBack: () -> Unit,
    @StringRes titleResId: Int,
    actions: @Composable (() -> Unit)? = null
) {
    TopAppBar(
        title = {
            Text(
                text = stringResource(id = titleResId),
                color = MaterialTheme.colorScheme.onPrimary
            )
        },
        navigationIcon = {
            IconButton(onClick = { navigateBack() }) {
                Icon(
                    imageVector = Icons.Filled.ArrowBack,
                    contentDescription = "Go back",
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
        },
        actions = {
            actions?.invoke() // Render actions only if non-null
        },
        modifier = Modifier.fillMaxWidth(),
        colors =
        TopAppBarDefaults.smallTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary
        )
    )
}

@Composable
@Preview(showBackground = true, name = "Budget Navigation Bar Preview")
fun PreviewBudgetNavigationBar() {
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

@Preview(showBackground = true)
@Composable
fun DefaultTopBarPreview() {
    DefaultTopBar(currentScreen = Overview)
}
