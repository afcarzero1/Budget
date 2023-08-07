package com.example.budgetapplication.ui.currencies

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.budgetapplication.ui.AppViewModelProvider
import androidx.compose.runtime.getValue
import com.example.budgetapplication.data.currencies.Currency
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.ui.unit.dp


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
        modifier = modifier,
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {

        Row(modifier = modifier) {

            Column(modifier = modifier) {
                //todo: change to currency icon
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = "Info",
                    modifier = modifier
                )
            }
            Column(modifier = modifier) {
                Text(
                    text = currency.name,
                    modifier = modifier
                )

                Text(
                    text = currency.value.toString(),
                    modifier = modifier
                )
            }

        }
    }
}