package com.todoapplication.view

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable

@Composable
fun AppTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colors = if (isSystemInDarkTheme()) {
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
        content = content,
    )
