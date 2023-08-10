package com.example.budgetapplication.ui.currencies

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.budgetapplication.R
import com.example.budgetapplication.data.currencies.Currency
import com.example.budgetapplication.ui.AppViewModelProvider

@Composable
fun CurrenciesSummary(
    modifier: Modifier = Modifier,
    viewModel: CurrenciesViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val currenciesState by viewModel.currenciesUiState.collectAsState()

    CurrenciesSummaryBody(
        currenciesList = currenciesState.currenciesList,
        modifier = modifier
    )

}


@Composable
fun CurrenciesSummaryBody(
    currenciesList: List<Currency>,
    modifier: Modifier = Modifier,
) {
    CurrenciesList(
        currenciesList = currenciesList,
        modifier = modifier
    )
}


@Composable
fun CurrenciesList(
    currenciesList: List<Currency>,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier.fillMaxHeight()
    ) {
        items(
            items = currenciesList,
            itemContent = { currency ->
                CurrencyItem(
                    currency = currency,
                    modifier = modifier
                )
            }
        )
    }

}


@Composable
fun CurrencyItem(
    currency: Currency,
    modifier: Modifier = Modifier,
) {
    var referenceCurrencyFirst by remember { mutableStateOf(false) }

    Card(
        modifier = modifier.padding(
            horizontal = 16.dp,
            vertical = 4.dp
        ),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {

            Column() {
                Text(
                    text = if (referenceCurrencyFirst)
                        "USD/" + currency.name else
                        currency.name + "/USD",
                    modifier = Modifier.padding(
                        start = 20.dp,
                        end = 20.dp
                    ),
                )

                Text(
                    text = currency.updatedTime,
                    modifier = Modifier.padding(
                        start = 20.dp,
                        end = 20.dp
                    ),
                    fontSize = MaterialTheme.typography.labelSmall.fontSize
                )
            }

            Text(
                text = if (referenceCurrencyFirst)
                    currency.value.toString() else
                    (1 / currency.value).toString(),
                modifier = Modifier.padding(
                    start = 20.dp,
                    end = 20.dp
                ),
                fontSize = MaterialTheme.typography.labelMedium.fontSize
            )

            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.TopEnd,
            ) {
                IconButton(
                    onClick = { referenceCurrencyFirst = !referenceCurrencyFirst },
                    modifier = Modifier
                        .padding(
                            start = 20.dp,
                            end = 2.dp,
                            top = 2.dp
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
}

@Preview
@Composable
fun CurrenciesItemPreview() {
    CurrencyItem(
        currency = currencies[0],
        modifier = Modifier.fillMaxSize()
    )
}


@Preview
@Composable
fun CurrenciesListPreview() {
    CurrenciesList(
        currenciesList = currencies,
        modifier = Modifier.fillMaxSize()
    )

}

@Preview
@Composable
fun CurrenciesSummaryPreview() {
    CurrenciesSummaryBody(
        currenciesList = listOf(currencies[0]),
    )
}


private val currencies = listOf(
    Currency(
        name = "USD",
        id = 0,
        value = 1.0f,
        updatedTime = "2021-10-10 10:10:10"
    ),
    Currency(
        name = "EUR",
        id = 0,
        value = 1.1f,
        updatedTime = "2021-10-10 10:10:10"
    ),
    Currency(
        name = "SEK",
        id = 0,
        value = 0.1f,
        updatedTime = "2021-10-10 10:10:10"
    )
)