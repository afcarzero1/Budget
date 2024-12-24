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
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerField(
    date: LocalDateTime,
    label: String,
    onDateChanged: (LocalDateTime) -> Unit,
    modifier: Modifier = Modifier
) {
    var showDatePicker by remember { mutableStateOf(false) }

    // For displaying, use user's local zone
    val zoneId = ZoneId.systemDefault()
    val displayedDate = date.atZone(zoneId)
        .format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))

    // Convert the incoming date to a UTC epoch for DatePicker
    val initialMillis = date.atZone(zoneId).toInstant().toEpochMilli()
    val datePickerState = rememberDatePickerState(initialSelectedDateMillis = initialMillis)

    Box(modifier = modifier.height(IntrinsicSize.Min)) {
        OutlinedTextField(
            label = { Text(label) },
            value = displayedDate,
            onValueChange = {},
            modifier = Modifier.fillMaxWidth(),
            readOnly = true,
        )
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 8.dp)
                .clip(MaterialTheme.shapes.extraSmall)
                .clickable { showDatePicker = true },
            color = Color.Transparent
        ) {}
    }

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    showDatePicker = false
                    val pickedMillis = datePickerState.selectedDateMillis ?: return@TextButton
                    // Interpret selected date as UTC, then fix the date part to user zone
                    val pickedDayUtc = Instant.ofEpochMilli(pickedMillis)
                        .atZone(ZoneOffset.UTC)
                        .toLocalDate()
                    // Attach the original time but keep the new day
                    onDateChanged(pickedDayUtc.atTime(date.toLocalTime()))
                }) {
                    Text("Confirm")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}



fun convertToLocalDateTimeViaInstant(dateToConvert: Date): LocalDateTime =
    dateToConvert
        .toInstant()
        .atZone(ZoneId.systemDefault())
        .toLocalDateTime()
