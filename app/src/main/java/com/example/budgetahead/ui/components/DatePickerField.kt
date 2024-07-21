package com.example.budgetahead.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.Calendar
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerField(
    date: LocalDateTime,
    label: String,
    onDateChanged: (date: LocalDateTime) -> Unit,
    modifier: Modifier = Modifier,
) {
    // Use for the text the variable that we get from out
    val calendar = Calendar.getInstance()
    calendar.set(Calendar.YEAR, date.year)
    calendar.set(Calendar.MONTH, date.monthValue - 1) // Calendar months are zero-based
    calendar.set(Calendar.DAY_OF_MONTH, date.dayOfMonth)
    calendar.set(Calendar.HOUR_OF_DAY, date.hour)
    calendar.set(Calendar.MINUTE, date.minute)
    calendar.set(Calendar.SECOND, date.second)
    calendar.set(Calendar.MILLISECOND, date.nano / 1_000_000)
    val textFieldDateState = calendar.timeInMillis

    // Whether to show the dialog or not
    var showDatePicker by remember { mutableStateOf(false) }

    val enabled = true
    Box(modifier = modifier.height(IntrinsicSize.Min)) {
        val formatter = SimpleDateFormat("dd MMMM yyyy", LocalConfiguration.current.locales.get(0))
        val formattedDate = formatter.format(Date(textFieldDateState))
        OutlinedTextField(
            label = { Text(label) },
            value = formattedDate,
            enabled = true,
            modifier = Modifier.fillMaxWidth(),
            onValueChange = { },
            readOnly = true,
        )
        Surface(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(top = 8.dp)
                    .clip(MaterialTheme.shapes.extraSmall)
                    .clickable(enabled = enabled) {
                        showDatePicker = true
                    },
            color = Color.Transparent,
        ) {}
    }

    if (showDatePicker) {
        val datePickerState =
            rememberDatePickerState(
                initialSelectedDateMillis = calendar.timeInMillis,
            )
        DatePickerDialog(
            onDismissRequest = {
                showDatePicker = false
            },
            confirmButton = {
                TextButton(onClick = {
                    showDatePicker = false
                    onDateChanged(
                        convertToLocalDateTimeViaInstant(Date(datePickerState.selectedDateMillis!!)),
                    )
                }) {
                    Text(text = "Confirm")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showDatePicker = false
                }) {
                    Text(text = "Cancel")
                }
            },
        ) {
            DatePicker(
                state = datePickerState,
            )
        }
    }
}

fun convertToLocalDateTimeViaInstant(dateToConvert: Date): LocalDateTime =
    dateToConvert
        .toInstant()
        .atZone(ZoneId.systemDefault())
        .toLocalDateTime()
