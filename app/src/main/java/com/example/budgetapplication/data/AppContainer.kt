package com.example.budgetapplication.data

import android.content.Context
import com.example.budgetapplication.data.currencies.CurrenciesRepository
import com.example.budgetapplication.data.currencies.MockCurrenciesRepository
import com.example.budgetapplication.data.currencies.OfflineCurrenciesRepository

interface AppContainer {
    val currenciesRepository: CurrenciesRepository
}

class AppDataContainer(private val context: Context) : AppContainer{

    override val currenciesRepository: CurrenciesRepository by lazy {
        MockCurrenciesRepository()
        //OfflineCurrenciesRepository(BudgetDatabase.getDatabase(context).currencyDao())
    }

}