package com.example.budgetapplication.ui.currencies

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.budgetapplication.data.currencies.Currency
import com.example.budgetapplication.ui.AppViewModelProvider


@Composable
fun CurrenciesSummary(
    modifier: Modifier = Modifier,
    viewModel: CurrenciesViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val currenciesState by viewModel.currenciesUiState.collectAsState()

    Card(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        CurrenciesSummaryBody(
            currenciesList = currenciesState.currenciesList,
            modifier = modifier
        )
    }

}


@Composable
fun CurrenciesSummaryBody(
    currenciesList: List<Currency>,
    modifier: Modifier = Modifier,
) {
    CurrenciesList(currenciesList = currenciesList, modifier = modifier)
}


@Composable
fun CurrenciesList(
    currenciesList: List<Currency>,
    modifier: Modifier = Modifier,
) {
    LazyColumn(modifier = modifier) {
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

            Text(
                text = currency.name + "/USD",
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
            )

            Text(text = currency.value.toString())

        }
    }
}

@Preview
@Composable
fun CurrenciesItemPreview() {
    val currency = Currency(
        name = "USD",
        id = 0,
        value = 1.0f,
        updatedTime = "2021-10-10 10:10:10"
    )

    CurrencyItem(
        currency = currency,
        modifier = Modifier.fillMaxSize()
    )
}


@Preview
@Composable
fun CurrenciesListPreview() {
    val currencies = listOf(
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

    CurrenciesList(
        currenciesList = currencies,
        modifier = Modifier.fillMaxSize()
    )

}