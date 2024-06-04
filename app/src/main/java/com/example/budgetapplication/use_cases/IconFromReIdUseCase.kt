package com.example.budgetapplication.use_cases

import android.content.Context
import com.example.budgetapplication.R

class IconFromReIdUseCase(val context: Context) {
    fun getCategoryIconResId(iconName: String?): Int{
        return iconName?.let {
            context.resources.getIdentifier(
                "cat_$it",
                "drawable",
                context.packageName
            )
        } ?: R.drawable.categories
    }
}