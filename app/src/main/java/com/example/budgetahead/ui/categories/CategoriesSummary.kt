package com.example.budgetahead.ui.categories

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.budgetahead.R
import com.example.budgetahead.data.categories.Category
import com.example.budgetahead.data.categories.CategoryType
import com.example.budgetahead.data.categories.CategoryWithTransactions
import com.example.budgetahead.data.currencies.Currency
import com.example.budgetahead.ui.AppViewModelProvider
import com.example.budgetahead.ui.components.PieChart
import com.example.budgetahead.ui.components.TextPiece
import com.example.budgetahead.ui.navigation.Categories
import com.example.budgetahead.ui.navigation.CategoryEntry
import com.example.budgetahead.ui.navigation.CategoryOverview
import com.example.budgetahead.ui.navigation.TabItem
import com.example.budgetahead.ui.navigation.TabbedPage
import com.example.budgetahead.ui.theme.InitialScreen
import com.example.budgetahead.use_cases.IconFromReIdUseCase
import java.time.YearMonth

@Composable
fun CategoriesSummary(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    viewModel: CategoriesSummaryViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val categoriesState by viewModel.categoriesUiState.collectAsState()
    val baseCurrency by viewModel.baseCurrency.collectAsState()
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
                    tabs =
                    listOf(
                        TabItem(
                            title = "Expense",
                            icon = {
                                Icon(
                                    painter = painterResource(
                                        id = R.drawable.payments_24dp_fill0_wght400_grad0_opsz24
                                    ),
                                    contentDescription = "Expenses",
                                    tint = Color(0xFFE57373)
                                )
                            },
                            screen = {
                                CategoriesSummaryBody(
                                    categories =
                                    categoriesState.categoriesList.filter {
                                        it.category.defaultType == CategoryType.Expense
                                    },
                                    onCategoryClicked = {
                                        navController.navigate(
                                            "${CategoryOverview.route}/${it.id}"
                                        )
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
                                    deltas = categoriesState.categoriesDelta,
                                    baseCurrency = baseCurrency
                                )
                            }
                        ),
                        TabItem(
                            title = "Income",
                            icon = {
                                Icon(
                                    painter = painterResource(
                                        id = R.drawable.payments_24dp_fill0_wght400_grad0_opsz24
                                    ),
                                    contentDescription = "Incomes",
                                    tint = Color(0xFF4CAF50)
                                )
                            },
                            screen = {
                                CategoriesSummaryBody(
                                    categories =
                                    categoriesState.categoriesList.filter {
                                        it.category.defaultType == CategoryType.Income
                                    },
                                    onCategoryClicked = {
                                        navController.navigate(
                                            "${CategoryOverview.route}/${it.id}"
                                        )
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
                                    deltas = categoriesState.categoriesDelta,
                                    baseCurrency = baseCurrency
                                )
                            }
                        )
                    )
                )
            }
        },
        floatingButton = {
            FloatingActionButton(
                onClick = {
                    navController.navigate(CategoryEntry.route)
                },
                modifier = Modifier.padding(16.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = "Add Category"
                )
            }
        }
    )
}

@Composable
fun CategoriesSummaryBody(
    categories: List<CategoryWithTransactions>,
    deltas: Map<Category, Float>,
    baseCurrency: String,
    yearMonthOfTransactions: YearMonth,
    categoryToColor: (Category) -> Color,
    onNextMonth: () -> Unit,
    onPreviousMonth: () -> Unit,
    onCategoryClicked: (Category) -> Unit
) {
    val totalDelta = categories.sumOf { deltas[it.category]?.toDouble() ?: 0.0 }

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
                middleText =
                listOf(
                    TextPiece(
                        text =
                        buildAnnotatedString {
                            withStyle(
                                style =
                                SpanStyle(
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            ) {
                                append(yearMonthOfTransactions.toString())
                            }
                        }
                    ),
                    TextPiece(
                        text =
                        buildAnnotatedString {
                            withStyle(
                                style =
                                SpanStyle(
                                    fontWeight = FontWeight.Normal,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            ) {
                                append(
                                    Currency.formatAmountStatic(
                                        baseCurrency,
                                        totalDelta.toFloat()
                                    )
                                )
                            }
                        }
                    )
                )
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
            modifier =
            Modifier
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
                CategoryCard(
                    it,
                    Pair(baseCurrency, deltas[it.category] ?: 0f),
                    onCategoryClicked,
                    categoryToColor(it.category)
                )
            }
        }
    }
}

@Composable
fun CategoryCard(
    categoryWithTransactions: CategoryWithTransactions,
    totalAmount: Pair<String, Float>,
    onCategoryClicked: (Category) -> Unit,
    color: Color
) {
    Card(
        modifier =
        Modifier
            .fillMaxWidth()
            .clickable { onCategoryClicked(categoryWithTransactions.category) }
    ) {
        Column(
            modifier =
            Modifier
                .padding(2.dp)
                .fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Left content with icon and text
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(4f)
                ) {
                    val iconResourceId =
                        IconFromReIdUseCase(LocalContext.current).getCategoryIconResId(
                            categoryWithTransactions.category.iconResId
                        )

                    Icon(
                        painter = painterResource(id = iconResourceId),
                        contentDescription = "Category Icon",
                        modifier =
                        Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .border(2.dp, color, CircleShape)
                            .background(MaterialTheme.colorScheme.tertiaryContainer)
                            .padding(8.dp),
                        tint = MaterialTheme.colorScheme.onTertiaryContainer
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Column {
                        Text(
                            text = categoryWithTransactions.category.name,
                            style =
                            MaterialTheme.typography.titleSmall.copy(
                                color = MaterialTheme.colorScheme.onSurface,
                                fontWeight = FontWeight.Bold
                            ),
                            overflow = TextOverflow.Ellipsis,
                            maxLines = 1
                        )
                        Text(
                            text =
                            Currency.formatAmountStatic(
                                totalAmount.first,
                                totalAmount.second
                            ),
                            style =
                            MaterialTheme.typography.bodyMedium.copy(
                                color = MaterialTheme.colorScheme.onSurface,
                                fontWeight = FontWeight.ExtraLight
                            )
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
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "No categories yet. Create one by clicking the + button",
            modifier = Modifier.padding(16.dp)
        )
    }
}
