import androidx.compose.runtime.collectAsState
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.budgetapplication.data.accounts.Account
import com.example.budgetapplication.data.balances.MockBalancesRepository
import com.example.budgetapplication.data.categories.Category
import com.example.budgetapplication.ui.overall.OverallViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.junit.runner.RunWith
import java.time.YearMonth

@RunWith(AndroidJUnit4::class)
class OverallViewModelTest {

    private val categories = listOf(
        Category(
            id = 1,
            name = "Food",
            defaultType = "Expense",
            parentCategoryId = null,
        ),
        Category(
            id = 2,
            name = "Transportation",
            defaultType = "Expense",
            parentCategoryId = null,
        ),
        Category(
            id = 3,
            name = "Rent",
            defaultType = "Expense",
            parentCategoryId = null,
        ),
        Category(
            id = 4,
            name = "Salary",
            defaultType = "Income",
            parentCategoryId = null,
        ),
    )


    private val currentBalances = mapOf(
        YearMonth.of(2023, 1) to mapOf(
            categories[0] to -1000f,  // Food (changed to negative)
            categories[1] to -200f,   // Transportation (changed to negative)
            categories[2] to -800f,   // Rent (changed to negative)
            categories[3] to 3000f    // Salary (remains positive)
        ),
        YearMonth.of(2023, 2) to mapOf(
            categories[0] to -900f,   // Food (changed to negative)
            categories[1] to -220f,   // Transportation (changed to negative)
            categories[2] to -800f,   // Rent (changed to negative)
            categories[3] to 3000f    // Salary (remains positive)
        ),
        YearMonth.of(2023, 3) to mapOf(
            categories[0] to -950f,   // Food (changed to negative)
            categories[1] to -210f,   // Transportation (changed to negative)
            categories[2] to -800f,   // Rent (changed to negative)
            categories[3] to 3000f    // Salary (remains positive)
        ),
        YearMonth.of(2023, 4) to mapOf(
            categories[0] to -980f,   // Food (changed to negative)
            categories[1] to -230f,   // Transportation (changed to negative)
            categories[2] to -800f,   // Rent (changed to negative)
            categories[3] to 3000f    // Salary (remains positive)
        ),
        YearMonth.of(2023, 5) to mapOf(
            categories[0] to -1020f,  // Food (changed to negative)
            categories[1] to -240f,   // Transportation (changed to negative)
            categories[2] to -800f,   // Rent (changed to negative)
            categories[3] to 3000f    // Salary (remains positive)
        ),
        YearMonth.of(2023, 6) to mapOf(
            categories[0] to -1050f,  // Food (changed to negative)
            categories[1] to -250f,   // Transportation (changed to negative)
            categories[2] to -800f,   // Rent (changed to negative)
            categories[3] to 3000f    // Salary (remains positive)
        ),
        YearMonth.of(2023, 7) to mapOf(
            categories[0] to -1100f,  // Food (changed to negative)
            categories[1] to -260f,   // Transportation (changed to negative)
            categories[2] to -800f,   // Rent (changed to negative)
            categories[3] to 3000f    // Salary (remains positive)
        )
    )

    private val expectedBalances = mapOf(
        YearMonth.of(2023, 1) to mapOf(
            categories[0] to -950f,   // Food (changed to negative)
            categories[1] to -210f,   // Transportation (changed to negative)
            categories[2] to -800f,   // Rent (changed to negative)
            categories[3] to 3200f    // Salary (changed to positive)
        ),
        YearMonth.of(2023, 2) to mapOf(
            categories[0] to -900f,   // Food (changed to negative)
            categories[1] to -220f,   // Transportation (changed to negative)
            categories[2] to -800f,   // Rent (changed to negative)
            categories[3] to 3200f    // Salary (changed to positive)
        ),
        YearMonth.of(2023, 3) to mapOf(
            categories[0] to -850f,   // Food (changed to negative)
            categories[1] to -230f,   // Transportation (changed to negative)
            categories[2] to -800f,   // Rent (changed to negative)
            categories[3] to 3200f    // Salary (changed to positive)
        ),
        YearMonth.of(2023, 4) to mapOf(
            categories[0] to -800f,   // Food (changed to negative)
            categories[1] to -240f,   // Transportation (changed to negative)
            categories[2] to -800f,   // Rent (changed to negative)
            categories[3] to 3200f    // Salary (changed to positive)
        ),
        YearMonth.of(2023, 5) to mapOf(
            categories[0] to -850f,   // Food (changed to negative)
            categories[1] to -250f,   // Transportation (changed to negative)
            categories[2] to -800f,   // Rent (changed to negative)
            categories[3] to 3200f    // Salary (changed to positive)
        ),
        YearMonth.of(2023, 6) to mapOf(
            categories[0] to -900f,   // Food (changed to negative)
            categories[1] to -260f,   // Transportation (changed to negative)
            categories[2] to -800f,   // Rent (changed to negative)
            categories[3] to 3200f    // Salary (changed to positive)
        ),
        YearMonth.of(2023, 7) to mapOf(
            categories[0] to -950f,   // Food (changed to negative)
            categories[1] to -270f,   // Transportation (changed to negative)
            categories[2] to -800f,   // Rent (changed to negative)
            categories[3] to 3200f    // Salary (changed to positive)
        ),
        YearMonth.of(2023, 8) to mapOf(
            categories[0] to -1000f,  // Food (changed to negative)
            categories[1] to -280f,   // Transportation (changed to negative)
            categories[2] to -800f,   // Rent (changed to negative)
            categories[3] to 3200f    // Salary (changed to positive)
        ),
        YearMonth.of(2023, 9) to mapOf(
            categories[0] to -1050f,  // Food (changed to negative)
            categories[1] to -290f,   // Transportation (changed to negative)
            categories[2] to -800f,   // Rent (changed to negative)
            categories[3] to 3200f    // Salary (changed to positive)
        )
    )

    private val balancesRepository: MockBalancesRepository = MockBalancesRepository(
        currentBalances,
        expectedBalances
    )

    private val accounts = listOf(
        Account(
            id = 1,
            name = "JPMorgan Chase",
            currency = "USD",
            initialBalance = 100.0f,
        ),
        Account(
            id = 2,
            name = "Deutsche Bank",
            currency = "EUR",
            initialBalance = 200.0f,
        ),
        Account(
            id = 3,
            name = "SEB",
            currency = "SEK",
            initialBalance = 30000f,
        )
    )



    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun testChangingDates() = runTest {


    }
}