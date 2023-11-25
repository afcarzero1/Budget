package com.example.budgetapplication.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
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

@Database(
    entities = [
        Currency::class,
        Account::class,
        Category::class,
        TransactionRecord::class,
        FutureTransaction::class
    ],
    version = 3,
    exportSchema = false,
)
@TypeConverters(DateConverter::class)
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
                    .addMigrations(MIGRATION_2_3)
                    .build()
                    .also { Instance = it }
            }
        }
    }

}

// Previous migration for having columns that are null for transfers.
val MIGRATION_2_3 = object : Migration(2, 3) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("PRAGMA foreign_keys=OFF;")
        database.execSQL(
            "CREATE TABLE IF NOT EXISTS `transactions_new` (" +
                    "`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                    "`name` TEXT NOT NULL, " +
                    "`type` TEXT NOT NULL, " +
                    "`accountId` INTEGER NOT NULL, " +
                    "`categoryId` INTEGER, " +
                    "`amount` REAL NOT NULL, " +
                    "`date` INTEGER NOT NULL, " +
                    "FOREIGN KEY(`accountId`) REFERENCES `accounts`(`id`) ON UPDATE NO ACTION ON DELETE RESTRICT, " +
                    "FOREIGN KEY(`categoryId`) REFERENCES `categories`(`id`) ON UPDATE NO ACTION ON DELETE RESTRICT)"
        )
        database.execSQL(
            "INSERT INTO `transactions_new` (`id`, `name`, `type`, `accountId`, `categoryId`, `amount`, `date`) " +
                    "SELECT `id`, `name`, `type`, `accountId`, `categoryId`, `amount`, `date` FROM `transactions`"
        )
        database.execSQL("DROP TABLE `transactions`")
        database.execSQL("ALTER TABLE `transactions_new` RENAME TO `transactions`")
        database.execSQL("PRAGMA foreign_keys=ON;")
    }
}

