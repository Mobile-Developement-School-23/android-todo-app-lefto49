package com.todoapplication.view

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.sp
import com.todoapplication.R

@Immutable
data class ExtendedTypography(
    val large: TextStyle = TextStyle.Default,
    val title: TextStyle = TextStyle.Default,
    val button: TextStyle = TextStyle.Default,
    val body: TextStyle = TextStyle.Default,
    val subhead: TextStyle = TextStyle.Default,
)

val textStyles = ExtendedTypography(
    large = TextStyle(
        fontSize = 32.sp,
        fontFamily = FontFamily(Font(R.font.montserrat)),
        lineHeight = 38.sp
    ),

    body = TextStyle(
        fontFamily = FontFamily(Font(R.font.montserrat)),
        fontSize = 16.sp, lineHeight = 20.sp
    ),

    title = TextStyle(
        fontFamily = FontFamily(Font(R.font.montserrat)),
        fontSize = 20.sp, lineHeight = 32.sp
    ),

    button = TextStyle(
        fontFamily = FontFamily(Font(R.font.montserrat)),
        fontSize = 14.sp, lineHeight = 24.sp
    ),

    subhead = TextStyle(
        fontFamily = FontFamily(Font(R.font.montserrat)),
        fontSize = 14.sp, lineHeight = 20.sp
    )
)

val customExtendedTypography = staticCompositionLocalOf {
    ExtendedTypography()
}