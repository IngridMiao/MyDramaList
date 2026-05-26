package front.app.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import front.app.ui.theme.*

@Composable
fun ProfileScreen(
    themeViewModel: ThemeViewModel,
    userName: String = "一般帳號",
    onLogout: () -> Unit = {},
    onDeleteAccount: () -> Unit = {}
) {
    val currentTheme by themeViewModel.appTheme.collectAsState()
    val currentFontSize by themeViewModel.fontSize.collectAsState()

    var showLogoutDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    Box(modifier = Modifier
        .fillMaxSize()
        .background(
            Brush.verticalGradient(
                colors = listOf(
                    MaterialTheme.colorScheme.background,
                    MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                )
            )
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp)
        ) {
            Spacer(Modifier.height(32.dp))

            // ── 帳號資訊 ──
            SectionTitle("帳號資訊")
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(getGlassBorder(), RoundedCornerShape(20.dp)),
                shape = RoundedCornerShape(20.dp),
                color = getGlassBackground(0.5f),
                tonalElevation = 2.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .background(getAppGradient(), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Outlined.Person,
                            contentDescription = null,
                            modifier = Modifier.size(32.dp),
                            tint = Color.White
                        )
                    }
                    Spacer(Modifier.width(16.dp))
                    Column {
                        Text(
                            text = userName,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = "登入方式：帳號密碼",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedButton(
                    onClick = { showLogoutDialog = true },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    border = getGlassBorder()
                ) {
                    Icon(Icons.Outlined.Logout, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("登出")
                }

                TextButton(
                    onClick = { showDeleteDialog = true },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                ) {
                    Icon(Icons.Outlined.DeleteOutline, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("刪除帳號")
                }
            }

            Spacer(Modifier.height(32.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.1f))
            Spacer(Modifier.height(32.dp))

            // ── 外觀 ──
            SectionTitle("外觀設定")
            
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(getGlassBorder(), RoundedCornerShape(20.dp)),
                shape = RoundedCornerShape(20.dp),
                color = getGlassBackground(0.5f)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text("色彩主題", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
                    Spacer(Modifier.height(12.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        AppTheme.entries.forEach { theme ->
                            FilterChip(
                                selected = currentTheme == theme,
                                onClick = { themeViewModel.setTheme(theme) },
                                label = { Text(theme.label) },
                                shape = RoundedCornerShape(8.dp)
                            )
                        }
                    }

                    Spacer(Modifier.height(24.dp))

                    Text("介面字體", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
                    Spacer(Modifier.height(12.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        FontSize.entries.forEach { size ->
                            FilterChip(
                                selected = currentFontSize == size,
                                onClick = { themeViewModel.setFontSize(size) },
                                label = { Text(size.label) },
                                shape = RoundedCornerShape(8.dp)
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(32.dp))
            SectionTitle("隱私設定")
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(getGlassBorder(), RoundedCornerShape(20.dp)),
                shape = RoundedCornerShape(20.dp),
                color = getGlassBackground(0.5f)
            ) {
                Row(
                    modifier = Modifier.padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Outlined.Lock,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(Modifier.width(16.dp))
                    Text(
                        text = "社群平台串接開發中",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }

    // ── 登出確認 Dialog ──
    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text("登出") },
            text = { Text("確定要登出嗎？") },
            confirmButton = {
                TextButton(onClick = {
                    showLogoutDialog = false
                    onLogout()
                }) { Text("確定") }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) { Text("取消") }
            }
        )
    }

    // ── 刪除帳號確認 Dialog ──
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("刪除帳號") },
            text = {
                Text(
                    "刪除後所有資料將無法復原。\n確定要刪除這個帳號嗎？",
                    color = MaterialTheme.colorScheme.error
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog = false
                        // TODO: 之後改成呼叫 API 刪除帳號
                        onDeleteAccount()
                    },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) { Text("刪除") }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) { Text("取消") }
            }
        )
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.primary
    )
    Spacer(Modifier.height(8.dp))
}