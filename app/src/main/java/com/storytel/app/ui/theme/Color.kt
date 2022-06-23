package com.storytel.app.ui.theme

import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.ui.graphics.Color

//TODO: Once Designers have given an OK Orange50 should be our new primary color
//private val Orange50 = Color(0xFFFE5C29)

private val Black = Color(0x00000000)
private val Orange70 = Color(0xFFC6461B)
private val Purple30 = Color(0xFF916678)
val Red300 = Color(0xFFEA6D7E)

private val Pink50 = Color(0xEAC8CAFF)
private val Pink70 = Color(0xF2D5F8FF)
private val Lilac30 = Color(0xE6C0E9FF)

val StorytelDarkColors = darkColors(
    primary = Black,
    onPrimary = Color.Black,
    primaryVariant = Orange70,
    secondary = Purple30,
    onSecondary = Color.Black,
    error = Red300,
    onError = Color.Black
)

val StorytelLightColors = lightColors(
    primary = Pink50,
    onPrimary = Color.Black,
    primaryVariant = Pink70,
    secondary = Lilac30,
    onSecondary = Color.Black,
    error = Red300,
    onError = Color.Black,
)