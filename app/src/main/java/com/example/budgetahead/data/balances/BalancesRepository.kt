package com.example.budgetahead.data.balances

import com.example.budgetahead.data.categories.Category
import com.example.budgetahead.data.transactions.FullTransactionRecord
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import java.time.YearMonth

/**
 * A repository for retrieving balance data for different timeframes.
 */
interface BalancesRepository {
    /**
     * Retrieves a stream of current balances organized by month and category.
     *
     * A balance is how much money (in the base currency) has been spent during that time period
     * on a certain category.
     *
     * It makes use only of the really executed transactions and does not use the planned
     * transactions.
     * @return A Flow emitting a map of YearMonth to a map of Category to balance amount.
     */
    fun getCurrentBalancesByMonthStream(
        fromDate: YearMonth,
        toDate: YearMonth,
    ): Flow<Map<YearMonth, Map<Category, Float>>>

    /**
     * Retrieves a stream of expected balances organized by month and category.
     *
     * A balance is how much money (in base currency) is/was expected to be spent during that time
     * period.
     *
     * @return A Flow emitting a map of YearMonth to a map of Category to balance amount.
     */
    fun getExpectedBalancesByMonthStream(
        fromDate: YearMonth,
        toDate: YearMonth,
        onlyUpcoming: Boolean = false,
    ): Flow<Map<YearMonth, Map<Category, Float>>>

    /**
     * Retrieves a stream of the actual balance for past dates (including current) and the expected balance
     * for future dates
     */
    fun getBalanceByDay(
        fromDate: LocalDate,
        toDate: LocalDate,
        realityDate: LocalDate = LocalDate.now(),
    ): Flow<Map<LocalDate, Float>>

    /**
     * Obtain the transactions that the user planned
     */
    fun getExpectedTransactions(
        fromDate: LocalDate,
        toDate: LocalDate,
    ): Flow<List<FullTransactionRecord>>

    /**
     * Obtain the transactions that the user planned for and have not yet been executed.
     * This method should have into consideration that some of the planned transactions will have
     * partial or null values since they were already executed after having been planned.
     */
    fun getPendingTransactions(
        fromDate: LocalDate,
        toDate: LocalDate,
    ): Flow<List<FullTransactionRecord>>
}
