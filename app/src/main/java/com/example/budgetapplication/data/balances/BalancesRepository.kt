package com.example.budgetapplication.data.balances

import com.example.budgetapplication.data.categories.Category
import kotlinx.coroutines.flow.Flow
import java.time.YearMonth

/**
 * A repository for retrieving balance data for different timeframes.
 */
interface BalancesRepository {
    /**
     * Retrieves a stream of current balances organized by month and category.
     * @return A Flow emitting a map of YearMonth to a map of Category to balance amount.
     */
    fun getCurrentBalancesByMonthStream(fromDate: YearMonth, toDate: YearMonth): Flow<Map<YearMonth, Map<Category, Float>>>

    /**
     * Retrieves a stream of expected balances organized by month and category.
     * @return A Flow emitting a map of YearMonth to a map of Category to balance amount.
     */
    fun getExpectedBalancesByMonthStream(fromDate: YearMonth, toDate: YearMonth): Flow<Map<YearMonth, Map<Category, Float>>>
}