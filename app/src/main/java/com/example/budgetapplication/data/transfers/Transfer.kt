package com.example.budgetapplication.data.transfers

import com.example.budgetapplication.data.accounts.Account
import java.time.LocalDateTime

data class Transfer(
    val sourceAccount: Account,
    val destinationAccount: Account,
    val amountSource: Float,
    val amountDestination: Float,
    val date: LocalDateTime
)