package com.example.budgetapplication.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.budgetapplication.data.currencies.Currency
import com.example.budgetapplication.data.currencies.CurrencyDao

@Database(entities = [Currency::class], version = 1, exportSchema = false)
abstract class BudgetDatabase : RoomDatabase() {

    abstract fun currencyDao(): CurrencyDao

    companion object {

        @Volatile
        private var Instance: BudgetDatabase? = null

        fun getDatabase(context : Context): BudgetDatabase{
            return Instance?: synchronized(this){
                Room.databaseBuilder(context, BudgetDatabase::class.java, "budget_database")
                    .build()
                    .also { Instance = it }
            }
        }
    }

}