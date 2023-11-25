package com.example.budgetapplication.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import java.time.YearMonth


@Composable
fun DateRangeSelector(
    startDate: YearMonth,
    endDate: YearMonth,
    onRangeChanged: (startDate: YearMonth, endDate: YearMonth) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        tonalElevation = 1.dp,
        color = MaterialTheme.colorScheme.secondaryContainer,
        modifier = modifier.wrapContentSize(),
        shape = MaterialTheme.shapes.small,
        content = {
            Row(
                modifier = Modifier
                    .height(54.dp)
                    .padding(8.dp)
                    .wrapContentSize(),
                horizontalArrangement = Arrangement.Center
            ) {
                IconButton(
                    onClick = {
                        onRangeChanged(
                            startDate.minusMonths(1), endDate.minusMonths(1)
                        )
                    }

                ) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = "Previous month",
                        tint = Color.Black,
                        modifier = Modifier.size(16.dp)
                    )
                }

                Text(
                    text = "${startDate.monthValue}/${startDate.year}",
                    fontSize = MaterialTheme.typography.bodyLarge.fontSize,
                    fontWeight = MaterialTheme.typography.bodyLarge.fontWeight,
                    modifier = Modifier.padding(
                        bottom = 2.dp,
                        top = 8.dp,
                    )
                )

                Text(
                    text = "-",
                    fontSize = MaterialTheme.typography.bodyLarge.fontSize,
                    fontWeight = MaterialTheme.typography.bodyLarge.fontWeight,
                    modifier = Modifier.padding(8.dp)
                )

                Text(
                    text = "${endDate.monthValue}/${endDate.year}",
                    fontSize = MaterialTheme.typography.bodyLarge.fontSize,
                    fontWeight = MaterialTheme.typography.bodyLarge.fontWeight,
                    modifier = Modifier.padding(
                        bottom = 2.dp,
                        top = 8.dp,
                    )
                )

                IconButton(onClick = {
                    onRangeChanged(
                        startDate.plusMonths(1), endDate.plusMonths(1)
                    )
                }) {
                    Icon(
                        imageVector = Icons.Filled.ArrowForward,
                        contentDescription = "Next month",
                        tint = Color.Black, // You can change the icon color here
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    )


}