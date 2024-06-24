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
        title = "Lorem Ipsum is simply dummy",
        description = "Lorem Ipsum is simply dummy text of the printing and typesetting industry.",
        image = R.drawable.budget
    ),
    Page(
        title = "Lorem Ipsum is simply dummy",
        description = "Lorem Ipsum is simply dummy text of the printing and typesetting industry.",
        image = R.drawable.budget
    ),
    Page(
        title = "Lorem Ipsum is simply dummy",
        description = "Lorem Ipsum is simply dummy text of the printing and typesetting industry.",
        image = R.drawable.budget
    )
)