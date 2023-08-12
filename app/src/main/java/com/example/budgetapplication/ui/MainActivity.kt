package com.example.budgetapplication.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import com.example.budgetapplication.data.currencies.Currency
import com.example.budgetapplication.ui.currencies.CurrenciesSummary
import com.example.budgetapplication.ui.theme.BudgetApplicationTheme
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BudgetApplicationTheme {
                CurrenciesSummary(
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}

