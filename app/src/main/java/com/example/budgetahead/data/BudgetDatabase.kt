package com.example.budgetahead.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.budgetahead.data.accounts.Account
import com.example.budgetahead.data.accounts.AccountDao
import com.example.budgetahead.data.categories.Category
import com.example.budgetahead.data.categories.CategoryDao
import com.example.budgetahead.data.currencies.Currency
import com.example.budgetahead.data.currencies.CurrencyDao
import com.example.budgetahead.data.future_transactions.FutureTransaction
import com.example.budgetahead.data.future_transactions.FutureTransactionDao
import com.example.budgetahead.data.transactions.TransactionDao
import com.example.budgetahead.data.transactions.TransactionRecord
import com.example.budgetahead.data.transfers.Transfer

@Database(
    entities = [
        Currency::class,
        Account::class,
        Category::class,
        TransactionRecord::class,
        FutureTransaction::class,
        Transfer::class
    ],
    version = 4,
    exportSchema = true
)
@TypeConverters(DateConverter::class, CategoryTypeConverter::class)
abstract class BudgetDatabase : RoomDatabase() {
    abstract fun currencyDao(): CurrencyDao

    abstract fun accountDao(): AccountDao

    abstract fun categoryDao(): CategoryDao

    abstract fun transactionDao(): TransactionDao

    abstract fun futureTransactionDao(): FutureTransactionDao

    companion object {
        @Volatile
        private var Instance: BudgetDatabase? = null

        fun getDatabase(context: Context): BudgetDatabase = Instance ?: synchronized(this) {
            Room
                .databaseBuilder(context, BudgetDatabase::class.java, "budget_database")
                .createFromAsset("database/budget_db.db")
                .build()
                .also { Instance = it }
        }
    }
}
