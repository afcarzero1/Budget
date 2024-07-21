package com.example.budgetahead.ui.components.buttons

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.budgetahead.ui.theme.PurpleGrey80

@Composable
fun BudgetButton(
    text: String,
    onClick: () -> Unit,
) {
    Button(
        onClick = onClick,
        colors =
            ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = Color.White,
            ),
        shape = RoundedCornerShape(size = 6.dp),
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
            color = Color.White,
        )
    }
}

@Composable
fun BudgetTextButton(
    text: String,
    onClick: () -> Unit,
) {
    TextButton(onClick = onClick) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
            color = PurpleGrey80,
        )
    }
}

@Composable
fun BudgetFloatingButton(
    onClick: () -> Unit,
    contentDescription: String?,
) {
    FloatingActionButton(
        onClick = onClick,
        modifier = Modifier.padding(16.dp),
        containerColor = MaterialTheme.colorScheme.primary,
        contentColor = MaterialTheme.colorScheme.onPrimary,
    ) {
        Icon(
            imageVector = Icons.Filled.Add,
            contentDescription = contentDescription,
        )
    }
}
