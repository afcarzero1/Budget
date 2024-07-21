package com.example.budgetahead.ui.components.dialogs

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.example.budgetahead.R

@Composable
fun ConfirmationDeletionDialog(
    onDeleteConfirm: () -> Unit,
    onDeleteCancel: () -> Unit,
    modifier: Modifier = Modifier,
    title: String = stringResource(R.string.attention),
    message: String = stringResource(R.string.delete_confirmation)
) {
    AlertDialog(
        onDismissRequest = { /* Prevent dialog from dismissing on back press or outside touch */ },
        title = { Text(text = title, color = MaterialTheme.colorScheme.error) },
        text = { Text(text = message, color = MaterialTheme.colorScheme.onErrorContainer) },
        modifier = modifier,
        dismissButton = {
            TextButton(onClick = onDeleteCancel) {
                Text(
                    stringResource(R.string.no),
                    color = MaterialTheme.colorScheme.onErrorContainer
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onDeleteConfirm) {
                Text(
                    stringResource(R.string.yes),
                    color = MaterialTheme.colorScheme.onErrorContainer
                )
            }
        },
        containerColor = MaterialTheme.colorScheme.errorContainer
    )
}

@Preview(showBackground = true)
@Composable
fun PreviewConfirmationDeletionDialog() {
    MaterialTheme {
        ConfirmationDeletionDialog(onDeleteConfirm = {}, onDeleteCancel = {})
    }
}
