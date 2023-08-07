package com.example.budgetapplication.data.currencies

import kotlinx.coroutines.flow.Flow

class OnlineCurrenciesRepository(private val currencyDao: CurrencyDao): CurrenciesRepository{
    override fun getAllCurrenciesStream(): Flow<List<Currency>> {

        return currencyDao.getAllCurrencies()

        TODO("Not yet implemented")
    }
}