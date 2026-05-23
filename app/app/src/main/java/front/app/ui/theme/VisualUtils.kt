package front.app.ui.theme

import androidx.compose.foundation.BorderStroke
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun getAppGradient(): Brush {
    return Brush.linearGradient(
        colors = listOf(
            MaterialTheme.colorScheme.primary,
            MaterialTheme.colorScheme.secondary
        )
    )
}

@Composable
fun getGlassBackground(alpha: Float = 0.7f): Color {
    return MaterialTheme.colorScheme.surface.copy(alpha = alpha)
}

@Composable
fun getGlassBorder(): BorderStroke {
    return BorderStroke(0.5.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f))
}

// 共享的預設標籤資料
val dummyTags = listOf("全部", "刑偵", "愛情", "懸疑", "古裝", "科幻")
