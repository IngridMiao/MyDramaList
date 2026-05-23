package front.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import front.app.ui.MainScreen
import front.app.ui.login.LoginScreen
import front.app.ui.theme.AppTheme
import front.app.ui.theme.FontSize
import front.app.ui.theme.ThemeViewModel

// ── 淺色主題色票 ──────────────────────────────────────────
private val LightPrimary         = Color(0xFF185FA5) // 藍色
private val LightOnPrimary       = Color(0xFFFFFFFF)
private val LightPrimaryContainer  = Color(0xFFD1E4FF)
private val LightOnPrimaryContainer= Color(0xFF001D36)

private val LightSecondary       = Color(0xFFBA7517) // 橘色
private val LightOnSecondary     = Color(0xFFFFFFFF)
private val LightSecondaryContainer = Color(0xFFFFDDB1)
private val LightOnSecondaryContainer = Color(0xFF291800)

private val LightTertiary        = Color(0xFF185FA5) // 暫時用 Primary 同色調避免紫色
private val LightOnTertiary      = Color(0xFFFFFFFF)
private val LightTertiaryContainer = Color(0xFFD1E4FF)
private val LightOnTertiaryContainer = Color(0xFF001D36)

private val LightBackground      = Color(0xFFF4F3EF)
private val LightOnBackground    = Color(0xFF1A1A18)
private val LightSurface         = Color(0xFFFFFFFF)
private val LightOnSurface       = Color(0xFF1A1A18)
private val LightSurfaceVariant  = Color(0xFFECEAE4)
private val LightOnSurfaceVariant= Color(0xFF5F5E5A)
 
// ── 深色主題色票 ──────────────────────────────────────────
private val DarkPrimary          = Color(0xFF85B7EB)
private val DarkOnPrimary        = Color(0xFF042C53)
private val DarkPrimaryContainer = Color(0xFF004975)
private val DarkOnPrimaryContainer= Color(0xFFD1E4FF)

private val DarkSecondary        = Color(0xFFEF9F27)
private val DarkOnSecondary      = Color(0xFF412402)
private val DarkSecondaryContainer = Color(0xFF5A3B00)
private val DarkOnSecondaryContainer = Color(0xFFFFDDB1)

private val DarkTertiary         = Color(0xFF85B7EB)
private val DarkOnTertiary       = Color(0xFF042C53)
private val DarkTertiaryContainer  = Color(0xFF004975)
private val DarkOnTertiaryContainer= Color(0xFFD1E4FF)

private val DarkBackground       = Color(0xFF14151A)
private val DarkOnBackground     = Color(0xFFE8E7E2)
private val DarkSurface          = Color(0xFF1E2028)
private val DarkOnSurface        = Color(0xFFE8E7E2)
private val DarkSurfaceVariant   = Color(0xFF282C36)
private val DarkOnSurfaceVariant = Color(0xFFA0A098)

class MainActivity : ComponentActivity() {

    private val themeViewModel: ThemeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val currentTheme by themeViewModel.appTheme.collectAsState()
            val currentFontSize by themeViewModel.fontSize.collectAsState()
            val isSystemDark = isSystemInDarkTheme()

            val isDark = when (currentTheme) {
                AppTheme.SYSTEM -> isSystemDark
                AppTheme.LIGHT -> false
                AppTheme.DARK -> true
            }

