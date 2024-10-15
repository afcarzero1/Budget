package com.example.budgetahead.use_cases

import androidx.lifecycle.SavedStateHandle
import java.time.YearMonth

// Extension function to put YearMonth
fun SavedStateHandle.setYearMonth(
    key: String,
    value: YearMonth,
) {
    this[key] = value.toString() // Convert YearMonth to String
}

// Extension function to get YearMonth
fun SavedStateHandle.getYearMonth(key: String): YearMonth? {
    val yearMonthStr = this.get<String>(key)
    return yearMonthStr?.let { YearMonth.parse(it) } // Convert String back to YearMonth
}
