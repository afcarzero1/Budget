package com.example.budgetahead.data.balances

import com.example.budgetahead.data.categories.Category
import com.example.budgetahead.data.transactions.FullTransactionRecord
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import java.time.LocalDate
import java.time.YearMonth

class MockBalancesRepository(
    private val currentBalances: Map<YearMonth, Map<Category, Float>>,
    private val expectedBalances: Map<YearMonth, Map<Category, Float>>,
) : BalancesRepository {
    override fun getExecutedBalancesByMonthStream(
        fromDate: YearMonth,
        toDate: YearMonth,
    ): Flow<Map<YearMonth, Map<Category, Float>>> {
        // Replace this with your logic to filter and return current balances within the specified date range
        val filteredCurrentBalances = currentBalances.filterKeys { it in fromDate..toDate }
        return flowOf(filteredCurrentBalances)
    }

    override fun getExpectedBalancesByMonthStream(
        fromDate: YearMonth,
        toDate: YearMonth,
        realityDate: LocalDate,
    ): Flow<Map<YearMonth, Map<Category, Float>>> {
        // Replace this with your logic to filter and return expected balances within the specified date range
        val filteredExpectedBalances = expectedBalances.filterKeys { it in fromDate..toDate }
        return flowOf(filteredExpectedBalances)
    }

    override fun getBalanceByDay(
        fromDate: LocalDate,
        toDate: LocalDate,
        realityDate: LocalDate,
    ): Flow<Map<LocalDate, Float>> {
        TODO("Not yet implemented")
    }

    override fun getPlannedTransactions(
        fromDate: LocalDate,
        toDate: LocalDate,
    ): Flow<List<FullTransactionRecord>> {
        TODO("Not yet implemented")
    }

    override fun getPendingTransactions(
        fromDate: LocalDate,
        toDate: LocalDate,
    ): Flow<List<FullTransactionRecord>> {
        TODO("Not yet implemented")
    }
}
