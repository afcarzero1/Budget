package com.example.budgetapplication.ui.currencies

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.budgetapplication.data.currencies.CurrenciesRepository
import com.example.budgetapplication.data.currencies.Currency
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
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

    val currenciesUiState: StateFlow<CurrenciesUiState> = combine(
        currenciesListFlow,
        baseCurrencyFlow
    ) { currenciesList, baseCurrency ->
        CurrenciesUiState(
            currenciesList = currenciesList,
            baseCurrency = baseCurrency
        )
    }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
            initialValue = CurrenciesUiState()
        )

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