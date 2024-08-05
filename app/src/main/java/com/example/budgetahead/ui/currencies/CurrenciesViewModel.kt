package com.example.budgetahead.ui.currencies

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.budgetahead.data.currencies.CurrenciesRepository
import com.example.budgetahead.data.currencies.Currency
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class CurrenciesViewModel(private val currenciesRepository: CurrenciesRepository) : ViewModel() {
    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }

    private val mutex = Mutex()

    private val currenciesListFlow = currenciesRepository.getAllCurrenciesStream()
    private val baseCurrencyFlow = currenciesRepository.getDefaultCurrencyStream()

    val currenciesUiState: StateFlow<CurrenciesUiState> =
        combine(
            currenciesListFlow,
            baseCurrencyFlow
        ) { currenciesList, baseCurrency ->
            CurrenciesUiState(
                currenciesList = currenciesList,
                baseCurrency = baseCurrency
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
            initialValue = CurrenciesUiState()
        )

    var searchQuery = mutableStateOf("")
        private set

    private val _searchResult = MutableStateFlow<Currency?>(null)
    val searchResult = _searchResult.asStateFlow()

    fun updateSeachQuery(query: String) {
        Log.d("TextFieldViewModel", "Text updated to: $query")
        searchQuery.value = query

        if (query.isNotEmpty()) {
            viewModelScope.launch {
                val matchedCurrency =
                    currenciesListFlow.first().firstOrNull { currency ->
                        currency.name.contains(query, ignoreCase = true)
                    }
                _searchResult.value = matchedCurrency
            }
        }
    }

    fun updateBaseCurrencySafe(newBaseCurrency: String) {
        viewModelScope.launch {
            mutex.withLock {
                updateBaseCurrency(newBaseCurrency)
            }
        }
    }

    private suspend fun updateBaseCurrency(newBaseCurrency: String) {
        val currentListOfCurrencies = currenciesListFlow.first()
        if (currentListOfCurrencies.map { it.name }.contains(newBaseCurrency)) {
            currenciesRepository.setDefaultCurrency(newBaseCurrency)
        }
    }
}

data class CurrenciesUiState(
    val currenciesList: List<Currency> = listOf(),
    val baseCurrency: String = "USD"
)
