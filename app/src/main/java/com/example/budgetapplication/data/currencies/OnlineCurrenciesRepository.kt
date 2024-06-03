package com.example.budgetapplication.data.currencies


import android.content.Context
import android.util.Log
import android.widget.Toast

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import java.io.IOException
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.concurrent.atomic.AtomicBoolean


class OnlineCurrenciesRepository(
    private val context: Context,
    private val currencyDao: CurrencyDao,
    private val currenciesApiService: CurrenciesApiService,
    private val apiKey: String,
    private val dataStore: DataStore<Preferences>
) : CurrenciesRepository {

    companion object {
        // Offline currencies supported when first opening the app
        private const val dateString = "2023-03-21 12:43:00+00"

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
                value = 1/1.1f,
                updatedTime = localDateTime
            ),
            Currency(
                name = "SEK",
                value = 1/0.1f,
                updatedTime = localDateTime
            )
        )

        val DEFAULT_CURRENCY_KEY = stringPreferencesKey("DEFAULT_CURRENCY")
        const val TAG = "OnlineCurrenciesRepo"

    }

    private val isFetching = AtomicBoolean(false)

    init {
        CoroutineScope(Dispatchers.IO).launch {
            initializeDatabaseIfNeeded()
        }
    }

    private suspend fun initializeDatabaseIfNeeded() {
        val hasCurrencies = currencyDao.getAllCurrencies().first().isNotEmpty()
        if (!hasCurrencies) {
            Log.d(TAG, "Database is empty. Initializing with default currencies.")
            defaultCurrencies.forEach {
                currencyDao.insert(it)
            }
        }
    }

    override fun getAllCurrenciesStream(): Flow<List<Currency>> {
        val currentTime = LocalDateTime.now()
        Log.d(TAG, "Getting all currencies stream at: $currentTime")


        return currencyDao.getAllCurrencies().onEach {
            if (it.isNotEmpty()) {
                val lastUpdatedTime = it[0].updatedTime

                if (lastUpdatedTime < currentTime.minusDays(1)) {
                    Log.d(TAG, "Fetching API data because last update is older than 1 day.")
                    CoroutineScope(Dispatchers.IO).launch {
                        try{
                            //fetchApi()
                        }catch (e: Exception){
                            withContext(Dispatchers.Main) {
                                Toast.makeText(context, "Error fetching currencies", Toast.LENGTH_LONG).show()
                            }
                        }
                    }
                } else {
                    Log.d(TAG, "Current data is from $lastUpdatedTime")
                    Log.d(TAG, "Not refreshing data.")
                }
            } else {
                Log.d(TAG, "Fetching API data because database is empty.")
                CoroutineScope(Dispatchers.IO).launch {
                    try{
                        //fetchApi()
                    }catch (e: Exception){
                        withContext(Dispatchers.Main) {
                            Toast.makeText(context, "Error fetching currencies", Toast.LENGTH_LONG).show()
                        }
                    }
                }
            }
        }
    }

    override fun getDefaultCurrencyStream(): Flow<String> {
        return dataStore.data
            .catch {
                    if (it is IOException) {
                        Log.e(TAG, "Error reading preferences.", it)
                        emit(emptyPreferences())
                    } else {
                        throw it
                    }
                }
            .map {preferences ->
                preferences[DEFAULT_CURRENCY_KEY] ?: "USD"
            }
    }

    override suspend fun setDefaultCurrency(newBaseCurrency: String) {
        dataStore.edit {mutablePreferences ->
            val currentCurrencies = currencyDao.getAllCurrencies().first()
            val newBaseCurrencyValue = currentCurrencies.first { it.name == newBaseCurrency }.value

            for (currency in currentCurrencies){
                val updatedCurrency = currency.copy(
                    value = currency.value / newBaseCurrencyValue
                )
                currencyDao.update(updatedCurrency)
            }

            mutablePreferences[DEFAULT_CURRENCY_KEY] = newBaseCurrency
        }

    }

    private suspend fun fetchApi() {
        if (isFetching.compareAndSet(false, true)) {
            Log.d(TAG, "Fetching data from API...")
            try {
                val responses: CurrenciesResponse = currenciesApiService.getCurrencies(apiKey)
                val currentBaseCurrency: String = getDefaultCurrencyStream().first()
                val currentBaseCurrencyRate = responses.rates[currentBaseCurrency]?.toFloat()

                if (currentBaseCurrencyRate == null) {
                    Log.e(TAG, "Error fetching data from API.")
                    return
                }

                // processing and inserting into the database

                Log.d(TAG, "Data fetched from API and inserted into database.")
            } catch (e: Exception) {
                Log.e(TAG, "Error while fetching data $e")
            } finally {
                isFetching.set(false)
            }
        } else {
            Log.d(TAG, "Fetch request ignored because a previous one is still running.")
        }
    }
}