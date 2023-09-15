package com.example.budgetapplication.data

import android.content.Context
import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.budgetapplication.data.accounts.Account
import com.example.budgetapplication.data.accounts.AccountDao
import com.example.budgetapplication.data.categories.Category
import com.example.budgetapplication.data.categories.CategoryDao
import com.example.budgetapplication.data.currencies.Currency
import com.example.budgetapplication.data.currencies.CurrencyDao
import com.example.budgetapplication.data.future_transactions.FutureTransaction
import com.example.budgetapplication.data.future_transactions.FutureTransactionDao
import com.example.budgetapplication.data.transactions.TransactionDao
import com.example.budgetapplication.data.transactions.TransactionRecord
import java.util.Locale

@Database(
    entities = [
        Currency::class,
        Account::class,
        Category::class,
        TransactionRecord::class,
        FutureTransaction::class
    ],
    version = 2,
    exportSchema = false,
)
abstract class BudgetDatabase : RoomDatabase() {

    abstract fun currencyDao(): CurrencyDao

    abstract fun accountDao(): AccountDao

    abstract fun categoryDao(): CategoryDao

    abstract fun transactionDao(): TransactionDao

    abstract fun futureTransactionDao(): FutureTransactionDao

    companion object {

        @Volatile
        private var Instance: BudgetDatabase? = null

        fun getDatabase(context: Context): BudgetDatabase {
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(context, BudgetDatabase::class.java, "budget_database")
                    .build()
                    .also { Instance = it }
            }
        }
    }

}