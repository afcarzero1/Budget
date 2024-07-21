package com.example.budgetahead.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import java.time.YearMonth
import java.time.format.DateTimeFormatter

@Composable
fun DateRangeSelector(
    startDate: YearMonth,
    endDate: YearMonth,
    onRangeChanged: (startDate: YearMonth, endDate: YearMonth) -> Unit,
    modifier: Modifier = Modifier
) {
    val dateFormatter = DateTimeFormatter.ofPattern("MMM yyyy")
    Row(
        modifier =
        modifier
            .height(54.dp)
            .padding(4.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = {
                onRangeChanged(
                    startDate.minusMonths(1),
                    endDate.minusMonths(1)
                )
            },
            modifier = Modifier.weight(1f)
        ) {
            Icon(
                imageVector = Icons.Filled.ArrowBack,
                contentDescription = "Previous month",
                tint = MaterialTheme.colorScheme.onPrimary, // Changed to primary color
                modifier =
                Modifier
                    .size(24.dp)
                    .background(
                        color = MaterialTheme.colorScheme.primary,
                        shape = CircleShape
                    ).padding(4.dp)
            )
        }
        Row(
            modifier = Modifier.padding(horizontal = 4.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = startDate.format(dateFormatter),
                fontSize = MaterialTheme.typography.bodyLarge.fontSize,
                fontWeight = MaterialTheme.typography.bodyLarge.fontWeight,
                modifier =
                Modifier.padding(
                    bottom = 2.dp,
                    top = 8.dp
                ),
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )

            Text(
                text = "-",
                fontSize = MaterialTheme.typography.bodyLarge.fontSize,
                fontWeight = MaterialTheme.typography.bodyLarge.fontWeight,
                modifier = Modifier.padding(8.dp),
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )

            Text(
                text = endDate.format(dateFormatter),
                fontSize = MaterialTheme.typography.bodyLarge.fontSize,
                fontWeight = MaterialTheme.typography.bodyLarge.fontWeight,
                modifier =
                Modifier.padding(
                    bottom = 2.dp,
                    top = 8.dp
                ),
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
        }

        IconButton(
            onClick = {
                onRangeChanged(
                    startDate.plusMonths(1),
                    endDate.plusMonths(1)
                )
            },
            modifier = Modifier.weight(1f)
        ) {
            Icon(
                imageVector = Icons.Filled.ArrowForward,
                contentDescription = "Next month",
                tint = MaterialTheme.colorScheme.onPrimary, // Changed to primary color
                modifier =
                Modifier
                    .size(24.dp)
                    .background(
                        color = MaterialTheme.colorScheme.primary,
                        shape = CircleShape
                    ).padding(4.dp)
            )
        }
    }
}

@Composable
fun YearMonthSelector(
    date: YearMonth,
    onYearMonthChanged: (date: YearMonth) -> Unit,
    modifier: Modifier = Modifier
) {
    val dateFormatter = DateTimeFormatter.ofPattern("MMM yyyy")
    Row(
        modifier =
        modifier
            .height(54.dp)
            .padding(4.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = {
                onYearMonthChanged(
                    date.minusMonths(1)
                )
            },
            modifier = Modifier.weight(1f)
        ) {
            Icon(
                imageVector = Icons.Filled.ArrowBack,
                contentDescription = "Month",
                tint = MaterialTheme.colorScheme.onPrimary, // Changed to primary color
                modifier =
                Modifier
                    .size(24.dp)
                    .background(
                        color = MaterialTheme.colorScheme.primary,
                        shape = CircleShape
                    ).padding(4.dp)
            )
        }
        Row(
            modifier = Modifier.padding(horizontal = 4.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = date.format(dateFormatter),
                fontSize = MaterialTheme.typography.bodyLarge.fontSize,
                fontWeight = MaterialTheme.typography.bodyLarge.fontWeight,
                modifier =
                Modifier.padding(
                    bottom = 2.dp,
                    top = 8.dp
                ),
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
        }

        IconButton(
            onClick = {
                onYearMonthChanged(
                    date.plusMonths(1)
                )
            },
            modifier = Modifier.weight(1f)
        ) {
            Icon(
                imageVector = Icons.Filled.ArrowForward,
                contentDescription = "Next month",
                tint = MaterialTheme.colorScheme.onPrimary, // Changed to primary color
                modifier =
                Modifier
                    .size(24.dp)
                    .background(
                        color = MaterialTheme.colorScheme.primary,
                        shape = CircleShape
                    ).padding(4.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewDateRangeSelector() {
    DateRangeSelector(
        startDate = YearMonth.now(),
        endDate = YearMonth.now().plusMonths(1),
        onRangeChanged = { _, _ -> /* Do nothing in preview */ },
        modifier = Modifier
    )
}
