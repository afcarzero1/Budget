package com.example.budgetapplication.ui

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.budgetapplication.BudgetApplication
import com.example.budgetapplication.ui.currencies.CurrenciesViewModel

object AppViewModelProvider {
    val Factory = viewModelFactory {

        initializer {
            CurrenciesViewModel(budgetApplication().container.currenciesRepository)
        }
    }

}


fun CreationExtras.budgetApplication(): BudgetApplication =
    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as BudgetApplication)