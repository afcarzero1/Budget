package com.example.budgetahead.ui.components.values

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.budgetahead.R
import com.example.budgetahead.data.currencies.Currency
import com.example.budgetahead.ui.theme.SoftGreen
import com.example.budgetahead.ui.theme.WineRed
import java.time.LocalDateTime
import kotlin.math.absoluteValue

@Composable
fun ValueWithIcon(
    value: Float,
    currency: Currency,
    modifier: Modifier = Modifier,
    textStyle: TextStyle = MaterialTheme.typography.bodyMedium,
) {
    Row(
        modifier = modifier.wrapContentWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceAround,
    ) {
        when {
            value > 0f -> {
                Icon(
                    imageVector = Icons.Filled.KeyboardArrowUp,
                    contentDescription = "income",
                    tint = SoftGreen,
                    modifier = Modifier.padding(start = 8.dp),
                )
            }

            value < 0f -> {
                Icon(
                    imageVector = Icons.Filled.KeyboardArrowDown,
                    contentDescription = "expense",
                    tint = WineRed,
                    modifier = Modifier.padding(start = 8.dp),
                )
            }

            else -> {
                Icon(
                    painter =
                        painterResource(
                            id = R.drawable.equal_24dp_fill0_wght400_grad0_opsz24,
                        ),
                    contentDescription = "neutral",
                    modifier = Modifier.padding(start = 8.dp).size(16.dp),
                )
            }
        }
        Text(
            text = currency.formatAmount(amount = value.absoluteValue),
            style = textStyle,
            modifier = Modifier.padding(end = 8.dp),
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewValueWIthIcon() {
    ValueWithIcon(
        value = 1000f,
        currency = Currency("USD", 1.0f, LocalDateTime.now()),
    )
}

@Preview(showBackground = true)
@Composable
fun PreviewNegValueWIthIcon() {
    ValueWithIcon(
        value = -1000f,
        currency = Currency("USD", 1.0f, LocalDateTime.now()),
    )
}

@Preview(showBackground = true)
@Composable
fun PreviewZeroValueWIthIcon() {
    ValueWithIcon(
        value = -0.0f,
        currency = Currency("USD", 1.0f, LocalDateTime.now()),
    )
}
