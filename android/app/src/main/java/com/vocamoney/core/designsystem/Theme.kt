package com.vocamoney.core.designsystem

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

/** Style tokens from PRD §5. */
object VocaColors {
    val Primary = Color(0xFF137C69)
    val Navy = Color(0xFF112B3C)
    val Mint = Color(0xFFE7F5EF)
    val Accent = Color(0xFFF6BC61)
    val Surface = Color(0xFFF5F7F8)
}

/** 8dp spacing system (PRD §5). */
object Spacing {
    val xs = 4.dp
    val s = 8.dp
    val m = 16.dp
    val l = 24.dp
    val xl = 32.dp
}

private val LightColors = lightColorScheme(
    primary = VocaColors.Primary,
    onPrimary = Color.White,
    primaryContainer = VocaColors.Mint,
    onPrimaryContainer = VocaColors.Navy,
    secondary = VocaColors.Navy,
    onSecondary = Color.White,
    tertiary = VocaColors.Accent,
    onTertiary = VocaColors.Navy,
    background = VocaColors.Surface,
    onBackground = VocaColors.Navy,
    surface = VocaColors.Surface,
    onSurface = VocaColors.Navy,
    surfaceContainer = Color.White,
)

// Card radii 16–20dp (PRD §5).
private val VocaShapes = Shapes(
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(20.dp),
)

@Composable
fun VocaMoneyTheme(content: @Composable () -> Unit) {
    // Dark theme is not in L0; it will be added with the accessibility pass (L10).
    MaterialTheme(colorScheme = LightColors, shapes = VocaShapes, content = content)
}
