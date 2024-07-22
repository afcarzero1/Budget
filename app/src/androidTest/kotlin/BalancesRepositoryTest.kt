import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.budgetahead.data.BudgetDatabase
import com.example.budgetahead.data.accounts.Account
import com.example.budgetahead.data.accounts.AccountDao
import com.example.budgetahead.data.accounts.AccountsRepository
import com.example.budgetahead.data.accounts.OfflineAccountsRepository
import com.example.budgetahead.data.balances.BalancesRepository
import com.example.budgetahead.data.balances.OfflineBalancesRepository
import com.example.budgetahead.data.categories.Category
import com.example.budgetahead.data.categories.CategoryDao
import com.example.budgetahead.data.categories.CategoryType
import com.example.budgetahead.data.categories.OfflineCategoriesRepository
import com.example.budgetahead.data.currencies.CurrenciesRepository
import com.example.budgetahead.data.currencies.CurrencyDao
import com.example.budgetahead.data.currencies.MockCurrenciesRepository
import com.example.budgetahead.data.future_transactions.FutureTransaction
import com.example.budgetahead.data.future_transactions.FutureTransactionDao
import com.example.budgetahead.data.future_transactions.FutureTransactionsRepository
import com.example.budgetahead.data.future_transactions.OfflineFutureTransactionsRepository
import com.example.budgetahead.data.future_transactions.RecurrenceType
import com.example.budgetahead.data.transactions.OfflineTransactionsRepository
import com.example.budgetahead.data.transactions.TransactionDao
import com.example.budgetahead.data.transactions.TransactionRecord
import com.example.budgetahead.data.transactions.TransactionType
import com.example.budgetahead.data.transactions.TransactionsRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException
import java.time.LocalDate
import java.time.LocalDateTime

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

@RunWith(AndroidJUnit4::class)
class BalancesRepositoryTest {
    private lateinit var budgetDatabase: BudgetDatabase

    private lateinit var offlineBalancesRepository: BalancesRepository

    private lateinit var currenciesRepository: CurrenciesRepository
    private lateinit var categoriesRepository: OfflineCategoriesRepository
    private lateinit var accountRepository: AccountsRepository
    private lateinit var transactionsRepository: TransactionsRepository
    private lateinit var futureTransactionsRepository: FutureTransactionsRepository

    private lateinit var accountDao: AccountDao
    private lateinit var currencyDao: CurrencyDao
    private lateinit var categoryDao: CategoryDao
    private lateinit var transactionDao: TransactionDao
    private lateinit var futureTransactionDao: FutureTransactionDao

    private val fakeCategories =
        listOf(
            Category(
                id = 1,
                name = "Food",
                defaultType = CategoryType.Expense,
                parentCategoryId = null,
            ),
            Category(
                id = 2,
                name = "Transportation",
                defaultType = CategoryType.Expense,
                parentCategoryId = null,
            ),
            Category(
                id = 3,
                name = "Rent",
                defaultType = CategoryType.Expense,
                parentCategoryId = null,
            ),
            Category(
                id = 4,
                name = "Salary",
                defaultType = CategoryType.Income,
                parentCategoryId = null,
            ),
        )

    @Before
    fun setup() {
        val context: Context = ApplicationProvider.getApplicationContext()
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
        futureTransactionDao = budgetDatabase.futureTransactionDao()

        currenciesRepository = MockCurrenciesRepository()
        transactionsRepository =
            OfflineTransactionsRepository(
                transactionDao,
            )

        accountRepository =
            OfflineAccountsRepository(
                accountDao,
                currenciesRepository,
                transactionsRepository,
            )

        futureTransactionsRepository =
            OfflineFutureTransactionsRepository(
                futureTransactionDao,
            )

        categoriesRepository =
            OfflineCategoriesRepository(
                categoryDao,
            )

        offlineBalancesRepository =
            OfflineBalancesRepository(
                accountRepository,
                transactionsRepository,
                futureTransactionsRepository,
            )
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        budgetDatabase.close()
    }

