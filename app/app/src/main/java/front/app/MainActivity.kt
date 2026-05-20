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

// 先在 class 外面或頂部定義兩組 Color
private val LightPrimary = Color(0xFF185FA5)
private val LightOnPrimary = Color(0xFFFFFFFF)
private val LightSecondary = Color(0xFFBA7517)
private val LightOnSecondary = Color(0xFFFFFFFF)
private val LightBackground = Color(0xFFF4F3EF)
private val LightOnBackground = Color(0xFF1A1A18)
private val LightSurface = Color(0xFFFFFFFF)
private val LightOnSurface = Color(0xFF1A1A18)

private val DarkPrimary = Color(0xFF85B7EB)
private val DarkOnPrimary = Color(0xFF042C53)
private val DarkSecondary = Color(0xFFEF9F27)
private val DarkOnSecondary = Color(0xFF412402)
private val DarkBackground = Color(0xFF14151A)
private val DarkOnBackground = Color(0xFFE8E7E2)
private val DarkSurface = Color(0xFF1E2028)
private val DarkOnSurface = Color(0xFFE8E7E2)

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
                    secondary = DarkSecondary,
                    onSecondary = DarkOnSecondary,
                    background = DarkBackground,
                    onBackground = DarkOnBackground,
                    surface = DarkSurface,
                    onSurface = DarkOnSurface
                )
            } else {
                lightColorScheme(
                    primary = LightPrimary,
                    onPrimary = LightOnPrimary,
                    secondary = LightSecondary,
                    onSecondary = LightOnSecondary,
                    background = LightBackground,
                    onBackground = LightOnBackground,
                    surface = LightSurface,
                    onSurface = LightOnSurface
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