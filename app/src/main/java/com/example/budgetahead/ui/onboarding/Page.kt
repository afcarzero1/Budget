package com.example.budgetahead.ui.onboarding

import androidx.annotation.DrawableRes
import com.example.budgetahead.R

data class Page(
    val title: String,
    val description: String,
    @DrawableRes val image: Int,
)

val pages = listOf(
    Page(
        title = "Welcome to AheadBudget!",
        description =
        "Manage your personal finances with ease and insight." +
                " Get started to take control of your budget and optimize your spending.",
        image = R.drawable.budget
    ),
    Page(
        title = "Powerful Tools at Your Fingertips",
        description =
        "Monitor your expenses and predict future finances based on your planned incomes and outgoings.",
        image = R.drawable.future_track
    ),
    Page(
        title = "All in one place!",
        description =
        "Easily handle multiple accounts across different currencies." +
                " Use custom categories for precise tracking and stay updated with real-time exchange rates.",
        image = R.drawable.all_in_one_place
    )
)