    @Test
    @Throws(Exception::class)
    fun testSimpleTransactions() =
        runBlocking {
            currenciesRepository.getAllCurrenciesStream().first().forEach {
                currencyDao.insert(it)
            }

            accountRepository.insertAccount(
                Account(
                    id = 1,
                    name = "JPMorgan Chase",
                    currency = "USD",
                    initialBalance = 100.0f,
                ),
            )

            assertTrue("Account must be inserted into the repository", accountRepository.getAllAccountsStream().first().size == 1)

            for (category in fakeCategories) {
                categoriesRepository.insert(
                    category,
                )
            }

            assertTrue("Categories must be inserted", categoriesRepository.getAllCategoriesStream().first().size == fakeCategories.size)

            // Start by inserting a transaction that happens only once!!
            futureTransactionsRepository.insert(
                FutureTransaction(
                    id = 0,
                    name = "First budget",
                    type = TransactionType.EXPENSE,
                    categoryId = 1,
                    amount = 10f,
                    currency = "USD",
                    startDate = LocalDateTime.parse("2024-07-01T12:00:00"),
                    endDate = LocalDateTime.parse("2024-07-31T12:00:00"),
                    recurrenceType = RecurrenceType.NONE,
                    recurrenceValue = 0,
                ),
            )

            // We should see a unique pending transaction
            var pendingTransaction =
                offlineBalancesRepository
                    .getPendingTransactions(
                        LocalDateTime.parse("2024-06-30T12:00:00").toLocalDate(),
                        LocalDateTime.parse("2024-07-02T12:00:00").toLocalDate(),
                    ).first()

            assertTrue("One transaction must be returned", pendingTransaction.size == 1)
            assertTrue("Amount should match", pendingTransaction[0].transactionRecord.amount == 10f)

            // Recurrent future transaction
            futureTransactionsRepository.insert(
                FutureTransaction(
                    id = 0, // auto-generate
                    name = "Second budget",
                    type = TransactionType.EXPENSE,
                    categoryId = 1,
                    amount = 20f,
                    currency = "EUR",
                    startDate = LocalDateTime.parse("2024-08-01T12:00:00"),
                    endDate = LocalDateTime.parse("2024-08-31T12:00:00"),
                    recurrenceType = RecurrenceType.WEEKLY,
                    recurrenceValue = 1,
                ),
            )

            pendingTransaction =
                offlineBalancesRepository
                    .getPendingTransactions(
                        LocalDateTime.parse("2024-08-01T12:00:00").toLocalDate(),
                        LocalDateTime.parse("2024-08-31T12:00:00").toLocalDate(),
                    ).first()

            assertTrue("The generated transactions must be returned", pendingTransaction.size == 5)
            for (pend in pendingTransaction) {
                assertTrue("Transaction given currency must match", pend.account.currency.name == "EUR")
                assertTrue("Transactions must have value in given currency", pend.transactionRecord.amount == 20f)
            }

            assertTrue(
                "The generated transactions must be cropped",
                offlineBalancesRepository
                    .getPendingTransactions(
                        LocalDateTime.parse("2024-08-15T12:00:00").toLocalDate(),
                        LocalDateTime.parse("2024-08-31T12:00:00").toLocalDate(),
                    ).first()
                    .size == 3,
            )

            futureTransactionsRepository.insert(
                FutureTransaction(
                    id = 0, // auto-generate
                    name = "Second budget",
                    type = TransactionType.EXPENSE,
                    categoryId = 1,
                    amount = 1.5f,
                    currency = "USD",
                    startDate = LocalDateTime.parse("2024-09-01T12:00:00"),
                    endDate = LocalDateTime.parse("2024-09-07T12:00:00"),
                    recurrenceType = RecurrenceType.DAILY,
                    recurrenceValue = 2,
                ),
            )

            assertTrue(
                "All transactions must be generated",
                offlineBalancesRepository
                    .getPendingTransactions(
                        LocalDateTime.parse("2024-08-31T12:00:00").toLocalDate(),
                        LocalDateTime.parse("2024-09-08T12:00:00").toLocalDate(),
                    ).first()
                    .size == 4,
            )

            assertTrue(
                "All transactions must be generated",
                offlineBalancesRepository
                    .getPendingTransactions(
                        LocalDateTime.parse("2024-09-01T12:00:00").toLocalDate(),
                        LocalDateTime.parse("2024-09-08T12:00:00").toLocalDate(),
                    ).first()
                    .size == 4,
            )

            assertTrue(
                "All transactions must be generated",
                offlineBalancesRepository
                    .getPendingTransactions(
                        LocalDateTime.parse("2024-09-01T12:00:00").toLocalDate(),
                        LocalDateTime.parse("2024-09-07T12:00:00").toLocalDate(),
                    ).first()
                    .size == 4,
            )
        }

