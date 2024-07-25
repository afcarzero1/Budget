package com.example.budgetahead.data.future_transactions

enum class TimePeriod {
    DAY,
    WEEK,
    MONTH,
    YEAR,
}

enum class RecurrenceType {
    NONE,
    DAILY,
    WEEKLY,
    WEEKLY_CONTINUOUS,
    MONTHLY,
    MONTHLY_CONTINUOUS,
    YEARLY,
    ;

    fun isContinuous(): Boolean =
        when (this) {
            WEEKLY_CONTINUOUS, MONTHLY_CONTINUOUS -> true
            else -> false
        }

    fun timePeriod(): TimePeriod? =
        when (this) {
            DAILY -> TimePeriod.DAY
            NONE -> null
            WEEKLY -> TimePeriod.WEEK
            WEEKLY_CONTINUOUS -> TimePeriod.WEEK
            MONTHLY -> TimePeriod.MONTH
            MONTHLY_CONTINUOUS -> TimePeriod.MONTH
            YEARLY -> TimePeriod.YEAR
        }
}

object RecurrenceTypeDescriptions {
    val descriptions: Map<RecurrenceType, String> =
        mapOf(
            RecurrenceType.NONE to "None",
            RecurrenceType.DAILY to "Days",
            RecurrenceType.WEEKLY to "Weeks",
            RecurrenceType.MONTHLY to "Months",
            RecurrenceType.YEARLY to "Years",
            RecurrenceType.WEEKLY_CONTINUOUS to "Weeks",
            RecurrenceType.MONTHLY_CONTINUOUS to "Months",
        )
}
