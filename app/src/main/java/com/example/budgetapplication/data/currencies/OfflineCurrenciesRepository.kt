package com.example.budgetapplication.data.currencies

import kotlinx.coroutines.flow.Flow

class OfflineCurrenciesRepository(private val currencyDao: CurrencyDao): CurrenciesRepository {
    override fun getAllCurrenciesStream(): Flow<List<Currency>> = currencyDao.getAllCurrencies()
}