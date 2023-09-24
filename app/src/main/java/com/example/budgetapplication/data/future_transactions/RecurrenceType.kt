package com.example.budgetapplication.data.future_transactions

import androidx.room.TypeConverter

enum class RecurrenceType {
    NONE,
    DAILY,
    WEEKLY,
    MONTHLY,
    YEARLY
}
