package com.example.budgetapplication.data

import android.content.Context
import android.os.Build
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.example.budgetapplication.R
import com.example.budgetapplication.data.accounts.AccountsRepository
import com.example.budgetapplication.data.accounts.OfflineAccountsRepository
import com.example.budgetapplication.data.balances.BalancesRepository
import com.example.budgetapplication.data.balances.OfflineBalancesRepository
import com.example.budgetapplication.data.categories.CategoriesRepository
import com.example.budgetapplication.data.categories.OfflineCategoriesRepository
import com.example.budgetapplication.data.currencies.CurrenciesApiService
import com.example.budgetapplication.data.currencies.CurrenciesRepository
import com.example.budgetapplication.data.currencies.MockCurrenciesRepository
import com.example.budgetapplication.data.currencies.OnlineCurrenciesRepository
import com.example.budgetapplication.data.future_transactions.FutureTransactionsRepository
import com.example.budgetapplication.data.future_transactions.OfflineFutureTransactionsRepository
import com.example.budgetapplication.data.transactions.OfflineTransactionsRepository
import com.example.budgetapplication.data.transactions.TransactionsRepository
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

interface AppContainer {
    val currenciesRepository: CurrenciesRepository

    val accountsRepository: AccountsRepository

    val transactionsRepository: TransactionsRepository

    val categoriesRepository: CategoriesRepository

    val futureTransactionsRepository: FutureTransactionsRepository

    val balancesRepository: BalancesRepository
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
            currenciesApiKey,
            context.dataStore
        )
    }

    override val accountsRepository: AccountsRepository by lazy {
        OfflineAccountsRepository(
            BudgetDatabase.getDatabase(context).accountDao(),
            currenciesRepository
        )
    }

    override val categoriesRepository: CategoriesRepository by lazy {
        OfflineCategoriesRepository(BudgetDatabase.getDatabase(context).categoryDao())
    }

    override val transactionsRepository: TransactionsRepository by lazy {
        OfflineTransactionsRepository(BudgetDatabase.getDatabase(context).transactionDao())
    }

    override val futureTransactionsRepository: FutureTransactionsRepository by lazy {
        OfflineFutureTransactionsRepository(BudgetDatabase.getDatabase(context).futureTransactionDao())
    }

    override val balancesRepository: BalancesRepository by lazy {
        OfflineBalancesRepository(
            accountsRepository = accountsRepository,
            transactionsRepository = transactionsRepository,
            futureTransactionsRepository = futureTransactionsRepository
        )
    }
}