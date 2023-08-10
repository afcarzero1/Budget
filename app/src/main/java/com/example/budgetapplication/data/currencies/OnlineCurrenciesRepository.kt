package com.example.budgetapplication.data.currencies

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.concurrent.TimeUnit

class OnlineCurrenciesRepository(
    private val currencyDao: CurrencyDao,
    private val currenciesApiService: CurrenciesApiService,
    private val apiKey: String
) : CurrenciesRepository {
    override fun getAllCurrenciesStream(): Flow<List<Currency>> {
        val currentTime = System.currentTimeMillis()
        val threshold = TimeUnit.DAYS.toMillis(1) // 1 day threshold

        return currencyDao.getAllCurrencies().map { currencies ->
            if (currencies.isEmpty()) {
                fetchApi()
            }
            currencies
        }
    }


    private suspend fun fetchApi() {
        val responses: CurrenciesResponse = currenciesApiService.getCurrencies(apiKey)

        for (rate in responses.rates){
            val currency = Currency(
                name = rate.key,
                value = rate.value.toFloat(),
                updatedTime = responses.date,
            )
            currencyDao.insert(currency)
        }

    }


}