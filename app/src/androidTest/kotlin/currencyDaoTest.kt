import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.budgetahead.data.BudgetDatabase
import com.example.budgetahead.data.currencies.Currency
import com.example.budgetahead.data.currencies.CurrencyDao
import java.io.IOException
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class currencyDaoTest {
    private lateinit var currencyDao: CurrencyDao
    private lateinit var budgetDatabase: BudgetDatabase

    private val input = "2023-03-21 12:43:00+00"

    // Define a DateTimeFormatter to match the input format
    private val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ssX")

    // Parse the input string to a LocalDateTime
    private val localDateTime = LocalDateTime.parse(input, formatter)

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
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        budgetDatabase.close()
    }

    private suspend fun addOneItemToDb() {
        currencyDao.insert(currencies[0])
    }

    private suspend fun addAllItemsToDb() {
        for (currency in currencies) {
            currencyDao.insert(currency)
        }
    }

    @Test
    @Throws(Exception::class)
    fun daoGetAllItems_returnsAllItemsFromDB() = runBlocking {
        addAllItemsToDb()
        val allItems = currencyDao.getAllCurrencies().first()

        for (currency in currencies) {
            Assert.assertTrue(allItems.contains(currency))
        }
    }
}
