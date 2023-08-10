package com.example.budgetapplication.data

import android.content.Context
import android.os.Build
import com.example.budgetapplication.R
import com.example.budgetapplication.data.currencies.CurrenciesApiService
import com.example.budgetapplication.data.currencies.CurrenciesRepository
import com.example.budgetapplication.data.currencies.MockCurrenciesRepository
import com.example.budgetapplication.data.currencies.OnlineCurrenciesRepository
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit

interface AppContainer {
    val currenciesRepository: CurrenciesRepository
}

class AppDataContainer(private val context: Context) : AppContainer{

    private val currenciesBaseUrl = "https://api.currencyfreaks.com/v2.0/"
    private val currenciesApiKey: String = context.getString(R.string.CURRENCY_API_KEY)
    @OptIn(ExperimentalSerializationApi::class)
    private val retrofit: Retrofit = Retrofit.Builder()
        .addConverterFactory(Json.asConverterFactory("application/json".toMediaType()))
        .baseUrl(currenciesBaseUrl)
        .build()

    private val currenciesApiService: CurrenciesApiService by lazy {
        retrofit.create(CurrenciesApiService::class.java)
    }

    override val currenciesRepository: CurrenciesRepository by lazy {
        OnlineCurrenciesRepository(
            BudgetDatabase.getDatabase(context).currencyDao(),
            currenciesApiService,
            currenciesApiKey
        )
        //MockCurrenciesRepository()
        //OfflineCurrenciesRepository(BudgetDatabase.getDatabase(context).currencyDao())
    }
}