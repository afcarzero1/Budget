package com.example.budgetapplication.data.currencies

import kotlinx.coroutines.flow.Flow

class OfflineCurrenciesRepository(private val currencyDao: CurrencyDao): CurrenciesRepository {
    override fun getAllCurrenciesStream(): Flow<List<Currency>> = currencyDao.getAllCurrencies()

    override suspend fun insertCurrency(currency: Currency) = currencyDao.insert(currency)

    override suspend fun deleteCurrency(currency: Currency) = currencyDao.delete(currency)

    override suspend fun updateCurrency(currency: Currency) = currencyDao.update(currency)

}