package com.example.budgetahead.data.accounts

import androidx.room.Embedded
import androidx.room.Relation
import com.example.budgetahead.data.currencies.Currency

data class AccountWithCurrency(
    @Embedded val account: Account,
    @Relation(
        parentColumn = "currency",
        entityColumn = "name"
    )
    val currency: Currency,
)