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
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import front.app.ui.MainScreen
import front.app.ui.login.LoginScreen
import front.app.ui.theme.AppTheme
import front.app.ui.theme.FontSize
import front.app.ui.theme.ThemeViewModel

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

            val typography = buildTypography(currentFontSize.scale)

            MaterialTheme(
                colorScheme = if (isDark) darkColorScheme() else lightColorScheme(),
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