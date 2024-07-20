package com.example.budgetahead.use_cases

import com.example.budgetahead.data.transactions.FullTransactionRecord
import com.example.budgetahead.data.transfers.TransferWithAccounts
import com.example.budgetahead.ui.transactions.GroupOfTransactionsAndTransfers

class GroupTransactionsAndTransfersByDateUseCase {
    fun execute(
        transactions: List<FullTransactionRecord>,
        transfers: List<TransferWithAccounts>
    ): List<GroupOfTransactionsAndTransfers> = try {
        val transactionsGroupedByDate = transactions.groupBy {
            it.transactionRecord.date.toLocalDate()
        }
        val transfersGroupedByDate = transfers.groupBy { it.transfer.date.toLocalDate() }

        val allDates = (transactionsGroupedByDate.keys union transfersGroupedByDate.keys).sorted().reversed()

        val combinedList = mutableListOf<GroupOfTransactionsAndTransfers>()
        for (date in allDates) {
            val dailyTransactions = transactionsGroupedByDate[date] ?: emptyList()
            val dailyTransfers = transfersGroupedByDate[date] ?: emptyList()
            combinedList.add(
                GroupOfTransactionsAndTransfers(
                    transactions = dailyTransactions,
                    transfers = dailyTransfers,
                    date = date
                )
            )
        }
        combinedList
    } catch (e: IllegalArgumentException) {
        throw e
    }
}
