package com.example.budgetahead.ui.overall

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.budgetahead.R

@Composable
fun BudgetSummaryPlaceholder(onLinkClicked: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            painter = painterResource(id = R.drawable.insert_chart_24dp_fill0_wght400_grad0_opsz24),
            contentDescription = "No Budget",
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "No budgets are set yet!",
            style = MaterialTheme.typography.headlineSmall.copy(
                color = MaterialTheme.colorScheme.onSurface.copy(
                    alpha = 0.5f
                )
            )
        )
        Spacer(modifier = Modifier.height(8.dp))
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            val text = "Start planning by adding your planned transactions "
            ClickableText(
                softWrap = true,
                text = buildAnnotatedString {
                    append(text)
                    withStyle(
                        style = SpanStyle(
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.9f),
                            textDecoration = TextDecoration.Underline
                        )
                    ) {
                        append("here")
                    }
                },
                onClick = { offset ->
                    Log.d("CLickableText", offset.toString())
                    if (offset in text.length until "${text}here".length+1) {
                        onLinkClicked()
                    }
                },
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = MaterialTheme.colorScheme.onSurface.copy(
                        alpha = 0.5f
                    )
                )
            )
        }

    }
}


@Preview(showBackground = true)
@Composable
fun PreviewBudgetSummaryPlaceholder() {
    MaterialTheme {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            BudgetSummaryPlaceholder(onLinkClicked = { /* Implement your click action here */ })
        }
    }
}