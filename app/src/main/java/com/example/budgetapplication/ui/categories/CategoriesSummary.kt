package com.example.budgetapplication.ui.categories

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.Card
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.budgetapplication.R
import com.example.budgetapplication.data.categories.CategoryType
import com.example.budgetapplication.data.categories.CategoryWithTransactions
import com.example.budgetapplication.ui.AppViewModelProvider
import com.example.budgetapplication.ui.components.ListDivider
import com.example.budgetapplication.ui.components.VerticalBar
import com.example.budgetapplication.ui.navigation.Categories
import com.example.budgetapplication.ui.navigation.CategoryDetails
import com.example.budgetapplication.ui.navigation.CategoryEntry
import com.example.budgetapplication.ui.theme.InitialScreen
import kotlinx.coroutines.launch


data class TabItem(
    val title: String,
    val icon: @Composable () -> Unit,
    val screen: @Composable () -> Unit
)


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CategoriesSummary(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    viewModel: CategoriesSummaryViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val categoriesState by viewModel.categoriesUiState.collectAsState()
    val pagerState = rememberPagerState(
        initialPage = 0,
        initialPageOffsetFraction = 0f,
        pageCount = { 2 }
    )
    val coroutineScope = rememberCoroutineScope()

    val tabs = listOf(
        TabItem(
            title = "Expense",
            icon = {
                Icon(
                    painter = painterResource(id = R.drawable.payments_24dp_fill0_wght400_grad0_opsz24),
                    contentDescription = "Expenses",
                    tint = Color(0xFFE57373)
                )
            },
            screen = {
                CategoriesSummaryBody(
                    categories = categoriesState.categoriesList.filter {
                        it.category.defaultType == CategoryType.Expense
                    },
                    navController = navController
                )
            }
        ),
        TabItem(
            title = "Income",
            icon = {
                Icon(
                    painter = painterResource(id = R.drawable.payments_24dp_fill0_wght400_grad0_opsz24),
                    contentDescription = "Incomes",
                    tint = Color(0xFF4CAF50)
                )
            },
            screen = {
                CategoriesSummaryBody(
                    categories = categoriesState.categoriesList.filter {
                        it.category.defaultType == CategoryType.Income
                    }, navController = navController
                )
            }
        ),
    )
    InitialScreen(
        navController = navController,
        destination = Categories,
        screenBody = {
            if (categoriesState.categoriesList.isEmpty()) {
                EmptyCategoryScreen()
            } else {
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    TabRow(
                        selectedTabIndex = pagerState.currentPage,
                    ) {
                        tabs.forEachIndexed { index, item ->
                            Tab(
                                selected = index == pagerState.currentPage,
                                text = { Text(text = item.title) },
                                icon = item.icon,
                                onClick = {
                                    coroutineScope.launch {
                                        pagerState.animateScrollToPage(
                                            index
                                        )
                                    }
                                },
                            )
                        }
                    }
                    HorizontalPager(
                        modifier = Modifier,
                        state = pagerState,
                        pageSpacing = 0.dp,
                        pageContent = {
                            tabs[it].screen()
                        }
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
    Column(
        modifier = Modifier
            .verticalScroll(rememberScrollState())
            .fillMaxSize(),
    ) {
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
            Text(
                text = category.category.defaultType.name,
                style = MaterialTheme.typography.titleMedium
            )
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