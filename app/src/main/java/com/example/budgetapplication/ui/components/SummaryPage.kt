package com.example.budgetapplication.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun <T> SummaryPage(
    items: List<T>,
    colors: (T) -> Color,
    amounts: (T) -> Float,
    circleLabel: String,
    rows: @Composable (T) -> Unit
) {
    Column(modifier = Modifier.verticalScroll(rememberScrollState())) {

        // Circle chart
        Box(Modifier.padding(16.dp)) {
            val itemProportion = items.extractProportions { amounts(it) }
            val total: Float = items.sumOf { amounts(it).toDouble() }.toFloat()
            val circleColors = items.map { colors(it) }
            AnimatedCircle(
                itemProportion,
                circleColors,
                Modifier
                    .height(300.dp)
                    .align(Alignment.Center)
                    .fillMaxWidth()
            )
            Column(modifier = Modifier.align(Alignment.Center)) {
                Text(
                    text = circleLabel,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
                Text(
                    text = formatAmount(total),
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }

            Spacer(Modifier.height(10.dp))
            // List of items
            Card{
                Column(modifier = Modifier.padding(16.dp)) {
                    items.forEach { item ->
                        rows(item)
                    }
                }
            }
        }
    }
}

/**
 * Extracts the proportions of each item in a list.
 * @param selector a function that returns the proportion of an item as a Float.
 * @return a list of Floats that represent the proportion of each item in the original list.
 */
fun <E> List<E>.extractProportions(selector: (E) -> Float): List<Float> {
    val total = this.sumOf { selector(it).toDouble() }
    return this.map { (selector(it) / total).toFloat() }
}