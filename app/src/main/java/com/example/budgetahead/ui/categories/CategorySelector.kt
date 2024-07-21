package com.example.budgetahead.ui.categories

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.budgetahead.data.categories.Category
import com.example.budgetahead.data.categories.CategoryType
import com.example.budgetahead.use_cases.IconFromReIdUseCase

@Composable
fun CategorySelector(
    category: Category?,
    categoryOptions: List<Category>,
    onCategorySelected: (Category) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    var showDialog by remember { mutableStateOf(false) }
    val iconResourceId =
        IconFromReIdUseCase(LocalContext.current).getCategoryIconResId(
            category?.iconResId
        )
    val icon = painterResource(id = iconResourceId)
    Box(modifier = modifier.height(IntrinsicSize.Min)) {
        OutlinedTextField(
            label = { Text("Category") },
            enabled = enabled,
            value = category?.name ?: "",
            onValueChange = {},
            leadingIcon = {
                Image(
                    painter = icon,
                    contentDescription = "Category Icon",
                    modifier =
                    Modifier
                        .size(25.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f))
                )
            },
            trailingIcon = {
                val icon2 =
                    if (showDialog) Icons.Filled.KeyboardArrowUp else Icons.Filled.ArrowDropDown
                Icon(icon2, "")
            },
            modifier = Modifier.fillMaxWidth(),
            readOnly = true
        )
        Surface(
            modifier =
            Modifier
                .fillMaxSize()
                .padding(top = 8.dp)
                .clip(MaterialTheme.shapes.extraSmall)
                .clickable(enabled = enabled) { showDialog = true },
            color = Color.Transparent
        ) {}
    }

    if (showDialog) {
        Dialog(
            onDismissRequest = { showDialog = false }
        ) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.padding(all = 16.dp)
            ) {
            }
        }
    }
}

@Preview
@Composable
fun CategorySelectorPreview() {
    CategorySelector(
        category =
        Category(
            id = 0,
            name = "School",
            defaultType = CategoryType.Expense,
            parentCategoryId = null,
            iconResId = "cat_school"
        ),
        categoryOptions = listOf(),
        onCategorySelected = {}
    )
}
