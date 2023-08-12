package com.example.budgetapplication.data.currencies

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


class MockCurrenciesRepository : CurrenciesRepository {
    override fun getAllCurrenciesStream(): Flow<List<Currency>> {
        val input = "2023-03-21 12:43:00+00"

        // Define a DateTimeFormatter to match the input format
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ssX")

        // Parse the input string to a LocalDateTime
        val localDateTime = LocalDateTime.parse(input, formatter)

        // Create a mock list of currencies
        val mockCurrencies = listOf(
            Currency("USD", 1.0f, localDateTime),
            Currency("EUR", 1.1f, localDateTime),
            Currency("SEK", 0.1f, localDateTime),
        )

        // Emit the mock currencies list as a Flow
        return flow {
            emit(mockCurrencies)
        }
    }
}




