import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.budgetahead.data.BudgetDatabase
import com.example.budgetahead.data.accounts.Account
import com.example.budgetahead.data.accounts.AccountDao
import com.example.budgetahead.data.categories.Category
import com.example.budgetahead.data.categories.CategoryDao
import com.example.budgetahead.data.categories.CategoryType
import com.example.budgetahead.data.currencies.Currency
import com.example.budgetahead.data.currencies.CurrencyDao
import com.example.budgetahead.data.transactions.FullTransactionRecord
import com.example.budgetahead.data.transactions.TransactionDao
import com.example.budgetahead.data.transactions.TransactionRecord
import com.example.budgetahead.data.transactions.TransactionType
import com.example.budgetahead.data.transfers.Transfer
import java.io.IOException
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class BudgetDatabaseTest {
    private lateinit var accountDao: AccountDao
    private lateinit var currencyDao: CurrencyDao
    private lateinit var categoryDao: CategoryDao
    private lateinit var transactionDao: TransactionDao

    private lateinit var budgetDatabase: BudgetDatabase

    private val dateString = "2023-03-21 12:43:00+00"

    private val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ssX")
    private val localDateTime = LocalDateTime.parse(dateString, formatter)

    // Mock data
    private val currencies =
        listOf(
            Currency(
                name = "USD",
                value = 1.0f,
                updatedTime = localDateTime
            ),
            Currency(
                name = "EUR",
                value = 1.1f,
                updatedTime = localDateTime
            ),
            Currency(
                name = "SEK",
                value = 0.1f,
                updatedTime = localDateTime
            )
        )
    private val accounts =
        listOf(
            Account(
                id = 1,
                name = "JPMorgan Chase",
                currency = "USD",
                initialBalance = 100.0f
            ),
            Account(
                id = 2,
                name = "Deutsche Bank",
                currency = "EUR",
                initialBalance = 200.0f
            ),
            Account(
                id = 3,
                name = "SEB",
                currency = "SEK",
                initialBalance = 30000f
            )
        )
    private val categories =
        listOf(
            Category(
                id = 1,
                name = "Food",
                defaultType = CategoryType.Expense,
                parentCategoryId = null
            ),
            Category(
                id = 2,
                name = "Transportation",
                defaultType = CategoryType.Expense,
                parentCategoryId = null
            ),
            Category(
                id = 3,
                name = "Rent",
                defaultType = CategoryType.Expense,
                parentCategoryId = null
            ),
            Category(
                id = 4,
                name = "Salary",
                defaultType = CategoryType.Income,
                parentCategoryId = null
            )
        )
    private val transactions =
        listOf(
            TransactionRecord(
                id = 1,
                name = "Burger King",
                type = TransactionType.EXPENSE,
                accountId = 1,
                categoryId = 1,
                amount = 10.0f,
                date = localDateTime
            ),
            TransactionRecord(
                id = 2,
                name = "Uber",
                type = TransactionType.EXPENSE,
                accountId = 1,
                categoryId = 2,
                amount = 20.0f,
                date = localDateTime.plusHours(1)
            ),
            TransactionRecord(
                id = 3,
                name = "Rent",
                type = TransactionType.EXPENSE,
                accountId = 1,
                categoryId = 3,
                amount = 1000.0f,
                date = localDateTime.plusDays(3)
            ),
            TransactionRecord(
                id = 4,
                name = "Salary",
                type = TransactionType.INCOME,
                accountId = 1,
                categoryId = 4,
                amount = 10000.0f,
                date = localDateTime.plusDays(5)
            ),
            TransactionRecord(
                id = 5,
                name = "Burger King",
                type = TransactionType.EXPENSE,
                accountId = 2,
                categoryId = 1,
                amount = 15.0f,
                date = localDateTime.plusDays(7)
            ),
            // Late transactions
            TransactionRecord(
                id = 6,
                name = "Rent",
                type = TransactionType.EXPENSE,
                accountId = 1,
                categoryId = 3,
                amount = 1000.0f,
                date = localDateTime.plusDays(35)
            ),
            TransactionRecord(
                id = 7,
                name = "Burger King",
                type = TransactionType.EXPENSE,
                accountId = 2,
                categoryId = 1,
                amount = 20.0f,
                date = localDateTime.plusDays(40)
            )
        )

    @Before
    fun createDb() {
        val context: Context = ApplicationProvider.getApplicationContext()
        // Using an in-memory database because the information stored here disappears when the
        // process is killed.
        budgetDatabase =
            Room
                .inMemoryDatabaseBuilder(context, BudgetDatabase::class.java)
                // Allowing main thread queries, just for testing.
                .allowMainThreadQueries()
                .build()

        currencyDao = budgetDatabase.currencyDao()
        accountDao = budgetDatabase.accountDao()
        categoryDao = budgetDatabase.categoryDao()
        transactionDao = budgetDatabase.transactionDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        budgetDatabase.close()
    }

    private suspend fun addAllItemsToDb() {
        for (currency in currencies) {
            currencyDao.insert(currency)
        }

        for (account in accounts) {
            accountDao.insert(account)
        }

        for (category in categories) {
            categoryDao.insert(category)
        }

        for (transaction in transactions) {
            transactionDao.insert(transaction)
        }
    }

    @Test
    @Throws(Exception::class)
    fun daoGetAllAccountsTest() = runBlocking {
        addAllItemsToDb()
        val allAccounts = accountDao.getAllAccounts().first()
        for (account in allAccounts) {
            assertTrue(accounts.contains(account))
        }
    }

    @Test
    @Throws(Exception::class)
    fun daoGetAllCategoriesTest() = runBlocking {
        addAllItemsToDb()
        val allCategories = categoryDao.getAllCategoriesStream().first()
        for (category in allCategories) {
            assertTrue(categories.contains(category))
        }
    }

    @Test
    @Throws(Exception::class)
    fun daoGetAllTransactionsTest() = runBlocking {
        addAllItemsToDb()
        val allTransactions = transactionDao.getAllTransactionsStream().first()
        for (transaction in allTransactions) {
            assertTrue(transactions.contains(transaction))
        }
    }

    @Test
    @Throws(Exception::class)
    fun daoGetAllCategoriesWithTransactionsTest() = runBlocking {
        addAllItemsToDb()
        val allCategories = categoryDao.getAllCategoriesWithTransactionsStream().first()
        for (category in allCategories) {
            assertTrue(categories.contains(category.category))
            for (transaction in category.transactions) {
                assertTrue(transaction.transactionRecord.categoryId == category.category.id)
                assertTrue(transactions.contains(transaction.transactionRecord))
            }
        }
    }

    @Test
    @Throws(Exception::class)
    fun getAllAccountsWithTransactionsTest() {
        runBlocking {
            addAllItemsToDb()
            val allAccounts = accountDao.getAllAccountsWithTransactions().first()
            for (account in allAccounts) {
                assertTrue(accounts.contains(account.account))
                for (transaction in account.transactionRecords) {
                    assertTrue(transaction.accountId == account.account.id)
                    assertTrue(transactions.contains(transaction))
                }

                if (account.account.name == "JPMorgan Chase") {
                    assertTrue(
                        account.transactionRecords.size ==
                            transactions.filter { it.accountId == 1 }.size
                    )

                    assertTrue(
                        account.balance ==
                            transactions
                                .filter { it.accountId == 1 }
                                .map {
                                    if (it.type ==
                                        TransactionType.EXPENSE
                                    ) {
                                        -it.amount
                                    } else {
                                        it.amount
                                    }
                                }
                                .sum()
                    )
                }
            }
        }
    }

    @Test
    @Throws(Exception::class)
    fun getAllFullTransactionsTest() {
        runBlocking {
            addAllItemsToDb()
            val allTransactions: List<FullTransactionRecord> =
                transactionDao.getAllFullTransactionsStream().first()
            for (transaction in allTransactions) {
                assertTrue(transactions.contains(transaction.transactionRecord))
                assertTrue(accounts.contains(transaction.account.account))
                assertTrue(currencies.contains(transaction.account.currency))
                assertTrue(categories.contains(transaction.category))
            }
        }
    }

    @Test
    @Throws(Exception::class)
    fun getCategoriesWithTransactions() = runBlocking {
        addAllItemsToDb()

        val allCategories = categoryDao.getAllCategoriesWithTransactionsStream().first()

        assertTrue(allCategories.size == categories.size)
    }

    @Test
    @Throws(Exception::class)
    fun getCategoriesWithTransactionsByDate() = runBlocking {
        addAllItemsToDb()

        val allCategories =
            categoryDao
                .getAllCategoriesWithTransactionsStream(
                    localDateTime,
                    localDateTime.plusDays(5)
                ).first()

        assertTrue(allCategories.size == categories.size)

        for ((category, transactions) in allCategories) {
            for (transaction in transactions) {
                assertTrue(transaction.transactionRecord.date < localDateTime.plusDays(5))
            }
        }
    }

    @Test
    @Throws(Exception::class)
    fun testTransfers() = runBlocking {
        addAllItemsToDb()

        transactionDao.insertTransferAndTransactions(
            Transfer(
                id = 0,
                sourceAccountId = 1,
                destinationAccountId = 2,
                destinationAccountTransactionId = -1,
                sourceAccountTransactionId = -1,
                amountDestination = 10f,
                amountSource = 10f,
                date = localDateTime.plusDays(5)
            )
        )

        val transfers = transactionDao.getAllTransfersWithAccountsStream().first()

        assertTrue(transfers.count() == 1)

        val transactions = transactionDao.getAllTransactionsStream().first()
        val fullTransactions = transactionDao.getAllFullTransactionsStream().first()

        val accounts = accountDao.getAllFullAccounts().first()
    }

    @Test(expected = Exception::class)
    fun daoInsertAccountWithWrongCurrency() {
        val account =
            Account(
                id = 4,
                name = "Banco de Bogota",
                currency = "COP",
                initialBalance = 1000000.0f
            )

        runBlocking {
            accountDao.insert(account)
        }
    }

    @Test(expected = Exception::class)
    fun deleteWrongCurrency() = runBlocking {
        addAllItemsToDb()
        currencyDao.delete(currencies[0])
    }
}
