package com.todoapplication.view

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

@Immutable
data class CustomColorsPalette(
    val colorPrimary: Color = Color.Unspecified,
    val colorOnPrimary: Color = Color.Unspecified,
    val colorSecondary: Color = Color.Unspecified,
    val colorOnSecondary: Color = Color.Unspecified,
    val colorTertiary: Color = Color.Unspecified,
    val colorOnTertiary: Color = Color.Unspecified,
    val colorOutline: Color = Color.Unspecified,
    val colorAccent: Color = Color.Unspecified,
    val blueLight: Color = Color.Unspecified,
    val gray: Color = Color.Unspecified,
    val grayLight: Color = Color.Unspecified,
    val green: Color = Color.Unspecified,
    val red: Color = Color.Unspecified,
    val redLight: Color = Color.Unspecified
)

val lightCustomPalette = CustomColorsPalette(
    colorPrimary = lightPrimary,
    colorOnPrimary = lightOnPrimary,
    colorSecondary = lightSecondary,
    colorOnSecondary = lightOnSecondary,
    colorTertiary = lightTertiary,
    colorOnTertiary = lightOnTertiary,
    colorOutline = lightOutline,
    colorAccent = lightAccent,
    blueLight = lightBlueLight,
    gray = lightGray,
    grayLight = lightGrayLight,
    green = lightGreen,
    red = lightRed,
    redLight = lightRedLight
)

val darkCustomPalette = CustomColorsPalette(
    colorPrimary = darkPrimary,
    colorOnPrimary = darkOnPrimary,
    colorSecondary = darkSecondary,
    colorOnSecondary = darkOnSecondary,
    colorTertiary = darkTertiary,
    colorOnTertiary = darkOnTertiary,
    colorOutline = darkOutline,
    colorAccent = darkAccent,
    blueLight = darkBlueLight,
    gray = darkGray,
    grayLight = darkGrayLight,
    green = darkGreen,
    red = darkRed,
    redLight = darkRedLight
)
val localColorsPalette = staticCompositionLocalOf {
    CustomColorsPalette()
}

@Composable
fun AppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val customColorsPalette =
        if (darkTheme) darkCustomPalette
        else lightCustomPalette

    CompositionLocalProvider(
        localColorsPalette provides customColorsPalette
    ) {
        MaterialTheme(
            colors = if (darkTheme) {
                darkColors(
                    primary = darkPrimary,
                    onPrimary = darkOnPrimary,
                    secondary = darkSecondary,
                    onSecondary = darkOnSecondary,
                    error = darkRed
                )
            } else {
                lightColors(
                    primary = lightPrimary,
                    onPrimary = lightOnPrimary,
                    secondary = lightSecondary,
                    onSecondary = lightOnSecondary,
                    error = lightRed
                )
            },
            content = content
        )
    }
}

object ExtendedTheme {
    val colors: CustomColorsPalette
        @Composable
        get() = localColorsPalette.current
}
