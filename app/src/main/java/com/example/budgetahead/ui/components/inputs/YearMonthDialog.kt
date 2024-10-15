package com.example.budgetahead.ui.components.inputs

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.budgetahead.ui.components.YearMonthSelector
import java.time.YearMonth

@Composable
fun YearMonthDialog(
    isOpen: Boolean,
    currentSelection: YearMonth,
    onClose: (YearMonth) -> Unit,
) {
    var endDate by remember { mutableStateOf(currentSelection) }

    if (isOpen) {
        AlertDialog(
            onDismissRequest = { onClose(currentSelection) },
            title = {
                Text(
                    text = "Select Month",
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                )
            },
            text = {
                YearMonthSelector(
                    date = endDate,
                    onYearMonthChanged = {
                        endDate = it
                    },
                    modifier = Modifier.fillMaxWidth(),
                )
            },
            confirmButton = {
                Button(onClick = {
                    onClose(endDate)
                }) {
                    Text("Close")
                }
            },
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            titleContentColor = MaterialTheme.colorScheme.primary,
        )
    }
}
