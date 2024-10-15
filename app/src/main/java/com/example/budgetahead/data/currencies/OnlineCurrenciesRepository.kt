package com.example.budgetahead.data.currencies

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.io.IOException
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.concurrent.atomic.AtomicBoolean

class OnlineCurrenciesRepository(
    private val context: Context,
    private val currencyDao: CurrencyDao,
    private val currenciesApiService: CurrenciesApiService,
    private val apiKey: String,
    private val dataStore: DataStore<Preferences>,
) : CurrenciesRepository {
    companion object {
        // Offline currencies supported when first opening the app
        private const val dateString = "2023-03-21 12:43:00+00"

        private val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ssX")
        private val localDateTime = LocalDateTime.parse(dateString, formatter)

        private val defaultCurrencies =
            listOf(
                Currency(
                    name = "USD",
                    value = 1.0f,
                    updatedTime = localDateTime,
                ),
                Currency(
                    name = "EUR",
                    value = 1 / 1.1f,
                    updatedTime = localDateTime,
                ),
                Currency(
                    name = "SEK",
                    value = 1 / 0.1f,
                    updatedTime = localDateTime,
                ),
            )

        val DEFAULT_CURRENCY_KEY = stringPreferencesKey("DEFAULT_CURRENCY")
        const val TAG = "OnlineCurrenciesRepo"
    }

    private val isFetching = AtomicBoolean(false)
    private val isEmpty = AtomicBoolean(false)

    init {
        CoroutineScope(Dispatchers.IO).launch {
            initializeDatabaseIfNeeded()
        }
    }

    private suspend fun initializeDatabaseIfNeeded() {
        val currencies = currencyDao.getAllCurrencies().first()

        if (currencies.isEmpty() || currencies[0].updatedTime < LocalDateTime.now().minusDays(1)) {
            val currentTime = LocalDateTime.now()
            Log.d(TAG, "Getting all currencies stream at: $currentTime")

            if (currencies.isEmpty()) {
                isEmpty.set(true)
            }

            fetchApi()
        }
    }

    override fun getAllCurrenciesStream(): Flow<List<Currency>> = currencyDao.getAllCurrencies()

    override fun getDefaultCurrencyStream(): Flow<String> =
        dataStore.data
            .catch {
                if (it is IOException) {
                    Log.e(TAG, "Error reading preferences.", it)
                    emit(emptyPreferences())
                } else {
                    throw it
                }
            }.map { preferences ->
                preferences[DEFAULT_CURRENCY_KEY] ?: "USD"
            }

    override suspend fun setDefaultCurrency(newBaseCurrency: String) {
        dataStore.edit { mutablePreferences ->
            val currentCurrencies = currencyDao.getAllCurrencies().first()
            val newBaseCurrencyValue = currentCurrencies.first { it.name == newBaseCurrency }.value

            for (currency in currentCurrencies) {
                val updatedCurrency =
                    currency.copy(
                        value = currency.value / newBaseCurrencyValue,
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
                for (rate in responses.rates) {
                    val currency =
                        Currency(
                            name = rate.key,
                            value = rate.value.toFloat() / currentBaseCurrencyRate,
                            updatedTime =
                                LocalDateTime.parse(
                                    responses.date,
                                    DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ssX"),
                                ),
                        )
                    currencyDao.insertOrReplace(currency)
                }

                Log.d(TAG, "Data fetched from API and inserted into database.")
            } catch (e: Exception) {
                if (isEmpty.compareAndSet(true, false)) {
                    for (defaultCurrency in defaultCurrencies) {
                        currencyDao.insertOrReplace(defaultCurrency)
                    }
                }
                Log.e(TAG, "Error while fetching data $e")
            } finally {
                isFetching.set(false)
            }
        } else {
            Log.d(TAG, "Fetch request ignored because a previous one is still running.")
        }
    }
}
