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
private val LightPrimary         = Color(0xFF3B82F6) // Modern Blue
private val LightOnPrimary       = Color(0xFFFFFFFF)
private val LightPrimaryContainer  = Color(0xFFDBEAFE)
private val LightOnPrimaryContainer= Color(0xFF1E3A8A)

private val LightSecondary       = Color(0xFF06B6D4) // Cyan
private val LightOnSecondary     = Color(0xFFFFFFFF)
private val LightSecondaryContainer = Color(0xFFCFFAFE)
private val LightOnSecondaryContainer = Color(0xFF083344)

private val LightTertiary        = Color(0xFF3B82F6)
private val LightOnTertiary      = Color(0xFFFFFFFF)
private val LightTertiaryContainer = Color(0xFFDBEAFE)
private val LightOnTertiaryContainer = Color(0xFF1E3A8A)

private val LightBackground      = Color(0xFFF8FAFC) // Slate 50 (乾淨俐落的淺灰白)
private val LightOnBackground    = Color(0xFF0F172A)
private val LightSurface         = Color(0xFFF8FAFC)
private val LightOnSurface       = Color(0xFF0F172A)
private val LightSurfaceVariant  = Color(0xFFE2E8F0)
private val LightOnSurfaceVariant= Color(0xFF475569)
 
// ── 深色主題色票 ──────────────────────────────────────────
private val DarkPrimary          = Color(0xFF60A5FA) // 稍微調亮的藍色確保對比
private val DarkOnPrimary        = Color(0xFF003362)
private val DarkPrimaryContainer = Color(0xFF1E3A8A)
private val DarkOnPrimaryContainer= Color(0xFFDBEAFE)

private val DarkSecondary        = Color(0xFF22D3EE) // 稍微調亮的青色
private val DarkOnSecondary      = Color(0xFF00363D)
private val DarkSecondaryContainer = Color(0xFF083344)
private val DarkOnSecondaryContainer = Color(0xFFCFFAFE)

private val DarkTertiary         = Color(0xFF60A5FA)
private val DarkOnTertiary       = Color(0xFF003362)
private val DarkTertiaryContainer  = Color(0xFF1E3A8A)
private val DarkOnTertiaryContainer= Color(0xFFDBEAFE)

private val DarkBackground       = Color(0xFF0A0F1E) // 深海藍黑
private val DarkOnBackground     = Color(0xFFF1F5F9)
private val DarkSurface          = Color(0xFF0A0F1E)
private val DarkOnSurface        = Color(0xFFF1F5F9)
private val DarkSurfaceVariant   = Color(0xFF1E293B)
private val DarkOnSurfaceVariant = Color(0xFF94A3B8)

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
                    surfaceContainer = Color(0xFF0A0F1E),
                    surfaceContainerLow = Color(0xFF0A0F1E),
                    surfaceContainerLowest = Color(0xFF000000),
                    surfaceContainerHigh = Color(0xFF161C2C),
                    surfaceContainerHighest = Color(0xFF21283A)
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
                    surfaceContainer = Color(0xFFF8FAFC),       // 與 Background 一致
                    surfaceContainerLow = Color(0xFFF8FAFC),    // 與 Background 一致
                    surfaceContainerLowest = Color(0xFFFFFFFF),
                    surfaceContainerHigh = Color(0xFFF1F5F9),   // 稍深 (Slate 100)
                    surfaceContainerHighest = Color(0xFFE2E8F0) // 最深 (Slate 200)
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