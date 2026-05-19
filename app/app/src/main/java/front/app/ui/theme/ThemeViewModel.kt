package front.app.ui.theme

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

enum class AppTheme(val label: String) {
    SYSTEM("跟隨系統"),
    LIGHT("淺色"),
    DARK("深色")
}

enum class FontSize(val label: String, val scale: Float) {
    SMALL("小", 0.85f),
    MEDIUM("中", 1.0f),
    LARGE("大", 1.15f)
}

class ThemeViewModel : ViewModel() {
    private val _appTheme = MutableStateFlow(AppTheme.SYSTEM)
    val appTheme: StateFlow<AppTheme> = _appTheme

    private val _fontSize = MutableStateFlow(FontSize.MEDIUM)
    val fontSize: StateFlow<FontSize> = _fontSize

    fun setTheme(theme: AppTheme) { _appTheme.value = theme }
    fun setFontSize(size: FontSize) { _fontSize.value = size }
}