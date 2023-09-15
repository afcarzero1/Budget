package com.example.budgetapplication.data.accounts

import androidx.room.Embedded
import androidx.room.Relation
import com.example.budgetapplication.data.currencies.Currency

data class AccountWithCurrency(
    @Embedded val account: Account,
    @Relation(
        parentColumn = "currency", // Assuming you have a currencyId field in your Account table
        entityColumn = "name"
    )
    val currency: Currency
)