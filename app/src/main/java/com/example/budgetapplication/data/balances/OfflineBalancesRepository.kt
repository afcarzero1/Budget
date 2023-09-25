package com.example.budgetapplication.data.balances

import com.example.budgetapplication.data.accounts.Account
import com.example.budgetapplication.data.accounts.AccountWithCurrency
import com.example.budgetapplication.data.categories.Category
import com.example.budgetapplication.data.currencies.Currency
import com.example.budgetapplication.data.future_transactions.FutureTransactionsRepository
import com.example.budgetapplication.data.future_transactions.RecurrenceType
import com.example.budgetapplication.data.transactions.FullTransactionRecord
import com.example.budgetapplication.data.transactions.TransactionRecord
import com.example.budgetapplication.data.transactions.TransactionType
import com.example.budgetapplication.data.transactions.TransactionsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.YearMonth

class OfflineBalancesRepository(
    private val transactionsRepository: TransactionsRepository,
    private val futureTransactionsRepository: FutureTransactionsRepository
) : BalancesRepository {
    override fun getCurrentBalancesByMonthStream(
        fromDate: YearMonth,
        toDate: YearMonth
    ): Flow<Map<YearMonth, Map<Category, Float>>> {
        return transactionsRepository.getFullTransactionsByMonthsStream(
            fromDate = fromDate,
            toDate = toDate
        ).map {
            groupTransactions(it, fromDate, toDate)
        }
    }

    override fun getExpectedBalancesByMonthStream(
        fromDate: YearMonth,
        toDate: YearMonth
    ): Flow<Map<YearMonth, Map<Category, Float>>> {
        return futureTransactionsRepository.getAllFutureFullTransactionsStream().map {
            val balancesByMonth = mutableMapOf<YearMonth, MutableMap<Category, Float>>()

            var currentDate = fromDate
            while (currentDate.isBefore(toDate) || currentDate == toDate) {
                balancesByMonth[currentDate] = mutableMapOf()
                currentDate = currentDate.plusMonths(1)
            }

            val allExpectedTransactions: List<FullTransactionRecord> = mutableListOf()

            for (futureTransaction in it) {
                val initialDate: LocalDate =
                    LocalDate.from(futureTransaction.futureTransaction.startDate)
                val endDate: LocalDate = LocalDate.from(futureTransaction.futureTransaction.endDate)

                val recurrenceType = futureTransaction.futureTransaction.recurrenceType

                val transactionDates = generateDates(
                    startDate = initialDate,
                    endDate = endDate,
                    recurrenceType = recurrenceType,
                    intervalStart = fromDate.atDay(1),
                    intervalEnd = toDate.atEndOfMonth()
                )

                // Add all the expected transactions
                for(date in transactionDates){
                    allExpectedTransactions.plus(
                        FullTransactionRecord(
                            transactionRecord = TransactionRecord(
                                id = 0,
                                date = LocalDateTime.from(date),
                                amount = futureTransaction.futureTransaction.amount,
                                name = "",
                                categoryId = futureTransaction.futureTransaction.categoryId,
                                accountId = 0,
                                type = futureTransaction.futureTransaction.type,
                            ),
                            category = futureTransaction.category,
                            account = AccountWithCurrency(
                                account = Account(
                                    id = 0,
                                    name = "",
                                    initialBalance = 0f,
                                    currency = futureTransaction.currency.name,
                                    color = 0x000000,
                                ),
                                currency = futureTransaction.currency
                            )
                        )
                    )
                }
            }

            groupTransactions(allExpectedTransactions, fromDate, toDate)
        }
    }

    private fun generateDates(
        startDate: LocalDate,
        endDate: LocalDate,
        recurrenceType: RecurrenceType,
        intervalStart: LocalDate,
        intervalEnd: LocalDate
    ): List<LocalDate> {
        val dates = mutableListOf<LocalDate>()
        var currentDate = startDate

        //TODO: Potential optimization here to avoid starting from the beginning every time

        while (currentDate <= endDate) {
            // Check if currentDate is within the intervalStart and intervalEnd
            if (currentDate in intervalStart..intervalEnd) {
                dates.add(currentDate)
            }

            // Determine the next date based on recurrenceType
            when (recurrenceType) {
                RecurrenceType.NONE -> break
                RecurrenceType.DAILY -> currentDate = currentDate.plusDays(1)
                RecurrenceType.WEEKLY -> currentDate = currentDate.plusWeeks(1)
                RecurrenceType.MONTHLY -> currentDate = currentDate.plusMonths(1)
                RecurrenceType.YEARLY -> currentDate = currentDate.plusYears(1)
            }

            // If the next date exceeds the intervalEnd, break out of the loop
            if (currentDate > intervalEnd) {
                break
            }
        }

        return dates
    }

    private fun generateInterval(
        fromDate: YearMonth,
        toDate: YearMonth
    ): Map<YearMonth, MutableMap<Category, Float>>{
        val balancesByMonth = mutableMapOf<YearMonth, MutableMap<Category, Float>>()
        var currentDate = fromDate
        while (currentDate.isBefore(toDate) || currentDate == toDate) {
            balancesByMonth[currentDate] = mutableMapOf()
            currentDate = currentDate.plusMonths(1)
        }
        return balancesByMonth
    }

    /**
     * Groups the transactions by month and category in the specified interval and returns a map
     * with the balances for each month and category
     * @param transactions The list of transactions to group
     * @param fromDate The start date of the interval
     * @param toDate The end date of the interval
     * @return A map with the balances for each month and category
     */
    private fun groupTransactions(
        transactions: List<FullTransactionRecord>,
        fromDate: YearMonth,
        toDate: YearMonth
    ): Map<YearMonth, Map<Category, Float>>{
        val balancesByMonth = generateInterval(fromDate, toDate)

        for (transaction in transactions){
            val transactionYearMonth = YearMonth.from(transaction.transactionRecord.date)
            val transactionCategory = transaction.category

            val monthBalances: MutableMap<Category, Float>? =
                balancesByMonth[transactionYearMonth]
            val categoryMonthBalance: Float? = monthBalances?.get(transactionCategory)

            // Compute the absolute value of the transaction
            // TODO: Here use the default currency when it will be implemented
            val transactionCurrencyValue = transaction.account.currency.value

            val transactionAbsoluteValue = transaction.transactionRecord.amount / transactionCurrencyValue
            val transactionValue: Float = when (transaction.transactionRecord.type) {
                TransactionType.INCOME -> transactionAbsoluteValue
                TransactionType.EXPENSE -> -transactionAbsoluteValue
            }

            if (categoryMonthBalance != null) {
                monthBalances[transactionCategory] =
                    categoryMonthBalance + transactionValue
            } else {
                monthBalances?.set(transactionCategory, transactionValue)
            }
        }

        return balancesByMonth
    }

}