package com.storytel.app.ui.theme

import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable

@Composable
fun StorytelTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colors = StorytelDarkColors,
        typography = StorytelTypography,
        shapes = StorytelShapes,
        content = content
    )
}