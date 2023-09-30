package com.example.budgetapplication.data.currencies

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.budgetapplication.data.DateConverter
import java.text.NumberFormat
import java.time.LocalDateTime
import java.util.Locale
import java.util.Currency as JavaCurrency


@Entity(tableName = "currencies")
data class Currency(
    @PrimaryKey
    val name: String,
    val value: Float,
    @TypeConverters(DateConverter::class)
    val updatedTime: LocalDateTime

){
    fun formatAmount(amount: Float, locale: Locale = Locale.getDefault()): String {
        return formatAmountStatic(name, amount, locale)
    }

    companion object {
        @JvmStatic
        fun formatAmountStatic(name: String, amount: Float, locale: Locale = Locale.getDefault()): String {
            return try {
                val currencyInstance = JavaCurrency.getInstance(name)
                val format = NumberFormat.getCurrencyInstance(locale)
                format.currency = currencyInstance
                format.format(amount)
            } catch (e: IllegalArgumentException) {
                // Fallback formatting for unrecognized currencies
                val currencySymbolOrCode = getCurrencySymbolOrCode(name)
                "$currencySymbolOrCode ${"%.2f".format(amount)}"
            }
        }

        private fun getCurrencySymbolOrCode(currencyCode: String): String {
            return try {
                JavaCurrency.getInstance(currencyCode).symbol
            } catch (e: IllegalArgumentException) {
                currencyCode // If symbol is not available, return the code itself
            }
        }
    }
}
