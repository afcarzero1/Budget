package com.example.budgetahead.data.balances

import com.example.budgetahead.data.accounts.Account
import com.example.budgetahead.data.accounts.AccountWithCurrency
import com.example.budgetahead.data.accounts.AccountsRepository
import com.example.budgetahead.data.categories.Category
import com.example.budgetahead.data.future_transactions.FullFutureTransaction
import com.example.budgetahead.data.future_transactions.FutureTransactionsRepository
import com.example.budgetahead.data.future_transactions.TimePeriod
import com.example.budgetahead.data.transactions.FullTransactionRecord
import com.example.budgetahead.data.transactions.TransactionRecord
import com.example.budgetahead.data.transactions.TransactionType
import com.example.budgetahead.data.transactions.TransactionsRepository
import com.example.budgetahead.use_cases.ComputeDeltaFromTransactionsUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import java.lang.IllegalStateException
import java.time.LocalDate
import java.time.YearMonth

class OfflineBalancesRepository(
    private val accountsRepository: AccountsRepository,
    private val transactionsRepository: TransactionsRepository,
    private val futureTransactionsRepository: FutureTransactionsRepository,
) : BalancesRepository {
    override fun getCurrentBalancesByMonthStream(
        fromDate: YearMonth,
        toDate: YearMonth,
    ): Flow<Map<YearMonth, Map<Category, Float>>> =
        transactionsRepository
            .getFullTransactionsByMonthsStream(
                fromDate = fromDate,
                toDate = toDate,
            ).map {
                groupTransactionsByMonthAndCategory(it, fromDate, toDate)
            }

    override fun getExpectedBalancesByMonthStream(
        fromDate: YearMonth,
        toDate: YearMonth,
        onlyUpcoming: Boolean,
    ): Flow<Map<YearMonth, Map<Category, Float>>> =
        combine(
            futureTransactionsRepository.getAllFutureFullTransactionsStream(),
            transactionsRepository.getAllFullTransactionsStream()
        ){ futureTransactions, executedTransactions ->
            val allExpectedTransactions: List<FullTransactionRecord> =
                generatePendingTransactions(
                    futureTransactions,
                    executedTransactions,
                    fromDate,
                    toDate,
                ).filter { transaction ->
                    if (onlyUpcoming) {
                        transaction.transactionRecord.date.toLocalDate() > LocalDate.now()
                    } else {
                        true
                    }
                }
            groupTransactionsByMonthAndCategory(allExpectedTransactions, fromDate, toDate)
        }


    override fun getBalanceByDay(
        fromDate: LocalDate,
        toDate: LocalDate,
        realityDate: LocalDate,
    ): Flow<Map<LocalDate, Float>> =
        combine(
            accountsRepository.getAllFullAccountsStream(),
            transactionsRepository.getAllFullTransactionsStream(),
            futureTransactionsRepository.getAllFutureFullTransactionsStream(),
        ) { accounts, transactions, futureTransactions ->
            Triple(accounts, transactions, futureTransactions)
        }.map { (allAccounts, allTransactions, allFutureTransactions) ->

            val balanceByDay: MutableMap<LocalDate, Float> = generateDayInterval(fromDate, toDate)

            // Compute the initial balance at "fromDate" in the
            // base currency
            var initialBalance = 0f
            for (accountInfo in allAccounts) {
                initialBalance += accountInfo.account.computeBalance(
                    accountInfo.transactionRecords.map { it.transactionRecord },
                    fromDate.minusDays(1),
                ) / accountInfo.currency.value
            }

            // Apply all the real transactions until "realityDate" (end of the day)
            val relevantTransactions =
                allTransactions
                    .filter {
                        it.transactionRecord.date.toLocalDate() >= fromDate && it.transactionRecord.date.toLocalDate() <= realityDate
                    }.sortedBy {
                        it.transactionRecord.date.toLocalDate()
                    }

            // Apply all the expected transactions until "toDate" (end of the day)
            val relevantFutureTransactions =
                generatePendingTransactions(
                    allFutureTransactions,
                    allTransactions,
                    realityDate,
                    toDate,
                )

            var currentDate = fromDate

            while (currentDate <= toDate) {
                if (currentDate <= realityDate) {
                    // TODO: Group before and use hash map, this is VERY inefficient
                    val dateTransactions =
                        relevantTransactions.filter {
                            it.transactionRecord.date.toLocalDate() == currentDate
                        }

                    val dateDelta =
                        dateTransactions
                            .sumOf {
                                val value =
                                    when (it.transactionRecord.type) {
                                        TransactionType.INCOME -> it.transactionRecord.amount.toDouble()
                                        TransactionType.EXPENSE -> -it.transactionRecord.amount.toDouble()
                                        TransactionType.EXPENSE_TRANSFER -> 0f.toDouble()
                                        TransactionType.INCOME_TRANSFER -> 0f.toDouble()
                                    }
                                value /
                                    it.account.currency.value
                                        .toDouble()
                            }.toFloat()

                    initialBalance += dateDelta
                }

                if (currentDate > realityDate) {
                    val dateTransactions =
                        relevantFutureTransactions.filter {
                            it.transactionRecord.date.toLocalDate() == currentDate
                        }

                    val dateDelta =
                        dateTransactions
                            .sumOf {
                                val value =
                                    when (it.transactionRecord.type) {
                                        TransactionType.INCOME -> it.transactionRecord.amount.toDouble()
                                        TransactionType.EXPENSE -> -it.transactionRecord.amount.toDouble()
                                        TransactionType.EXPENSE_TRANSFER -> 0F.toDouble()
                                        TransactionType.INCOME_TRANSFER -> 0f.toDouble()
                                    }
                                value /
                                    it.account.currency.value
                                        .toDouble()
                            }.toFloat()

                    initialBalance += dateDelta
                }

                balanceByDay[currentDate] = initialBalance
                currentDate = currentDate.plusDays(1)
            }

            balanceByDay
        }

    override fun getExpectedTransactions(
        fromDate: LocalDate,
        toDate: LocalDate,
    ): Flow<List<FullTransactionRecord>> {
        return combine(
            futureTransactionsRepository.getAllFutureFullTransactionsStream(),
            transactionsRepository.getAllFullTransactionsStream(),
        ) { futureTransactions, executedTransactions ->
            generatePendingTransactions(futureTransactions, executedTransactions, fromDate, toDate)
        }
        // return futureTransactionsRepository.getAllFutureFullTransactionsStream().map {
        //    generateExpectedTransactions(it, fromDate, toDate)
        // }
    }

    override fun getPendingTransactions(
        fromDate: LocalDate,
        toDate: LocalDate,
    ): Flow<List<FullTransactionRecord>> {
        TODO("Not yet implemented")
    }

    private fun generateDates(
        startDate: LocalDate,
        endDate: LocalDate,
        timePeriod: TimePeriod?,
        intervalStart: LocalDate,
        intervalEnd: LocalDate,
    ): List<LocalDate> {
        val dates = mutableListOf<LocalDate>()
        var currentDate = startDate // cannot use max because we need it to be a "multiple" of this day

        // TODO: Potential optimization here to avoid starting from the beginning every time

        while (currentDate <= endDate) {
            // Check if currentDate is within the intervalStart and intervalEnd
            if (currentDate in intervalStart..intervalEnd) {
                dates.add(currentDate)
            }

            currentDate = timePeriod?.let { date: TimePeriod ->
                when (date) {
                    TimePeriod.YEAR -> currentDate.plusYears(1)
                    TimePeriod.DAY -> currentDate.plusDays(1)
                    TimePeriod.WEEK -> currentDate.plusWeeks(1)
                    TimePeriod.MONTH -> currentDate.plusMonths(1)
                }
            } ?: break

            // If the next date exceeds the intervalEnd, break out of the loop
            if (currentDate > intervalEnd) {
                break
            }
        }

        return dates
    }

    private fun generateInterval(
        fromDate: YearMonth,
        toDate: YearMonth,
    ): Map<YearMonth, MutableMap<Category, Float>> {
        val balancesByMonth = mutableMapOf<YearMonth, MutableMap<Category, Float>>()
        var currentDate = fromDate
        while (currentDate.isBefore(toDate) || currentDate == toDate) {
            balancesByMonth[currentDate] = mutableMapOf()
            currentDate = currentDate.plusMonths(1)
        }
        return balancesByMonth
    }

    private fun generateDayInterval(
        fromDate: LocalDate,
        toDate: LocalDate,
    ): MutableMap<LocalDate, Float> {
        val balancesByDay = mutableMapOf<LocalDate, Float>()

        var currentDate = fromDate
        while (!currentDate.isAfter(toDate)) {
            val floatValue = 0.0f

            balancesByDay[currentDate] = floatValue
            currentDate = currentDate.plusDays(1)
        }

        return balancesByDay
    }

    /**
     * Generate all the future expected transactions in the specified interval
     *
     */
    private fun generatePendingTransactions(
        futureTransactions: List<FullFutureTransaction>,
        executedTransactions: List<FullTransactionRecord>,
        fromDate: YearMonth,
        toDate: YearMonth,
    ): List<FullTransactionRecord> =
        generatePendingTransactions(
            futureTransactions,
            executedTransactions,
            fromDate.atDay(1),
            toDate.atEndOfMonth(),
        )

    /**
     * Generate the pending transactions given a list of future transactions and executed transactions.
     */
    private fun generatePendingTransactions(
        futureTransactions: List<FullFutureTransaction>,
        executedTransactions: List<FullTransactionRecord>,
        fromDate: LocalDate,
        toDate: LocalDate,
    ): List<FullTransactionRecord> {
        val pendingTransactions: MutableList<FullTransactionRecord> = mutableListOf()

        // Add the pending transactions coming from continuous kind of future transactions
        pendingTransactions.addAll(
            generatePendingTransactionsContinuous(
                futureTransactions,
                executedTransactions,
                fromDate,
                toDate
            )
        )

        // Handle remaining future transactions
        for (futureTransaction in futureTransactions.filter { !it.futureTransaction.recurrenceType.isContinuous() }) {
            val recurrenceType = futureTransaction.futureTransaction.recurrenceType
                for (date in generateDates(
                    startDate = futureTransaction.futureTransaction.startDate.toLocalDate(),
                    endDate = futureTransaction.futureTransaction.endDate.toLocalDate(),
                    timePeriod = recurrenceType.timePeriod(),
                    intervalStart = fromDate,
                    intervalEnd = toDate,
                )) {
                    pendingTransactions.add(
                        generateTransaction(
                            date,
                            futureTransaction,
                        ),
                    )
                }
        }

        return pendingTransactions
    }

    private fun generatePendingTransactionsContinuous(
        futureTransactions: List<FullFutureTransaction>,
        executedTransactions: List<FullTransactionRecord>,
        fromDate: LocalDate,
        toDate: LocalDate,
    ): List<FullTransactionRecord> {
        val pendingTransactions: MutableList<FullTransactionRecord> = mutableListOf()

        // Only Process Continuous
        // Only Process Transactions that are somehow relevant for the queried period
        futureTransactions
            .filter {
                it.futureTransaction.recurrenceType.isContinuous() &&
                    !areExternal(
                        start1 = fromDate,
                        end1= toDate,
                        start2 = it.futureTransaction.startDate.toLocalDate(),
                        end2 = it.futureTransaction.endDate.toLocalDate(),
                    )
            }.groupBy {
                it.category
            }.forEach { (category, categoryFutureTransactions) ->
                val relevantExecutedTransactions =
                    executedTransactions
                        .filter {
                            it.category == category
                        }.sortedBy {
                            it.transactionRecord.date
                        }
                val alreadyCounted = MutableList(relevantExecutedTransactions.size) { false }

                for (futureTransaction in categoryFutureTransactions) {
                    var currentDate = futureTransaction.futureTransaction.startDate
                    var currentExpectedTransactionIndex = 0
                    val timePeriod = futureTransaction.futureTransaction.recurrenceType.timePeriod()

                    while (currentDate <= futureTransaction.futureTransaction.endDate) {
                        val nextDate =
                            timePeriod?.let { date: TimePeriod ->
                                when (date) {
                                    TimePeriod.YEAR -> currentDate.plusYears(1)
                                    TimePeriod.DAY -> currentDate.plusDays(1)
                                    TimePeriod.WEEK -> currentDate.plusWeeks(1)
                                    TimePeriod.MONTH -> currentDate.plusMonths(1)
                                }
                            } ?: throw IllegalStateException("Continuous events cant be single pointed in time")

                        val periodExecutedTransactions: MutableList<FullTransactionRecord> = mutableListOf()

                        // Get those transactions that were executed between currentDate and nextDate
                        while (
                            currentExpectedTransactionIndex < relevantExecutedTransactions.size &&
                            relevantExecutedTransactions[currentExpectedTransactionIndex].transactionRecord.date < nextDate
                        ) {
                            // Add it only if it falls within the examined period AND it was not already counted
                            if (
                                relevantExecutedTransactions[currentExpectedTransactionIndex].transactionRecord.date >= currentDate &&
                                !alreadyCounted[currentExpectedTransactionIndex]
                            ) {
                                periodExecutedTransactions.add(relevantExecutedTransactions[currentExpectedTransactionIndex])
                                alreadyCounted[currentExpectedTransactionIndex] = true
                            }

                            // Advance
                            currentExpectedTransactionIndex += 1
                        }

                        // Sum how much those transactions are worth! IN BASE CURRENCY
                        val totalExecuted =
                            ComputeDeltaFromTransactionsUseCase().computeDelta(
                                periodExecutedTransactions,
                            )

                        // Convert that value to the transaction currency
                        val totalExecutedInCurrency =
                            ComputeDeltaFromTransactionsUseCase().fromBaseCurrency(
                                totalExecuted,
                                futureTransaction.currency,
                            )

                        // Subtract the value from the expected transaction for this period.
                        val totalPending = futureTransaction.futureTransaction.amount - totalExecutedInCurrency

                        // Add the "planned" modified transaction at the end of the period
                        pendingTransactions.add(
                            generateTransaction(
                                nextDate.toLocalDate(),
                                futureTransaction,
                                totalPending,
                            ),
                        )

                        // Advance to next period
                        currentDate = nextDate
                    }
                }
            }
        return pendingTransactions
    }

    private fun areExternal(
        start1: LocalDate,
        end1: LocalDate,
        start2: LocalDate,
        end2: LocalDate,
    ): Boolean = end1.isBefore(start2) || start1.isAfter(end2)

    private fun generateTransaction(
        date: LocalDate,
        futureTransaction: FullFutureTransaction,
        amount: Float? = null,
    ): FullTransactionRecord =
        FullTransactionRecord(
            transactionRecord =
                TransactionRecord(
                    id = 0,
                    date = date.atStartOfDay(),
                    amount = amount ?: futureTransaction.futureTransaction.amount,
                    name = "",
                    categoryId = futureTransaction.futureTransaction.categoryId,
                    accountId = 0,
                    type = futureTransaction.futureTransaction.type,
                ),
            category = futureTransaction.category,
            account =
                AccountWithCurrency(
                    account =
                        Account(
                            id = 0,
                            name = "",
                            initialBalance = 0f,
                            currency = futureTransaction.currency.name,
                            color = 0x000000,
                        ),
                    currency = futureTransaction.currency,
                ),
        )

    /**
     * Groups the transactions by month and category in the specified interval and returns a map
     * with the balances for each month and category
     * @param transactions The list of transactions to group
     * @param fromDate The start date of the interval
     * @param toDate The end date of the interval
     * @return A map with the balances for each month and category
     */
    private fun groupTransactionsByMonthAndCategory(
        transactions: List<FullTransactionRecord>,
        fromDate: YearMonth,
        toDate: YearMonth,
    ): Map<YearMonth, Map<Category, Float>> {
        val balancesByMonth = generateInterval(fromDate, toDate)

        for (transaction in transactions) {
            val transactionYearMonth = YearMonth.from(transaction.transactionRecord.date)
            val transactionCategory = transaction.category

            val monthBalances: MutableMap<Category, Float>? =
                balancesByMonth[transactionYearMonth]
            val categoryMonthBalance: Float? = monthBalances?.get(transactionCategory)

            // Compute the absolute value of the transaction
            // TODO: Add here protection for currencies that are too close to 0 in value.
            val transactionCurrencyValue = transaction.account.currency.value

            val transactionAbsoluteValue =
                transaction.transactionRecord.amount / transactionCurrencyValue

            val transactionValue: Float =
                when (transaction.transactionRecord.type) {
                    TransactionType.INCOME -> transactionAbsoluteValue
                    TransactionType.EXPENSE -> -transactionAbsoluteValue
                    TransactionType.EXPENSE_TRANSFER -> 0f
                    TransactionType.INCOME_TRANSFER -> 0f
                }

            // Only in the case that this is not a transfer we attempt to add it
            transactionCategory?.let {
                if (categoryMonthBalance != null) {
                    monthBalances[transactionCategory] =
                        categoryMonthBalance + transactionValue
                } else {
                    monthBalances?.set(transactionCategory, transactionValue)
                }
            }
        }

        return balancesByMonth
    }
}
