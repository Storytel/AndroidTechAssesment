package com.storytel.app.ui.theme

import androidx.compose.material.darkColors
import androidx.compose.ui.graphics.Color

private val Orange50 = Color(0xFFFE5C29)
private val orange70 = Color(0xFFC6461B)
private val Purple30 = Color(0xFF916678)
val Red300 = Color(0xFFEA6D7E)

val StorytelColors = darkColors(
    primary = Orange50,
    onPrimary = Color.Black,
    primaryVariant = orange70,
    secondary = Purple30,
    onSecondary = Color.Black,
    error = Red300,
    onError = Color.Black
)
