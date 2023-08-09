package com.example.budgetapplication.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.budgetapplication.data.currencies.Currency
import com.example.budgetapplication.ui.currencies.CurrenciesList
import com.example.budgetapplication.ui.currencies.CurrenciesSummary
import com.example.budgetapplication.ui.currencies.CurrenciesSummaryBody
import com.example.budgetapplication.ui.theme.BudgetApplicationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BudgetApplicationTheme {
                CurrenciesSummaryBody(
                    currenciesList = currencies,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
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