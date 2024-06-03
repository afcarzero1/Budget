package com.example.budgetapplication.ui.categories

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardElevation
import androidx.compose.material3.Divider
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.budgetapplication.R
import com.example.budgetapplication.data.accounts.Account
import com.example.budgetapplication.data.accounts.AccountWithCurrency
import com.example.budgetapplication.data.categories.Category
import com.example.budgetapplication.data.categories.CategoryType
import com.example.budgetapplication.data.categories.CategoryWithTransactions
import com.example.budgetapplication.data.currencies.Currency
import com.example.budgetapplication.data.transactions.FullTransactionRecord
import com.example.budgetapplication.data.transactions.TransactionRecord
import com.example.budgetapplication.data.transactions.TransactionType
import com.example.budgetapplication.ui.AppViewModelProvider
import com.example.budgetapplication.ui.components.ColorAssigner
import com.example.budgetapplication.ui.components.ListDivider
import com.example.budgetapplication.ui.components.PieChart
import com.example.budgetapplication.ui.components.VerticalBar
import com.example.budgetapplication.ui.components.graphics.AvailableColors
import com.example.budgetapplication.ui.navigation.AccountDetails
import com.example.budgetapplication.ui.navigation.Categories
import com.example.budgetapplication.ui.navigation.CategoryDetails
import com.example.budgetapplication.ui.navigation.CategoryEntry
import com.example.budgetapplication.ui.navigation.TabItem
import com.example.budgetapplication.ui.navigation.TabbedPage
import com.example.budgetapplication.ui.theme.InitialScreen
import java.time.LocalDateTime
import java.time.YearMonth


@Composable
fun CategoriesSummary(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    viewModel: CategoriesSummaryViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val categoriesState by viewModel.categoriesUiState.collectAsState()
    val transactionsYearMonth by viewModel.currentMonthOfTransactions.collectAsState()

    InitialScreen(
        navController = navController,
        destination = Categories,
        screenBody = {
            if (categoriesState.categoriesList.isEmpty()) {
                EmptyCategoryScreen()
            } else {
                TabbedPage(
                    modifier = modifier.fillMaxSize(),
                    tabs = listOf(
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
                                    onCategoryClicked = {
                                        navController.navigate("${CategoryDetails.route}/${it.id}")
                                    },
                                    onNextMonth = {
                                        viewModel.setMonthOfTransactions(
                                            transactionsYearMonth.plusMonths(
                                                1
                                            )
                                        )
                                    },
                                    onPreviousMonth = {
                                        viewModel.setMonthOfTransactions(
                                            transactionsYearMonth.minusMonths(
                                                1
                                            )
                                        )
                                    },
                                    yearMonthOfTransactions = transactionsYearMonth,
                                    categoryToColor = {
                                        viewModel.colorAssigner.assignColor(it.name)
                                    },
                                    deltas = categoriesState.categoriesDelta
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
                                    },
                                    onCategoryClicked = {
                                        navController.navigate("${CategoryDetails.route}/${it.id}")
                                    },
                                    onNextMonth = {
                                        viewModel.setMonthOfTransactions(
                                            transactionsYearMonth.plusMonths(
                                                1
                                            )
                                        )
                                    },
                                    onPreviousMonth = {
                                        viewModel.setMonthOfTransactions(
                                            transactionsYearMonth.minusMonths(
                                                1
                                            )
                                        )
                                    },
                                    yearMonthOfTransactions = transactionsYearMonth,
                                    categoryToColor = {
                                        viewModel.colorAssigner.assignColor(it.name)
                                    },
                                    deltas = categoriesState.categoriesDelta
                                )
                            }
                        ),
                    ))
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
    categories: List<CategoryWithTransactions>,
    deltas: Map<Category, Float>,
    yearMonthOfTransactions: YearMonth,
    categoryToColor: (Category) -> Color,
    onNextMonth: () -> Unit,
    onPreviousMonth: () -> Unit,
    onCategoryClicked: (Category) -> Unit
) {

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            IconButton(
                onClick = onPreviousMonth,
                modifier = Modifier.padding(16.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.ArrowBack,
                    contentDescription = "Previous",
                    modifier = Modifier.size(32.dp)
                )
            }
            PieChart(
                data = categories,
                itemToWeight = { categoryWithTransactions ->
                    deltas[categoryWithTransactions.category] ?: 0f
                },
                itemDetails = null,
                itemToColor = {
                    categoryToColor(it.category)
                },
                middleText = "$yearMonthOfTransactions"
            )
            IconButton(
                onClick = onNextMonth,
                modifier = Modifier.padding(16.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.ArrowForward,
                    contentDescription = "Next",
                    modifier = Modifier.size(32.dp)
                )
            }
        }
        Divider(
            color = Color.Gray,
            thickness = 1.dp,
            modifier = Modifier
                .padding(horizontal = 10.dp)
                .padding(vertical = 16.dp)
        )
        LazyVerticalGrid(
            modifier = Modifier.fillMaxSize(),
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(all = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(categories) {
                CategoryCard(it, onCategoryClicked, categoryToColor(it.category))
            }
        }
    }
}

