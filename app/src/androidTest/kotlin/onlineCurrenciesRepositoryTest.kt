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
import com.example.budgetahead.data.currencies.OnlineCurrenciesRepository
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import retrofit2.Retrofit
import java.io.IOException

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

@RunWith(AndroidJUnit4::class)
class onlineCurrenciesRepositoryTest {
    private lateinit var budgetDatabase: BudgetDatabase
    private val currenciesBaseUrl = "https://api.currencyfreaks.com/v2.0/"

    @OptIn(ExperimentalSerializationApi::class)
    private val retrofit: Retrofit =
        Retrofit
            .Builder()
            .addConverterFactory(Json.asConverterFactory("application/json".toMediaType()))
            .baseUrl(currenciesBaseUrl)
            .build()

    private lateinit var currenciesApiService: CurrenciesApiService
    private lateinit var onlineCurrenciesRepository: OnlineCurrenciesRepository
    private lateinit var apiKey: String

    @Before
    fun setup() {
        val context: Context = ApplicationProvider.getApplicationContext()
        apiKey = context.getString(R.string.CURRENCY_API_KEY)
        // Using an in-memory database because the information stored here disappears when the
        // process is killed.
        budgetDatabase =
            Room
                .inMemoryDatabaseBuilder(context, BudgetDatabase::class.java)
                // Allowing main thread queries, just for testing.
                .allowMainThreadQueries()
                .build()

        currenciesApiService = retrofit.create(CurrenciesApiService::class.java)

        onlineCurrenciesRepository =
            OnlineCurrenciesRepository(
                context,
                BudgetDatabase.getDatabase(context).currencyDao(),
                currenciesApiService,
                apiKey,
                context.dataStore,
            )
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        budgetDatabase.close()
    }

    @Test
    fun queryApiCurrencies() {
        val currenciesResponse =
            runBlocking {
                currenciesApiService.getCurrencies(apiKey)
            }

        assert(currenciesResponse.rates.isNotEmpty())
    }
}
