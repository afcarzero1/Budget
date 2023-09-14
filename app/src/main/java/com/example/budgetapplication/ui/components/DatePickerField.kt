package com.example.budgetapplication.ui.components

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
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.Calendar
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerField(
    label: String,
    onDateChanged: (date: LocalDateTime) -> Unit,
    modifier: Modifier = Modifier,
    initialDate: LocalDateTime = LocalDateTime.now()
) {
    val calendar = Calendar.getInstance()

    calendar.set(Calendar.YEAR, initialDate.year)
    calendar.set(Calendar.MONTH, initialDate.monthValue - 1) // Calendar months are zero-based
    calendar.set(Calendar.DAY_OF_MONTH, initialDate.dayOfMonth)
    calendar.set(Calendar.HOUR_OF_DAY, initialDate.hour)
    calendar.set(Calendar.MINUTE, initialDate.minute)
    calendar.set(Calendar.SECOND, initialDate.second)
    calendar.set(
        Calendar.MILLISECOND,
        initialDate.nano / 1_000_000
    ) // Convert nanoseconds to milliseconds

    var showDatePicker by remember { mutableStateOf(false) }

    // set the initial date
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = calendar.timeInMillis
    )

    var selectedDate by remember {
        mutableLongStateOf(calendar.timeInMillis)
    }

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = {
                showDatePicker = false
            },
            confirmButton = {
                TextButton(onClick = {
                    showDatePicker = false
                    selectedDate = datePickerState.selectedDateMillis!!
                    onDateChanged(convertToLocalDateTimeViaInstant(Date(selectedDate)))
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
            }
        ) {
            DatePicker(
                state = datePickerState
            )
        }
    }
    val enabled = true

    Box(modifier = modifier.height(IntrinsicSize.Min)) {
        val formatter = SimpleDateFormat("dd MMMM yyyy", Locale.ROOT)
        val formattedDate = formatter.format(Date(selectedDate))
        OutlinedTextField(
            label = { Text(label) },
            value = formattedDate,
            enabled = enabled,
            modifier = Modifier.fillMaxWidth(),
            onValueChange = { },
            readOnly = true,
        )
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 8.dp)
                .clip(MaterialTheme.shapes.extraSmall)
                .clickable(enabled = enabled) { showDatePicker = true },
            color = Color.Transparent,
        ) {}
    }

}

fun convertToLocalDateTimeViaInstant(dateToConvert: Date): LocalDateTime {
    return dateToConvert.toInstant()
        .atZone(ZoneId.systemDefault())
        .toLocalDateTime()
}