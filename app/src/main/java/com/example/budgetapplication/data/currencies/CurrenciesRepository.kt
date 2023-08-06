package com.example.budgetapplication.data.currencies

import kotlinx.coroutines.flow.Flow


interface CurrenciesRepository {

    fun getAllCurrenciesStream(): Flow<List<Currency>>

    suspend fun insertCurrency(currency: Currency)

    suspend fun deleteCurrency(currency: Currency)

    suspend fun updateCurrency(currency: Currency)
}