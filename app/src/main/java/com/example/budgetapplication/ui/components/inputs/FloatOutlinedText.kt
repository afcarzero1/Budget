package com.example.budgetapplication.ui.components.inputs

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.text.input.KeyboardType


/**
 * A generic composable function to create an outlined text field for editing float values
 * within any record type `T`. The function is type-agnostic and utilizes provided lambdas
 * to interact with the specific fields of the record.
 *
 * @param modifier Modifier to be applied to the text field (e.g., sizing, padding).
 * @param record The data record of type `T` that contains the float value to be edited.
 * @param onValueChange Function that updates the record `T` with a new float value and returns the updated record.
 * @param recordToId Function that extracts a unique identifier from the record `T`, used for state management.
 * @param recordToFloat Function that extracts the float value from the record `T`.
 * @param label Optional composable lambda to provide a custom label for the text field. If not provided,
 *              defaults to a standard "Enter value" label.
 * @param colors Colors used for styling the text field. Defaults to the outlined text field color set.
 */
@Composable
fun <T> FloatOutlinedText(
    modifier: Modifier = Modifier,
    record: T,
    onValueChange: (T, Float) -> Unit, // Function to update record with new float value
    recordToId: (T) -> Int, // Function to get a unique identifier for T
    recordToFloat: (T) -> Float, // Function to get the float value from T
    label: @Composable (() -> Unit)? = null, // Optional label as a composable lambda
    colors: TextFieldColors = OutlinedTextFieldDefaults.colors()
) {
    var text by remember(recordToId(record)) { mutableStateOf(recordToFloat(record).toString()) }
    var supportText by remember { mutableStateOf("") }

    OutlinedTextField(
        value = text,
        onValueChange = {
            text = it
            supportText = try {
                val parsedFloat = it.toFloat()
                onValueChange(record, parsedFloat)
                ""
            } catch (e: NumberFormatException) {
                "Please enter a valid number."
            }
        },
        label = label ?: { Text("Enter value") },
        colors = colors,
        modifier = modifier
            .fillMaxWidth()
            .onFocusChanged { focusState ->
                if (!focusState.isFocused) {
                    try {
                        val parsedFloat = text.toFloat()
                        onValueChange(record, parsedFloat)
                        supportText = ""
                    } catch (e: NumberFormatException) {
                        text = recordToFloat(record).toString()
                        supportText = "Please enter a valid number."
                    }
                }
            },
        enabled = true,
        singleLine = true,
        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
        isError = supportText.isNotEmpty()
    )
}