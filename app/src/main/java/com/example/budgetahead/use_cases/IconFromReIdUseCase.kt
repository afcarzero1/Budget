package com.example.budgetahead.use_cases

import android.content.Context
import com.example.budgetahead.R

class IconFromReIdUseCase(
    val context: Context,
) {
    fun getCategoryIconResId(iconName: String?): Int =
        iconName?.let {
            context.resources.getIdentifier(
                "cat_$it",
                "drawable",
                context.packageName,
            )
        } ?: R.drawable.categories
}
