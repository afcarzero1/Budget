package com.example.budgetapplication.ui.components.graphics

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.budgetapplication.R

fun convertLongToColor(colorLong: Long): Color {
    val alpha = (colorLong shr 24 and 0xFF) / 255.0f
    val red = (colorLong shr 16 and 0xFF) / 255.0f
    val green = (colorLong shr 8 and 0xFF) / 255.0f
    val blue = (colorLong and 0xFF) / 255.0f
    return Color(red, green, blue, alpha)
}

fun convertColorToLong(color: Color): Long {
    val alpha = (color.alpha * 255).toInt() and 0xFF
    val red = (color.red * 255).toInt() and 0xFF
    val green = (color.green * 255).toInt() and 0xFF
    val blue = (color.blue * 255).toInt() and 0xFF
    return (alpha.toLong() shl 24) or (red.toLong() shl 16) or (green.toLong() shl 8) or blue.toLong()
}

object AvailableColors {
    val colorsList = listOf(
        Color(0xFFBB86FC),
        Color(0xFF6200EE),
        Color(0xFF03DAC5),
        Color(0xFF007BFF),
        Color(0xFF5C6BC0),
        Color(0xFFE91E63),
        Color(0xFF9C27B0),
        Color(0xFF2196F3),
        Color(0xFF4CAF50)
    )
}

@Composable
fun ColorPicker(
    modifier: Modifier = Modifier,
    color: Color,
    options: List<Color>,
    onColorChanged: (Color) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = modifier
        .size(35.dp)
        .background(color = color, shape = RoundedCornerShape(10.dp))
        .border(
            border = BorderStroke(1.dp, Color(0x80000000)), shape = RoundedCornerShape(10.dp)
        )
        .clickable { expanded = true })

    if (expanded) {
        Dialog(onDismissRequest = { expanded = false }) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                color = Color.White,
                shape = RoundedCornerShape(8.dp),
                border = BorderStroke(
                    width = 2.dp, color = Color.Gray.copy(alpha = 0.8f)
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        "Select a Color",
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.Black
                    )
                    Spacer(modifier = Modifier.height(16.dp))  // Space between the title and grid
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(3),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp)  // Padding at the bottom
                    ) {
                        items(options.size) { index ->
                            ColorBox(color = options[index], onColorSelected = {
                                onColorChanged(it)
                                expanded = false  // Close dialog when a color is selected
                            })
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun ColorBox(color: Color, onColorSelected: (Color) -> Unit) {
    Surface(
        modifier = Modifier
            .aspectRatio(1f)
            .size(32.dp)
            // Sets the size directly on the Surface
            .clickable { onColorSelected(color) }, // Clickable modifier for color selection
        shape = RoundedCornerShape(10.dp), // Sets the shape of the Surface
        color = color, // Background color of the Surface
        border = BorderStroke(
            1.dp, if (color == Color.Black) Color.Black else Color.Transparent
        ), // Border condition
        tonalElevation = 0.dp // Optional: You can set elevation if needed for a shadow effect
    ) {
        Box(modifier = Modifier.fillMaxSize()) {

        }
    }
}


object AvailableIcons {
    val icons = listOf(
        "bar", "groceries", "rent", "restaurants", "school", "trips"
    )
}

@Composable
fun IconPicker(
    modifier: Modifier = Modifier,
    currentIconName: String, // Current icon name as a string
    iconOptions: List<String>, // List of icon names
    onIconChanged: (String) -> Unit // Passing string to callback instead of Painter
) {
    var expanded by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val currentIconId =
        context.resources.getIdentifier("cat_$currentIconName", "drawable", context.packageName)
    val currentIcon = painterResource(id = currentIconId)

    IconButton(
        onClick = { expanded = true },
        modifier = modifier
            .size(35.dp)
            .background(color = Color.White, shape = RoundedCornerShape(10.dp))
            .border(
                border = BorderStroke(1.dp, Color(0x80000000)),
                shape = RoundedCornerShape(10.dp)
            )
    ) {
        Icon(
            painter = currentIcon,
            contentDescription = currentIconName
        )
    }

    if (expanded) {
        Dialog(onDismissRequest = { expanded = false }) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                color = Color.White,
                shape = RoundedCornerShape(8.dp),
                border = BorderStroke(
                    width = 2.dp, color = Color.Gray.copy(alpha = 0.8f)
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        "Select an Icon",
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.Black
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(3),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp)
                    ) {
                        items(iconOptions) { iconName ->
                            IconBox(iconName = iconName,
                                onIconSelected = {
                                    onIconChanged(it)
                                    expanded = false
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun IconBox(iconName: String, onIconSelected: (String) -> Unit) {
    val context = LocalContext.current
    val resourceId =
        context.resources.getIdentifier("cat_$iconName", "drawable", context.packageName)
    val icon = painterResource(id = resourceId)
    IconButton(onClick = { onIconSelected(iconName) }) {
        Icon(painter = icon, contentDescription = iconName)
    }
}


@Preview(showBackground = true)
@Composable
fun ColorPickerPreview() {
    val sampleColors = listOf(
        Color.Red,
        Color.Green,
        Color.Blue,
        Color.Cyan,
        Color.Magenta,
        Color.Yellow,
        Color.Black,
        Color.Gray,
        Color.White
    )

    // Creating a fake function for handling color changes
    val onColorChanged: (Color) -> Unit = {}

    // Displaying the ColorPicker with the dialog open
    ColorPicker(
        modifier = Modifier, color = Color.Blue, // Current selected color
        options = sampleColors, onColorChanged = onColorChanged
    )
}