    @Test
    @Throws(Exception::class)
    fun testContinuousTransactions() =
        runBlocking {
            currenciesRepository.getAllCurrenciesStream().first().forEach {
                currencyDao.insert(it)
            }

            accountRepository.insertAccount(
                Account(
                    id = 1,
                    name = "JPMorgan Chase",
                    currency = "USD",
                    initialBalance = 100.0f,
                ),
            )

            assertTrue("Account must be inserted into the repository", accountRepository.getAllAccountsStream().first().size == 1)

            for (category in fakeCategories) {
                categoriesRepository.insert(
                    category,
                )
            }

            assertTrue("Categories must be inserted", categoriesRepository.getAllCategoriesStream().first().size == fakeCategories.size)

            futureTransactionsRepository.insert(
                FutureTransaction(
                    id = 0, // auto-generate
                    name = "First budget",
                    type = TransactionType.EXPENSE,
                    categoryId = 1,
                    amount = 20f,
                    currency = "USD",
                    startDate = LocalDateTime.parse("2024-08-01T12:00:00"),
                    endDate = LocalDateTime.parse("2024-08-31T12:00:00"),
                    recurrenceType = RecurrenceType.WEEKLY_CONTINUOUS,
                    recurrenceValue = 1,
                ),
            )

            var pendingTransaction =
                offlineBalancesRepository
                    .getPendingTransactions(
                        LocalDateTime.parse("2024-07-31T12:00:00").toLocalDate(),
                        LocalDateTime.parse("2024-09-01T12:00:00").toLocalDate(),
                    ).first()

            assertTrue("Weekly transactions", pendingTransaction.size == 5)
            val possibleDates =
                hashSetOf(
                    LocalDate.parse("2024-08-31"),
                    LocalDate.parse("2024-08-08"),
                    LocalDate.parse("2024-08-15"),
                    LocalDate.parse("2024-08-22"),
                    LocalDate.parse("2024-08-29"),
                )
            for (pending in pendingTransaction) {
                assertTrue(possibleDates.contains(pending.transactionRecord.date.toLocalDate()))
            }
            assertTrue("Value must be cropped.", pendingTransaction.last().transactionRecord.amount == 20f * 2f / 7f)

            transactionsRepository.insert(
                TransactionRecord(
                    id = 0,
                    name = "Fake transaction",
                    type = TransactionType.EXPENSE,
                    amount = 5f,
                    accountId = 1,
                    categoryId = 1,
                    date = LocalDateTime.parse("2024-08-05T12:00:00"),
                ),
            )

            pendingTransaction =
                offlineBalancesRepository
                    .getPendingTransactions(
                        LocalDateTime.parse("2024-07-31T12:00:00").toLocalDate(),
                        LocalDateTime.parse("2024-09-01T12:00:00").toLocalDate(),
                    ).first()

            // Transactions should still be inserted but the first one should have the value reduced!
            assertTrue("Weekly transactions", pendingTransaction.size == 5)
            assertTrue("Value should be reduced", pendingTransaction[0].transactionRecord.amount == 15f)

            accountRepository.insertAccount(
                Account(
                    id = 2,
                    name = "Deutsche Bank",
                    currency = "EUR",
                    initialBalance = 100.0f,
                ),
            )

            transactionsRepository.insert(
                TransactionRecord(
                    id = 0,
                    name = "Fake transaction",
                    type = TransactionType.EXPENSE,
                    amount = 5f,
                    accountId = 2, // Transaction in EUR
                    categoryId = 1,
                    date = LocalDateTime.parse("2024-08-12T12:00:00"),
                ),
            )

            pendingTransaction =
                offlineBalancesRepository
                    .getPendingTransactions(
                        LocalDateTime.parse("2024-07-31T12:00:00").toLocalDate(),
                        LocalDateTime.parse("2024-09-01T12:00:00").toLocalDate(),
                    ).first()

            assertTrue("Weekly transactions", pendingTransaction.size == 5)
            assertTrue("Value should be reduced", pendingTransaction[0].transactionRecord.amount == 15f)
            assertTrue("Value should be reduced taking into account exchange rate", pendingTransaction[1].transactionRecord.amount == 14.5f)
        }
}
