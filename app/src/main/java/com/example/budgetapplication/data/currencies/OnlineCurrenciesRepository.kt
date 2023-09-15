package com.example.budgetapplication.data.currencies

import android.util.Log
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class OnlineCurrenciesRepository(
    private val currencyDao: CurrencyDao,
    private val currenciesApiService: CurrenciesApiService,
    private val apiKey: String
) : CurrenciesRepository {

    private val TAG = "OnlineCurrenciesRepo"

    companion object {
        private val dateString = "2023-03-21 12:43:00+00"

        private val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ssX")
        private val localDateTime = LocalDateTime.parse(dateString, formatter)

        private val defaultCurrencies = listOf(
            Currency(
                name = "USD",
                value = 1.0f,
                updatedTime = localDateTime
            ),
            Currency(
                name = "EUR",
                value = 1.1f,
                updatedTime = localDateTime
            ),
            Currency(
                name = "SEK",
                value = 0.1f,
                updatedTime = localDateTime
            )
        )
    }
    private var isEmpty: Boolean = false

    override fun getAllCurrenciesStream(): Flow<List<Currency>> {
        val currentTime = LocalDateTime.now()
        Log.d(TAG, "Getting all currencies stream at: $currentTime")


        return currencyDao.getAllCurrencies().onEach {
            if (it.isNotEmpty()) {
                val lastUpdatedTime = it[0].updatedTime

                if (lastUpdatedTime < currentTime.minusDays(1)) {
                    Log.d(TAG, "Fetching API data because last update is older than 1 day.")
                    fetchApi()
                } else {
                    Log.d(TAG, "Current data is from $lastUpdatedTime")
                    Log.d(TAG, "Not refreshing data.")
                }
            } else {
                Log.d(TAG, "Fetching API data because database is empty.")
                isEmpty = true
                fetchApi()
            }

        }
    }

    private suspend fun fetchApi() {
        Log.d(TAG, "Fetching data from API...")

        val responses: CurrenciesResponse

        try {
            responses = currenciesApiService.getCurrencies(apiKey)
        } catch (e: Exception) {
            if (isEmpty) {
                // Add default data
                for (currency in defaultCurrencies){
                    currencyDao.insert(currency = currency)
                }
                isEmpty = false
            }
            return
        }


        // Insert all currencies into the database
        // TODO: figure out why dates are saved with 00:00 time
        for (rate in responses.rates) {
            val currency = Currency(
                name = rate.key,
                value = rate.value.toFloat(),
                updatedTime = LocalDateTime.parse(
                    responses.date,
                    DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ssX")
                )
            )
            currencyDao.insertOrReplace(currency)
            isEmpty = false
        }

        Log.d(TAG, "Data fetched from API and inserted into database.")
    }
}