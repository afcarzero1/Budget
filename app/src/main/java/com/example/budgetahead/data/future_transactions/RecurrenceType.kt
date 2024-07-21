package com.example.budgetahead.data.future_transactions

enum class RecurrenceType {
    NONE,
    DAILY,
    WEEKLY,
    WEEKLY_CONTINUOUS,
    MONTHLY,
    MONTHLY_CONTINUOUS,
    YEARLY
}

object RecurrenceTypeDescriptions {
    val descriptions: Map<RecurrenceType, String> = mapOf(
        RecurrenceType.NONE to "None",
        RecurrenceType.DAILY to "Days",
        RecurrenceType.WEEKLY to "Weeks",
        RecurrenceType.MONTHLY to "Months",
        RecurrenceType.YEARLY to "Years",
        RecurrenceType.WEEKLY_CONTINUOUS to "Weeks, cont.",
        RecurrenceType.MONTHLY_CONTINUOUS to "Months, cont."
    )
}
