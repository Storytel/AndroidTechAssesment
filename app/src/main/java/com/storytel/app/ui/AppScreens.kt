package com.storytel.app.ui

/**
 * List of screens for [StorytelJobApp]
 */
sealed class Screen(val route: String) {
    object Home : Screen("home")
}
