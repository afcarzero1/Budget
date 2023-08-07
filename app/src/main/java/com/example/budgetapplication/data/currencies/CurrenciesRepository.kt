package com.example.budgetapplication.data.currencies

import kotlinx.coroutines.flow.Flow


interface CurrenciesRepository {

    fun getAllCurrenciesStream(): Flow<List<Currency>>

}