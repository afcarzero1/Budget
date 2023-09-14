package com.example.budgetapplication.ui.categories

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.Card
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.budgetapplication.ui.AppViewModelProvider
import com.example.budgetapplication.ui.navigation.Categories
import com.example.budgetapplication.ui.theme.InitialScreen
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.budgetapplication.data.categories.CategoryWithTransactions
import com.example.budgetapplication.ui.components.ListDivider
import com.example.budgetapplication.ui.components.VerticalBar
import com.example.budgetapplication.ui.navigation.AccountDetails
import com.example.budgetapplication.ui.navigation.CategoryDetails
import com.example.budgetapplication.ui.navigation.CategoryEntry

@Composable
fun CategoriesSummary(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    viewModel: CategoriesSummaryViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {

    InitialScreen(navController = navController, destination = Categories, screenBody = {
        val categoriesState by viewModel.categoriesUiState.collectAsState()

        if (categoriesState.categoriesList.isEmpty()) {
            EmptyCategoryScreen()
        } else {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                CategoriesSummaryBody(
                    categories = categoriesState.categoriesList, navController = navController
                )
            }
        }

    }, floatingButton = {
        FloatingActionButton(
            onClick = {
                navController.navigate(CategoryEntry.route)
            }, modifier = Modifier.padding(16.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.Add, contentDescription = "Add Category"
            )
        }
    })
}


@Composable
fun CategoriesSummaryBody(
    categories: List<CategoryWithTransactions>, navController: NavHostController
) {
    Column(modifier = Modifier.verticalScroll(rememberScrollState())) {

        Card {
            Column(modifier = Modifier.padding(16.dp)) {
                categories.forEach { category ->
                    CategoryRow(category = category, onItemSelected = {
                        navController.navigate("${CategoryDetails.route}/${it.category.id}")
                    })
                }
            }
        }
    }
}

@Composable
fun CategoryRow(
    category: CategoryWithTransactions,
    onItemSelected: (CategoryWithTransactions) -> Unit = {},
) {
    Row(
        modifier = Modifier
            .height(68.dp)
            .padding(horizontal = 16.dp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {

        VerticalBar(
            color = Color(0), modifier = Modifier.width(2.dp)
        )

        Spacer(modifier = Modifier.width(12.dp))

        // Title and type of category
        Column(Modifier) {
            Text(text = category.category.name, style = MaterialTheme.typography.headlineSmall)
            Text(text = category.category.defaultType, style = MaterialTheme.typography.titleMedium)
        }

        Spacer(modifier = Modifier.width(18.dp))

        // Parent Category
        Column(Modifier) {
            //TODO: Update here when parent category is ready
            Text(text = "Depends on", style = MaterialTheme.typography.headlineSmall)
            Text(text = "None", style = MaterialTheme.typography.titleMedium)
        }

        Spacer(modifier = Modifier.width(18.dp))

        IconButton(onClick = { onItemSelected(category) }) {
            Icon(
                imageVector = Icons.Default.KeyboardArrowRight,
                contentDescription = null,
            )
        }
    }
    ListDivider()
}


@Composable
fun EmptyCategoryScreen() {
    Column(
        modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "No categories yet. Create one by clicking the + button",
            modifier = Modifier.padding(16.dp)
        )
    }
}