package com.example.budgetahead.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog

@Composable
fun <T> LargeDropdownMenu(
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    label: String,
    notSetLabel: String? = null,
    items: List<T>,
    onItemSelected: (index: Int, item: T) -> Unit,
    selectedItemToString: (T) -> String = { it.toString() },
    colors: TextFieldColors = OutlinedTextFieldDefaults.colors(),
    drawItem: @Composable (
        T,
        Boolean,
        Boolean,
        () -> Unit
    ) -> Unit = { item, selected, itemEnabled, onClick ->
        DefaultDrawItem(item, selected, itemEnabled, onClick, selectedItemToString, leadingIcon)
    },
    leadingIcon: (@Composable (T) -> Unit)? = null,
    initialIndex: Int = -1
) {
    var expanded by remember { mutableStateOf(false) }
    var selectedIndex by remember { mutableIntStateOf(-1) }

    // Case in which the initial index is out of bounds (probably items has not received data yet)
    if (initialIndex > items.size) {
        selectedIndex = -1
    }
    val uiIndex = if (selectedIndex == -1) initialIndex else selectedIndex

    Box(modifier = modifier.height(IntrinsicSize.Min)) {
        OutlinedTextField(
            label = { Text(label) },
            value = items.getOrNull(uiIndex)?.let { selectedItemToString(it) } ?: "",
            enabled = enabled,
            modifier = Modifier.fillMaxWidth(),
            trailingIcon = {
                val icon =
                    if (expanded) Icons.Filled.KeyboardArrowUp else Icons.Filled.ArrowDropDown
                Icon(icon, "")
            },
            leadingIcon =
            items.getOrNull(uiIndex)?.let { item ->
                leadingIcon?.let { { leadingIcon(item) } }
            },
            onValueChange = { },
            readOnly = true,
            colors = colors
        )

        // Transparent clickable surface on top of OutlinedTextField
        Surface(
            modifier =
            Modifier
                .fillMaxSize()
                .padding(top = 8.dp)
                .clip(MaterialTheme.shapes.extraSmall)
                .clickable(enabled = enabled) { expanded = true },
            color = Color.Transparent
        ) {}
    }

    if (expanded) {
        Dialog(
            onDismissRequest = { expanded = false }
        ) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.padding(all = 16.dp)
            ) {
                val listState = rememberLazyListState()
                if (uiIndex > -1) {
                    LaunchedEffect("ScrollToSelected") {
                        listState.scrollToItem(index = uiIndex)
                    }
                }

                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    state = listState
                ) {
                    if (notSetLabel != null) {
                        item {
                            LargeDropdownMenuItem(
                                text = notSetLabel,
                                selected = false,
                                enabled = false,
                                onClick = { }
                            )
                        }
                    }
                    itemsIndexed(items) { index, item ->
                        val selectedItem: Boolean = index == uiIndex
                        drawItem(
                            item,
                            selectedItem,
                            true
                        ) {
                            selectedIndex = index
                            onItemSelected(index, item)
                            expanded = false
                        }

                        if (index < items.lastIndex) {
                            Divider(modifier = Modifier.padding(horizontal = 16.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun <T> DefaultDrawItem(
    item: T,
    selected: Boolean,
    enabled: Boolean,
    onClick: () -> Unit,
    selectedItemToString: (T) -> String,
    leadingIcon: (@Composable (T) -> Unit)? = null
) {
    LargeDropdownMenuItem(
        text = selectedItemToString(item),
        selected = selected,
        enabled = enabled,
        onClick = onClick,
        leadingIcon = leadingIcon?.let { { leadingIcon(item) } }
    )
}

@Composable
fun LargeDropdownMenuItem(
    text: String,
    selected: Boolean,
    enabled: Boolean,
    onClick: () -> Unit,
    leadingIcon: (@Composable () -> Unit)? = null
) {
    val contentColor =
        when {
            !enabled -> MaterialTheme.colorScheme.onSurface.copy(alpha = ALPHA_DISABLED)
            selected -> MaterialTheme.colorScheme.primary.copy(alpha = ALPHA_FULL)
            else -> MaterialTheme.colorScheme.onSurface.copy(alpha = ALPHA_FULL)
        }

    CompositionLocalProvider(LocalContentColor provides contentColor) {
        Box(
            modifier =
            Modifier
                .clickable(enabled) { onClick() }
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row {
                leadingIcon?.let {
                    it()
                    Spacer(modifier = Modifier.width(16.dp)) // Add spacer only if leadingIcon is not null
                }
                Text(
                    text = text,
                    style = MaterialTheme.typography.titleSmall
                )
            }
        }
    }
}

private const val ALPHA_DISABLED = 0.38f
private const val ALPHA_FULL = 1f
