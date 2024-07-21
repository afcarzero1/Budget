package com.example.budgetahead.data.currencies

import kotlinx.coroutines.flow.Flow

class OfflineCurrenciesRepository(
    private val currencyDao: CurrencyDao,
) : CurrenciesRepository {
    override fun getAllCurrenciesStream(): Flow<List<Currency>> = currencyDao.getAllCurrencies()

    override fun getDefaultCurrencyStream(): Flow<String> {
        TODO("Not yet implemented")
    }

    override suspend fun setDefaultCurrency(newBaseCurrency: String) {
        TODO("Not yet implemented")
    }
}