            val colorScheme = if (isDark) {
                darkColorScheme(
                    primary = DarkPrimary,
                    onPrimary = DarkOnPrimary,
                    primaryContainer = DarkPrimaryContainer,
                    onPrimaryContainer = DarkOnPrimaryContainer,
                    secondary = DarkSecondary,
                    onSecondary = DarkOnSecondary,
                    secondaryContainer = DarkSecondaryContainer,
                    onSecondaryContainer = DarkOnSecondaryContainer,
                    tertiary = DarkTertiary,
                    onTertiary = DarkOnTertiary,
                    tertiaryContainer = DarkTertiaryContainer,
                    onTertiaryContainer = DarkOnTertiaryContainer,
                    background = DarkBackground,
                    onBackground = DarkOnBackground,
                    surface = DarkSurface,
                    onSurface = DarkOnSurface,
                    surfaceVariant = DarkSurfaceVariant,
                    onSurfaceVariant = DarkOnSurfaceVariant,
                    surfaceContainer = Color(0xFF1E2028),
                    surfaceContainerLow = Color(0xFF191C23),
                    surfaceContainerLowest = Color(0xFF0C0F16),
                    surfaceContainerHigh = Color(0xFF282A33),
                    surfaceContainerHighest = Color(0xFF33353E)
                )
            } else {
                lightColorScheme(
                    primary = LightPrimary,
                    onPrimary = LightOnPrimary,
                    primaryContainer = LightPrimaryContainer,
                    onPrimaryContainer = LightOnPrimaryContainer,
                    secondary = LightSecondary,
                    onSecondary = LightOnSecondary,
                    secondaryContainer = LightSecondaryContainer,
                    onSecondaryContainer = LightOnSecondaryContainer,
                    tertiary = LightTertiary,
                    onTertiary = LightOnTertiary,
                    tertiaryContainer = LightTertiaryContainer,
                    onTertiaryContainer = LightOnTertiaryContainer,
                    background = LightBackground,
                    onBackground = LightOnBackground,
                    surface = LightSurface,
                    onSurface = LightOnSurface,
                    surfaceVariant = LightSurfaceVariant,
                    onSurfaceVariant = LightOnSurfaceVariant,
                    surfaceContainer = Color(0xFFF4F3EF),       // 修改: 與 Background 一致
                    surfaceContainerLow = Color(0xFFF4F3EF),    // 修改: 與 Background 一致
                    surfaceContainerLowest = Color(0xFFFFFFFF), // 最淺為白
                    surfaceContainerHigh = Color(0xFFEBE9E2),   // 稍深
                    surfaceContainerHighest = Color(0xFFE2E0D8) // 最深
                )
            }

            val typography = buildTypography(currentFontSize.scale)

            MaterialTheme(
                colorScheme = colorScheme,
                typography = typography
            ) {
                var isLoggedIn by remember { mutableStateOf(false) }
                var currentUserId by remember { mutableLongStateOf(-1L) }

                if (isLoggedIn) {
                    MainScreen(
                        themeViewModel = themeViewModel,
                        userId = currentUserId,
                        onLogout = { 
                            isLoggedIn = false
                            currentUserId = -1L
                        }
                    )
                } else {
                    LoginScreen(
                        onLoginSuccess = { userId ->
                            currentUserId = userId
                            isLoggedIn = true
                        }
                    )
                }
            }
        }
    }
}

fun buildTypography(scale: Float): Typography {
    fun TextUnit.scaled() = (this.value * scale).sp
    return Typography().run {
        copy(
            displayLarge = displayLarge.copy(fontSize = displayLarge.fontSize.scaled()),
            displayMedium = displayMedium.copy(fontSize = displayMedium.fontSize.scaled()),
            displaySmall = displaySmall.copy(fontSize = displaySmall.fontSize.scaled()),
            headlineLarge = headlineLarge.copy(fontSize = headlineLarge.fontSize.scaled()),
            headlineMedium = headlineMedium.copy(fontSize = headlineMedium.fontSize.scaled()),
            headlineSmall = headlineSmall.copy(fontSize = headlineSmall.fontSize.scaled()),
            titleLarge = titleLarge.copy(fontSize = titleLarge.fontSize.scaled()),
            titleMedium = titleMedium.copy(fontSize = titleMedium.fontSize.scaled()),
            titleSmall = titleSmall.copy(fontSize = titleSmall.fontSize.scaled()),
            bodyLarge = bodyLarge.copy(fontSize = bodyLarge.fontSize.scaled()),
            bodyMedium = bodyMedium.copy(fontSize = bodyMedium.fontSize.scaled()),
            bodySmall = bodySmall.copy(fontSize = bodySmall.fontSize.scaled()),
            labelLarge = labelLarge.copy(fontSize = labelLarge.fontSize.scaled()),
            labelMedium = labelMedium.copy(fontSize = labelMedium.fontSize.scaled()),
            labelSmall = labelSmall.copy(fontSize = labelSmall.fontSize.scaled()),
        )
    }
}