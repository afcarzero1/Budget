package com.example.budgetapplication.ui.currencies

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.budgetapplication.R
import com.example.budgetapplication.data.currencies.Currency
import com.example.budgetapplication.ui.AppViewModelProvider
import com.example.budgetapplication.ui.components.LargeDropdownMenu
import com.example.budgetapplication.ui.navigation.Currencies
import com.example.budgetapplication.ui.navigation.CurrenciesSettings
import com.example.budgetapplication.ui.theme.InitialScreen
import kotlinx.coroutines.launch
import java.text.DecimalFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


@Composable
fun CurrenciesScreen(navHostController: NavHostController, viewModel: CurrenciesViewModel) {
    InitialScreen(navController = navHostController, destination = Currencies, screenBody = {
        CurrenciesSummary(
            modifier = Modifier.fillMaxSize(),
            viewModel = viewModel
        )
    })
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CurrenciesScreenTopBar(navController: NavHostController) {
    TopAppBar(
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically
            ) {
                // Menu Icon
                IconButton(onClick = {
                    // Handle navigation drawer or menu expansion
                }) {
                    Icon(
                        imageVector = Icons.Filled.Menu,
                        contentDescription = "Menu",
                    )
                }

                // Search Bar in the middle
                OutlinedTextField(
                    value = "", // Bind to a state if needed
                    onValueChange = { /* Update the state here */ },
                    modifier = Modifier
                        .weight(1f) // Takes up all available space between the icons
                        .padding(horizontal = 8.dp)
                        .height(32.dp),
                    placeholder = { Text("Search currency") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text, imeAction = ImeAction.Search
                    ),
                    keyboardActions = KeyboardActions(onSearch = {
                        // Define the search action, possibly navigate or fetch data
                    }),
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Outlined.Search,
                            contentDescription = "Search Currencies"
                        )
                    },
                    shape = RoundedCornerShape(16.dp)
                )

                IconButton(onClick = {
                    navController.navigate(CurrenciesSettings.route)
                }) {
                    Icon(
                        imageVector = Icons.Filled.Settings, contentDescription = "Search"
                    )
                }
            }
        },
    )


}


@Composable
fun CurrenciesSummary(
    modifier: Modifier = Modifier,
    viewModel: CurrenciesViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    Log.d("CurrenciesSummary", "Currencies summary composable")
    val currenciesState by viewModel.currenciesUiState.collectAsState()

    Column(modifier = Modifier.fillMaxWidth()) {
        CurrenciesSummaryBody(
            currenciesList = currenciesState.currenciesList,
            baseCurrency = currenciesState.baseCurrency,
            modifier = modifier
        )
    }
}


@Composable
fun CurrenciesSummaryBody(
    currenciesList: List<Currency>,
    baseCurrency: String,
    modifier: Modifier = Modifier,
) {
    Surface(color = MaterialTheme.colorScheme.surface) {
        CurrenciesList(
            currenciesList = currenciesList, baseCurrency = baseCurrency, modifier = modifier
        )
    }
}


@Composable
fun CurrenciesList(
    currenciesList: List<Currency>,
    baseCurrency: String,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier.fillMaxHeight()
    ) {
        items(items = currenciesList, itemContent = { currency ->
            CurrencyItem(
                currency = currency, baseCurrency = baseCurrency, modifier = modifier
            )
        })
    }

}


@Composable
fun CurrencyItem(
    currency: Currency, baseCurrency: String, modifier: Modifier = Modifier
) {
    var referenceCurrencyFirst by remember { mutableStateOf(false) }
    val decimalFormat = DecimalFormat("#,##0.00###")

    Card(
        modifier = modifier.padding(
            horizontal = 16.dp, vertical = 4.dp
        ), colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
        ), elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {


            val styledText = buildAnnotatedString {
                val firstCurrencyText = if (referenceCurrencyFirst) baseCurrency else currency.name
                val secondCurrencyText = if (referenceCurrencyFirst) currency.name else baseCurrency

                withStyle(
                    style = SpanStyle(
                        fontWeight = FontWeight.Bold,
                        fontSize = MaterialTheme.typography.titleMedium.fontSize
                    )
                ) {
                    append(firstCurrencyText)
                }

                append(" /")

                withStyle(
                    style = SpanStyle(
                        color = Color.Gray, fontSize = MaterialTheme.typography.bodySmall.fontSize
                    )
                ) {
                    append(secondCurrencyText)
                }
            }
            Text(
                text = styledText, modifier = Modifier.padding(start = 20.dp, end = 60.dp)
            )

            Spacer(Modifier.weight(1f))

            Text(
                text = if (referenceCurrencyFirst) decimalFormat.format(currency.value) else decimalFormat.format(
                    1 / currency.value
                ), modifier = Modifier.padding(
                    start = 20.dp, end = 20.dp
                ), fontSize = MaterialTheme.typography.labelMedium.fontSize, style = TextStyle(
                    fontWeight = FontWeight.Bold, // Setting the font weight to bold
                    fontSize = MaterialTheme.typography.labelMedium.fontSize
                )
            )

            IconButton(
                onClick = { referenceCurrencyFirst = !referenceCurrencyFirst },
                modifier = Modifier
                    .padding(
                        start = 20.dp, end = 2.dp, top = 2.dp
                    )
                    .size(18.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.switch_icon),
                    contentDescription = "Refresh",
                    modifier = Modifier.size(16.dp),
                )
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CurrencySettingsScreen(
    navController: NavHostController,
    viewModel: CurrenciesViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val currenciesState by viewModel.currenciesUiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Currencies Settings") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f).padding(16.dp)) {
                    Text(
                        "Base Currency",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                    Text(
                        "The primary currency used for all transactions.",
                        color = Color.Gray,
                        fontSize = 14.sp
                    )
                }
                LargeDropdownMenu(
                    label = "Select Base Currency",
                    items = currenciesState.currenciesList.map { it.name },
                    onItemSelected = { _, item ->
                        viewModel.updateBaseCurrencySafe(item)
                    },
                    initialIndex = currenciesState.currenciesList.indexOfFirst {
                        it.name == currenciesState.baseCurrency
                    },
                    modifier = Modifier.width(160.dp)
                )
            }
        }
    }
}


@Preview
@Composable
fun CurrenciesItemPreview() {
    CurrencyItem(
        currency = currencies[0], baseCurrency = "USD", modifier = Modifier.fillMaxSize()
    )
}


@Preview
@Composable
fun CurrenciesListPreview() {
    CurrenciesList(
        currenciesList = currencies, baseCurrency = "USD", modifier = Modifier.fillMaxSize()
    )

}

@Preview
@Composable
fun CurrenciesSummaryPreview() {
    CurrenciesSummaryBody(
        currenciesList = listOf(currencies[0]), baseCurrency = "USD"
    )
}


private val input = "2023-03-21 12:43:00+00"

// Define a DateTimeFormatter to match the input format
private val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ssX")

// Parse the input string to a LocalDateTime
private val localDateTime = LocalDateTime.parse(input, formatter)

private val currencies = listOf(
    Currency(
        name = "USD", value = 1.0f, updatedTime = localDateTime
    ), Currency(
        name = "EUR", value = 1.1f, updatedTime = localDateTime
    ), Currency(
        name = "SEK", value = 0.1f, updatedTime = localDateTime
    )
)