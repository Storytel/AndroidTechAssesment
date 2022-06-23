package com.storytel.app.ui

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.storytel.app.ui.home.Home

@Composable
fun StorytelJobApp() {
    NavHost(
        navController = rememberNavController(),
        startDestination = Screen.Home.route
    ) {
        composable(Screen.Home.route) {
            Home()
        }
    }
}
