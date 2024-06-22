import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.budgetahead.R
import com.example.budgetahead.data.BudgetDatabase
import com.example.budgetahead.data.currencies.CurrenciesApiService
import com.example.budgetahead.data.currencies.CurrenciesResponse
import com.example.budgetahead.data.currencies.CurrencyDao
import com.example.budgetahead.data.currencies.OnlineCurrenciesRepository
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.timeout
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import okhttp3.internal.wait
import org.junit.After
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlin.time.Duration.Companion.seconds


private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")


internal class MockCurrenciesApiService : CurrenciesApiService {
    var servedRequests: Int = 0

    var rates = mapOf(
        "EUR" to "0.8",    // Euro
        "USD" to "1.0",    // US Dollar
        "JPY" to "134.0",  // Japanese Yen
        "GBP" to "0.77",   // British Pound
        "AUD" to "1.43",   // Australian Dollar
        "CAD" to "1.35",   // Canadian Dollar
        "CHF" to "0.92",   // Swiss Franc
        "CNY" to "7.15"    // Chinese Yuan
    )

    override suspend fun getCurrencies(apiKey: String): CurrenciesResponse {
        val now = LocalDateTime.now().atZone(ZoneId.systemDefault()).toOffsetDateTime()
        servedRequests += 1
        return CurrenciesResponse(
            date = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ssX")),
            base = "USD",
            rates = rates
        )
    }
}

@RunWith(AndroidJUnit4::class)
class onlineCurrenciesRepositoryMockTest {


    private lateinit var budgetDatabase: BudgetDatabase
    private lateinit var currencyDao: CurrencyDao

    private lateinit var currenciesApiService: MockCurrenciesApiService
    private lateinit var onlineCurrenciesRepository: OnlineCurrenciesRepository
    private lateinit var apiKey: String

    @Before
    fun setup() {
        val context: Context = ApplicationProvider.getApplicationContext()
        apiKey = context.getString(R.string.CURRENCY_API_KEY)
        // Using an in-memory database because the information stored here disappears when the
        // process is killed.
        budgetDatabase = Room.inMemoryDatabaseBuilder(context, BudgetDatabase::class.java)
            // Allowing main thread queries, just for testing.
            .allowMainThreadQueries()
            .build()
        currencyDao = budgetDatabase.currencyDao()

        currenciesApiService = MockCurrenciesApiService()

        onlineCurrenciesRepository = OnlineCurrenciesRepository(
            context,
            currencyDao,
            currenciesApiService,
            apiKey,
            context.dataStore
        )
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        budgetDatabase.close()
    }


    @OptIn(FlowPreview::class)
    @Test
    fun queryApiCurrencies() {
        val currenciesResponse = runBlocking {
            currenciesApiService.getCurrencies(apiKey)
        }

        assertTrue(currenciesResponse.rates.isNotEmpty())
        assertTrue(currenciesApiService.servedRequests == 1)

        runBlocking {
            val daoCurrencies = currencyDao.getAllCurrencies().first()
            assertTrue(
                "The currencies database must be empty for this test to run",
                daoCurrencies.size == 0
            )
        }


        runBlocking {
            val responses = onlineCurrenciesRepository.getAllCurrenciesStream().timeout(10.seconds)
                .take(currenciesApiService.rates.size + 1).onEach {
                    for (currency in it) {
                        assertTrue(currenciesApiService.rates.containsKey(currency.name))
                    }
                }.toList()
            assertTrue(currenciesApiService.servedRequests == 2)
        }


        runBlocking {
            for (i in 1..10) {
                val responses =
                    onlineCurrenciesRepository.getAllCurrenciesStream().timeout(10.seconds).first()
            }
            delay(5000)

            assertTrue(
                "No Extra requests should be done once updated!",
                currenciesApiService.servedRequests == 2
            )
        }
    }
}