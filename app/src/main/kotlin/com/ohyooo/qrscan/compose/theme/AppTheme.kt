package com.ohyooo.qrscan.compose.theme

import androidx.compose.material.Colors
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Typography
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

private val LightPalette = lightColors(
    primary = Color(0xFF0A7E5C),
    primaryVariant = Color(0xFF085D45),
    secondary = Color(0xFF355C7D),
    background = Color(0xFFF3F7FB),
    surface = Color.White,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onBackground = Color(0xFF14213D),
    onSurface = Color(0xFF14213D)
)

private val DarkPalette = darkColors(
    primary = Color(0xFF2DB087),
    primaryVariant = Color(0xFF0A7E5C),
    secondary = Color(0xFF86A8C5),
    background = Color(0xFF0F1720),
    surface = Color(0xFF16212D),
    onPrimary = Color.White,
    onSecondary = Color(0xFF0F1720),
    onBackground = Color(0xFFE6EEF5),
    onSurface = Color(0xFFE6EEF5)
)

private val AppTypography = Typography(
    h4 = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Bold,
        fontSize = 32.sp,
        lineHeight = 36.sp
    ),
    h6 = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.SemiBold,
        fontSize = 20.sp,
        lineHeight = 26.sp
    ),
    subtitle1 = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Medium,
        fontSize = 17.sp,
        lineHeight = 24.sp
    ),
    body1 = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp
    ),
    body2 = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 21.sp
    ),
    caption = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 18.sp
    ),
    button = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.SemiBold,
        fontSize = 14.sp,
        lineHeight = 18.sp
    )
)

@Composable
fun QRScanTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colors = if (darkTheme) DarkPalette else LightPalette,
        typography = AppTypography,
        content = content
    )
}

val Colors.panelBackground: Color
    get() = if (isLight) Color(0xFFF6F8FB) else Color(0xFF1D2935)

val Colors.heroGradientTop: Color
    get() = if (isLight) Color(0xFFE7F5F1) else Color(0xFF123126)

val Colors.heroGradientBottom: Color
    get() = if (isLight) Color(0xFFF3F7FB) else Color(0xFF0F1720)
