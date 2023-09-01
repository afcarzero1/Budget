package com.example.budgetapplication.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.budgetapplication.data.transactions.FullTransactionRecord
import java.text.DecimalFormat


@Composable
fun BaseRow(
    color: Color,
    title: String,
    subtitle: String,
    amount: Float,
    currency: String,
    negative: Boolean,
    onItemSelected : (String) -> Unit = {}
) {
    val formattedAmount = formatAmount(amount)
    Row(
        modifier = Modifier
            .height(68.dp)
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        // Color bar in the left side
        VerticalBar(
            color = color,
            modifier = Modifier.width(2.dp)
        )

        Spacer(Modifier.width(12.dp))

        // Title and subtitle
        Column(Modifier) {
            Text(text = title, style = MaterialTheme.typography.headlineSmall)
            if (subtitle.isNotEmpty()){
                Text(text = subtitle, style = MaterialTheme.typography.titleMedium)
            }
        }

        Spacer(Modifier.weight(1f))

        // Amount
        Row(
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = if (negative) "-$currency" else currency,
                style = MaterialTheme.typography.labelLarge,
                modifier = Modifier.align(Alignment.CenterVertically)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = formattedAmount,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.align(Alignment.CenterVertically)
            )
            Spacer(modifier = Modifier.width(4.dp))
            IconButton(onClick = {onItemSelected(title)}) {
                Icon(
                    imageVector = Icons.Default.KeyboardArrowRight,
                    contentDescription = null,
                )
            }
        }
        Spacer(Modifier.width(16.dp))
    }
    ListDivider()
}


@Composable
fun VerticalBar(color: Color, modifier: Modifier = Modifier) {
    Spacer(
        modifier
            .size(4.dp, 36.dp)
            .background(color = color))
}

@Composable
fun ListDivider(modifier: Modifier = Modifier) {
    Divider(color = MaterialTheme.colorScheme.background, thickness = 1.dp, modifier = modifier)
}

fun formatAmount(amount: Float): String {
    return AmountDecimalFormat.format(amount)
}

private val AmountDecimalFormat = DecimalFormat("###,###.##")

@Composable
private fun TransactionRow(
    transaction: FullTransactionRecord,
    color: Color,
){
    BaseRow(
        color = color,
        title = transaction.transactionRecord.name,
        subtitle = transaction.category.name,
        amount = transaction.transactionRecord.amount,
        currency = transaction.account.currency,
        negative = transaction.transactionRecord.type == "Expense"
    )
}
