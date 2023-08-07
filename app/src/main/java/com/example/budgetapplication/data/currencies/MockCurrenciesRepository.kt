package com.example.budgetapplication.data.currencies

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow


class MockCurrenciesRepository : CurrenciesRepository {
    override fun getAllCurrenciesStream(): Flow<List<Currency>> {
        // Create a mock list of currencies
        val mockCurrencies = listOf(
            Currency(0,"USD", 1.0f, "2023-08-07"),
            Currency(1,"EUR", 1.1f, "2023-08-07"),
            Currency(2,"SEK", 0.1f, "2023-08-07"),
        )

        // Emit the mock currencies list as a Flow
        return flow {
            emit(mockCurrencies)
        }
    }
}




