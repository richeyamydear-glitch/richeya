package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

private val LightColorScheme = lightColorScheme(
    primary = MangoOrange,
    onPrimary = Color.White,
    primaryContainer = MangoSoftContainer,
    onPrimaryContainer = MangoOnContainer,
    secondary = WatermelonCoral,
    onSecondary = Color.White,
    secondaryContainer = WatermelonSoftContainer,
    onSecondaryContainer = WatermelonOnContainer,
    tertiary = MintLime,
    onTertiary = Color.White,
    tertiaryContainer = MintSoftContainer,
    onTertiaryContainer = MintOnContainer,
    background = PapayaCream,
    onBackground = EspressoInk,
    surface = CardWhite,
    onSurface = EspressoInk,
    surfaceVariant = WarmPeachSurface,
    onSurfaceVariant = CocoaMuted,
    outline = WarmOutline
)

private val DarkColorScheme = darkColorScheme(
    primary = DarkMangoPrimary,
    onPrimary = MangoOnContainer,
    primaryContainer = DarkMangoContainer,
    onPrimaryContainer = MangoSoftContainer,
    secondary = DarkCoralSecondary,
    onSecondary = WatermelonOnContainer,
    secondaryContainer = DarkCoralContainer,
    onSecondaryContainer = WatermelonSoftContainer,
    tertiary = DarkMintTertiary,
    onTertiary = MintOnContainer,
    tertiaryContainer = DarkMintContainer,
    onTertiaryContainer = MintSoftContainer,
    background = DarkCocoaBg,
    onBackground = DarkCreamText,
    surface = DarkCocoaSurface,
    onSurface = DarkCreamText,
    surfaceVariant = DarkWarmOutline,
    onSurfaceVariant = DarkMutedText,
    outline = DarkWarmOutline
)

val SepotongBuahShapes = Shapes(
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(18.dp),
    large = RoundedCornerShape(24.dp),
    extraLarge = RoundedCornerShape(30.dp)
)

@Composable
fun SepotongBuahTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = SepotongBuahTypography,
        shapes = SepotongBuahShapes,
        content = content
    )
}
