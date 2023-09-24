package com.example.budgetapplication.data.balances

import com.example.budgetapplication.data.accounts.Account
import com.example.budgetapplication.data.categories.Category
import com.example.budgetapplication.data.future_transactions.FutureTransactionsRepository
import com.example.budgetapplication.data.future_transactions.RecurrenceType
import com.example.budgetapplication.data.transactions.FullTransactionRecord
import com.example.budgetapplication.data.transactions.TransactionRecord
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
            // Create a map with empty lists for each YearMonth in between and initialize it
            val balancesByMonth = mutableMapOf<YearMonth, MutableMap<Category, Float>>()

            var currentDate = fromDate
            while (currentDate.isBefore(toDate) || currentDate == toDate) {
                balancesByMonth[currentDate] = mutableMapOf()
                currentDate = currentDate.plusMonths(1)
            }

            // Add every transaction to it
            for (transaction in it){
                val transactionYearMonth = YearMonth.from(transaction.transactionRecord.date)
                val transactionCategory = transaction.category

                val monthBalances: MutableMap<Category, Float>? = balancesByMonth[transactionYearMonth]
                val categoryMonthBalance: Float? = monthBalances?.get(transactionCategory)

                if (categoryMonthBalance != null) {
                    monthBalances[transactionCategory] = categoryMonthBalance + transaction.transactionRecord.amount
                }else{
                    monthBalances?.set(transactionCategory, transaction.transactionRecord.amount)
                }
            }

            balancesByMonth
        }
    }

    override fun getExpectedBalancesByMonthStream(
        fromDate: YearMonth,
        toDate: YearMonth
    ): Flow<Map<YearMonth, Map<Category, Float>>> {
        TODO("Not yet implemented")
/*        futureTransactionsRepository.getAllFutureFullTransactionsStream().map {
            val balancesByMonth = mutableMapOf<YearMonth, MutableMap<Category, Float>>()

            var currentDate = fromDate
            while (currentDate.isBefore(toDate) || currentDate == toDate) {
                balancesByMonth[currentDate] = mutableMapOf()
                currentDate = currentDate.plusMonths(1)
            }

            var allExpectedTransactions: List<FullTransactionRecord> = mutableListOf()


            for(futureTransaction in it) {
                val initialDate: LocalDate = LocalDate.from(futureTransaction.futureTransaction.startDate)
                val endDate: LocalDate = LocalDate.from(futureTransaction.futureTransaction.endDate)

                val recurrenceType = futureTransaction.futureTransaction.recurrenceType

                when (recurrenceType) {
                    RecurrenceType.NONE ->
                        if(initialDate.isBefore(toDate.atEndOfMonth()) && initialDate.isAfter(fromDate.atDay(1))) {
                            allExpectedTransactions.plus(
                                FullTransactionRecord(
                                    transactionRecord = TransactionRecord(
                                        id = 0,
                                        date = futureTransaction.futureTransaction.startDate,
                                        amount = futureTransaction.futureTransaction.amount,
                                        name = "",
                                        categoryId = futureTransaction.futureTransaction.categoryId,
                                        accountId = 0,
                                        type = futureTransaction.futureTransaction.type,
                                    ),
                                    category = futureTransaction.category,
                                    account = Account(
                                        id = 0,
                                        name = "",
                                        initialBalance = 0f,
                                        currency = futureTransaction.currency.name,
                                    )
                                )
                            )
                        }
                    RecurrenceType.DAILY -> TODO()
                    RecurrenceType.WEEKLY -> TODO()
                    RecurrenceType.MONTHLY -> TODO()
                    RecurrenceType.YEARLY -> TODO()
                }
            }
        }*/
    }

}