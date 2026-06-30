package com.example.jjmfronted.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

val Blue800 = Color(0xFF1565C0)
val Blue700 = Color(0xFF1976D2)
val Blue600 = Color(0xFF1E88E5)
val Blue50 = Color(0xFFE3F2FD)
val Cyan600 = Color(0xFF00ACC1)
val Cyan700 = Color(0xFF0097A7)
val Amber = Color(0xFFFF8F00)
val Green600 = Color(0xFF43A047)
val Red600 = Color(0xFFE53935)
val Gray50 = Color(0xFFFAFAFA)
val Gray100 = Color(0xFFF5F5F5)
val Gray200 = Color(0xFFEEEEEE)
val Gray400 = Color(0xFFBDBDBD)
val Gray600 = Color(0xFF757575)
val Gray900 = Color(0xFF212121)

private val JJMColorScheme = lightColorScheme(
    primary = Blue800,
    onPrimary = Color.White,
    primaryContainer = Blue50,
    onPrimaryContainer = Blue800,
    secondary = Cyan600,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFB2EBF2),
    onSecondaryContainer = Cyan700,
    tertiary = Amber,
    onTertiary = Color.White,
    background = Gray50,
    onBackground = Gray900,
    surface = Color.White,
    onSurface = Gray900,
    surfaceVariant = Gray100,
    onSurfaceVariant = Gray600,
    error = Red600,
    onError = Color.White,
    outline = Gray200
)

private val JJMTypography = Typography(
    headlineLarge = TextStyle(fontSize = 28.sp, fontWeight = FontWeight.Bold),
    headlineMedium = TextStyle(fontSize = 22.sp, fontWeight = FontWeight.Bold),
    titleLarge = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.SemiBold),
    titleMedium = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.SemiBold),
    bodyLarge = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Normal),
    bodyMedium = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Normal),
    labelLarge = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Medium),
    labelSmall = TextStyle(fontSize = 11.sp, fontWeight = FontWeight.Medium)
)

@Composable
fun JJMTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = JJMColorScheme,
        typography = JJMTypography,
        content = content
    )
}
