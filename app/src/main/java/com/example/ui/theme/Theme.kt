package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
  primary = MintDark,
  onPrimary = ForestGreen,
  primaryContainer = EmeraldPrimary,
  onPrimaryContainer = MintLight,
  secondary = SageAccent,
  onSecondary = ForestGreen,
  secondaryContainer = DarkSurfaceVariant,
  onSecondaryContainer = MintLight,
  tertiary = BinYellowEWaste,
  background = DarkBackground,
  onBackground = TextCharcoalDark,
  surface = DarkSurface,
  onSurface = TextCharcoalDark,
  surfaceVariant = DarkSurfaceVariant,
  onSurfaceVariant = TextMutedDark,
)

private val LightColorScheme = lightColorScheme(
  primary = SleekGreenPrimary,
  onPrimary = Color.White,
  primaryContainer = SleekPill,
  onPrimaryContainer = SleekGreenPrimary,
  secondary = SageAccent,
  onSecondary = Color.White,
  secondaryContainer = SleekPillLight,
  onSecondaryContainer = SleekGreenPrimary,
  tertiary = BinBlueRecycle,
  background = SleekBackground,
  onBackground = SleekTextPrimary,
  surface = SleekSurface,
  onSurface = SleekTextPrimary,
  surfaceVariant = SleekDivider,
  onSurfaceVariant = SleekTextSecondary,
  outline = SleekCardBorder,
  outlineVariant = SleekDivider,
)

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = false,
  dynamicColor: Boolean = false, // Use our calming custom palette by default
  content: @Composable () -> Unit,
) {
  // Always use LightColorScheme so SleekSurface cards, OutlinedTextFields, and SleekGreenPrimary
  // buttons maintain high-contrast dark text on light backgrounds and crisp white text on green buttons.
  val colorScheme = LightColorScheme

  MaterialTheme(
    colorScheme = colorScheme,
    typography = Typography,
    content = content
  )
}

