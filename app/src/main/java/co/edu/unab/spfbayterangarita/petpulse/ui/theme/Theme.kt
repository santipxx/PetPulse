package co.edu.unab.spfbayterangarita.petpulse.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = PetGreen,
    secondary = PetSoftGreen,
    tertiary = PetOrange,
    background = PetDarkGreen,
    surface = PetCard,
    onPrimary = PetCream,
    onSecondary = PetTextDark,
    onTertiary = PetTextDark,
    onBackground = PetCream,
    onSurface = PetTextDark
)

private val LightColorScheme = lightColorScheme(
    primary = PetGreen,
    secondary = PetSoftGreen,
    tertiary = PetOrange,
    background = PetCream,
    surface = PetCard,
    onPrimary = PetCream,
    onSecondary = PetTextDark,
    onTertiary = PetTextDark,
    onBackground = PetTextDark,
    onSurface = PetTextDark
)

@Composable
fun PetPulseTheme(
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
        typography = Typography,
        content = content
    )
}