@Composable
fun CategoryCard(
    categoryWithTransactions: CategoryWithTransactions,
    onCategoryClicked: (Category) -> Unit,
    color: Color
) {
    var isExpanded by remember { mutableStateOf(false) }  // State to manage card expansion

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCategoryClicked(categoryWithTransactions.category) },
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Left content with icon and text
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    val iconName =
                        categoryWithTransactions.category.iconResId // Assuming 'iconName' is the string field
                    val iconResourceId = iconName?.let {
                        LocalContext.current.resources.getIdentifier(
                            "cat_$it",
                            "drawable",
                            LocalContext.current.packageName
                        )
                    } ?: R.drawable.categories // Default icon if none specified

                    val icon = painterResource(id = iconResourceId)

                    Image(
                        painter = icon,
                        contentDescription = "Category Icon",
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .border(2.dp, color, CircleShape)
                            .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f))
                            .padding(8.dp)
                    )

                    Spacer(modifier = Modifier.width(16.dp))

                    Text(
                        text = categoryWithTransactions.category.name,
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = MaterialTheme.colorScheme.onSurface,
                            fontWeight = FontWeight.Bold,
                        ),
                        overflow = TextOverflow.Ellipsis
                    )
                }

                // Expand/Collapse icon
                IconButton(onClick = { isExpanded = !isExpanded }) {
                    Icon(
                        imageVector = if (isExpanded) Icons.Filled.KeyboardArrowUp else Icons.Filled.ArrowDropDown,
                        contentDescription = if (isExpanded) "Collapse" else "Expand"
                    )
                }
            }

            // Transaction summary
            AnimatedVisibility(visible = isExpanded) {
                Column {
                    categoryWithTransactions.transactions.forEach { transaction ->
                        Text(
                            text = "${transaction.transactionRecord.type}: \$${transaction.transactionRecord.amount}",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }
    }
}

/**
@Preview(showBackground = true)
@Composable
fun PreviewCategoryCard() {
val transactions = listOf(
FullTransactionRecord(
transactionRecord = TransactionRecord(
id = 1,
name = "Books",
type = TransactionType.EXPENSE,
accountId = 101,
categoryId = 1,
amount = 79.99f,
date = LocalDateTime.now()
),
account = AccountWithCurrency(
account = Account(
id = 101,
name = "Bank",
currency = "EUR",
initialBalance = 1000f
),
currency = Currency(
"EUR",
1.0f,
updatedTime = LocalDateTime.now()
)
),
category = Category(
id = 1,
name = "Education",
defaultType = CategoryType.Expense,
parentCategoryId = null,
iconResId = null
)
),
FullTransactionRecord(
transactionRecord = TransactionRecord(
id = 2,
name = "Supplies",
type = TransactionType.EXPENSE,
accountId = 101,
categoryId = 1,
amount = 200.99f,
date = LocalDateTime.now()
),
account = AccountWithCurrency(
account = Account(
id = 101,
name = "Bank",
currency = "EUR",
initialBalance = 1000f
),
currency = Currency(
"EUR",
1.0f,
updatedTime = LocalDateTime.now()
)
),
category = Category(
id = 1,
name = "Education",
defaultType = CategoryType.Expense,
parentCategoryId = null,
iconResId = null
)
)
)

// Create a sample CategoryWithTransactions
val sampleCategoryWithTransactions = CategoryWithTransactions(
category = Category(
id = 1,
name = "Education",
defaultType = CategoryType.Expense,
parentCategoryId = null,
iconResId = null
),
transactions = transactions
)

CategoryCard(
categoryWithTransactions = sampleCategoryWithTransactions,
onCategoryClicked = {}
)
}
 **/

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