package com.example.budgetapplication.ui.navigation

/**
 * Interface to describe the navigation destinations of the Budget application
 */
interface  NavigationDestination {

    /**
     * Route string
     */
    val route: String

    /**
     * String resource id that contains the title of the screen
     */
    val titleRes: Int
}