package com.example.budgetahead.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.unit.dp
import com.example.budgetahead.data.categories.Category
import com.example.budgetahead.data.currencies.Currency
import kotlin.math.absoluteValue

@Composable
fun BudgetSummary(
    expenses: Map<Category, Float>,
    expectedExpenses: Map<Category, Float>,
    baseCurrency: Currency
) {
    val expectedExpensesList = expectedExpenses.entries.toList()

    Column {
        expectedExpensesList.forEach { (category, expectedAmount) ->
            val actualAmount = expenses[category] ?: 0f
            BudgetBar(
                categoryName = category.name,
                categoryAmount = actualAmount.absoluteValue,
                categoryLimit = expectedAmount.absoluteValue,
                baseCurrency = baseCurrency,
                modifier = Modifier.padding(8.dp)
            )
        }
    }
}


@Composable
fun BudgetBar(
    categoryName: String,
    categoryAmount: Float,
    categoryLimit: Float,
    baseCurrency: Currency,
    modifier: Modifier = Modifier
) {
    // Assume Currency.formatAmount() exists and returns a formatted string
    val formattedAmount = baseCurrency.formatAmount(categoryAmount)
    val formattedLimit = baseCurrency.formatAmount(categoryLimit)

    // Calculate the progress as a fraction
    val progress = (categoryAmount / categoryLimit).coerceIn(0f, 1f)

    Column(modifier = modifier) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = categoryName, style = MaterialTheme.typography.titleSmall)
            Text(
                text = "$formattedAmount / $formattedLimit",
                style = MaterialTheme.typography.titleSmall
            )
        }

        LinearProgressIndicator(
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp),
            progress = progress
        )
    }
}


@Composable
fun LinearProgressIndicator(
    modifier: Modifier = Modifier,
    progress: Float,
    startColor: Color = Color(0xFF4CAF50), // Green color for 0% progress
    endColor: Color = Color(0xFFF44336), // Red color for 100% progress
    backgroundColor: Color = Color(0xFFC8E6C9),
    clipShape: Shape = RoundedCornerShape(16.dp)
) {
    // Coerce the progress value to be between 0f and 1f
    val innerProgress = progress.coerceIn(0f, 1f)

    // Calculate the color based on the progress
    val progressColor = lerp(startColor, endColor, innerProgress)

    Box(
        modifier = modifier
            .clip(clipShape)
            .background(backgroundColor)
            .height(8.dp)
    ) {
        Box(
            modifier = Modifier
                .background(progressColor)
                .fillMaxHeight()
                .fillMaxWidth(innerProgress)
        )
    }
}