package com.example.budgetapplication.data.balances

import com.example.budgetapplication.data.categories.Category
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import java.time.YearMonth

class MockBalancesRepository(
    private val currentBalances:Map<YearMonth, Map<Category, Float>>,
    private val expectedBalances:Map<YearMonth, Map<Category, Float>>
) : BalancesRepository {
    override fun getCurrentBalancesByMonthStream(
        fromDate: YearMonth,
        toDate: YearMonth
    ): Flow<Map<YearMonth, Map<Category, Float>>> {
        // Replace this with your logic to filter and return current balances within the specified date range
        val filteredCurrentBalances = currentBalances.filterKeys { it in fromDate..toDate }
        return flowOf(filteredCurrentBalances)
    }

    override fun getExpectedBalancesByMonthStream(
        fromDate: YearMonth,
        toDate: YearMonth
    ): Flow<Map<YearMonth, Map<Category, Float>>> {
        // Replace this with your logic to filter and return expected balances within the specified date range
        val filteredExpectedBalances = expectedBalances.filterKeys { it in fromDate..toDate }
        return flowOf(filteredExpectedBalances)
    }
}