package com.example.budgetapplication.ui.currencies

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.budgetapplication.data.currencies.CurrenciesRepository
import com.example.budgetapplication.data.currencies.Currency
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class CurrenciesViewModel(currenciesRepository: CurrenciesRepository) : ViewModel() {

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }

    val currenciesUiState: StateFlow<CurrenciesUiState> = currenciesRepository
        .getAllCurrenciesStream()
        .map { CurrenciesUiState(it) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
            initialValue = CurrenciesUiState()
        )

}


data class CurrenciesUiState(val currenciesList: List<Currency> = listOf())