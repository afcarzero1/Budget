package com.example.budgetahead.data.currencies

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.budgetahead.data.DateConverter
import java.text.NumberFormat
import java.time.LocalDateTime
import java.util.Currency as JavaCurrency
import java.util.Locale

@Entity(tableName = "currencies")
data class Currency(
    @PrimaryKey
    val name: String,
    val value: Float,
    @TypeConverters(DateConverter::class)
    val updatedTime: LocalDateTime
) {
    fun formatAmount(amount: Float, locale: Locale = Locale.getDefault()): String =
        formatAmountStatic(name, amount, locale)

    companion object {
        @JvmStatic
        fun formatAmountStatic(
            name: String,
            amount: Float,
            locale: Locale = Locale.getDefault()
        ): String = try {
            val currencyInstance = JavaCurrency.getInstance(name)
            val format = NumberFormat.getCurrencyInstance(locale)
            format.currency = currencyInstance
            format.format(amount)
        } catch (e: IllegalArgumentException) {
            // Fallback formatting for unrecognized currencies
            val currencySymbolOrCode = getCurrencySymbolOrCode(name)
            "$currencySymbolOrCode ${"%.2f".format(amount)}"
        }

        private fun getCurrencySymbolOrCode(currencyCode: String): String = try {
            JavaCurrency.getInstance(currencyCode).symbol
        } catch (e: IllegalArgumentException) {
            currencyCode // If symbol is not available, return the code itself
        }
    }
}
