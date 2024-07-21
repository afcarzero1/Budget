package com.example.budgetahead.data.currencies

import kotlinx.coroutines.flow.Flow

interface CurrenciesRepository {
    fun getAllCurrenciesStream(): Flow<List<Currency>>

    fun getDefaultCurrencyStream(): Flow<String>

    suspend fun setDefaultCurrency(newBaseCurrency: String)
}
