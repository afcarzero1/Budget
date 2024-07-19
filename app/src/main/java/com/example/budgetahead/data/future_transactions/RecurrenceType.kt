package com.example.budgetahead.data.future_transactions

enum class RecurrenceType {
    NONE,
    DAILY,
    WEEKLY,
    MONTHLY,
    YEARLY
}

object RecurrenceTypeDescriptions {
    val descriptions: Map<RecurrenceType, String> =
        mapOf(
            RecurrenceType.NONE to "None",
            RecurrenceType.DAILY to "Days",
            RecurrenceType.WEEKLY to "Weeks",
            RecurrenceType.MONTHLY to "Months",
            RecurrenceType.YEARLY to "Years"
        )
}
