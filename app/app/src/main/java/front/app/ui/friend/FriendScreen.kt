package front.app.ui.friend

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import front.app.model.DramaResponse
import front.app.viewmodel.DramaViewModel
import front.app.ui.theme.getAppGradient
import front.app.ui.theme.getGlassBackground
import front.app.ui.theme.getGlassBorder

enum class SortOrder(val label: String) {
    UPDATED_AT("更新時間"),
    GRADE("評級")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FriendScreen(
    userId: Long = -1L,
    viewModel: DramaViewModel = viewModel(),
    onCardClick: (DramaResponse) -> Unit = {}
) {
    val publicDramas by viewModel.publicDramas.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    var sortOrder by remember { mutableStateOf(SortOrder.UPDATED_AT) }
    
    val context = LocalContext.current
    var showAddFriendDialog by remember { mutableStateOf(false) }
    var friendNameInput by remember { mutableStateOf("") }
    
    val pendingRequests by viewModel.pendingRequests.collectAsState()
    var showRequestsDialog by remember { mutableStateOf(false) }

    LaunchedEffect(userId) {
        if (userId != -1L) {
            viewModel.fetchFriendsDramas(userId)
            viewModel.fetchPendingRequests(userId)
        }
    }

    val sortedDramas = remember(publicDramas, sortOrder) {
        when (sortOrder) {
            SortOrder.UPDATED_AT -> publicDramas.sortedByDescending { it.updatedAt ?: "" }
            SortOrder.GRADE -> publicDramas.sortedByDescending { it.grade ?: 0f }
        }
    }

    val filteredDramas = sortedDramas.filter {
        it.title.contains(searchQuery, ignoreCase = true) ||
        (it.tag?.contains(searchQuery, ignoreCase = true) ?: false) ||
        it.userName.contains(searchQuery, ignoreCase = true)
    }

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
        Column(modifier = Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("搜尋標籤、劇名或朋友") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(Icons.Default.Close, contentDescription = "清除")
                            }
                        }
                        IconButton(onClick = { 
                            if (userId != -1L) {
                                viewModel.fetchFriendsDramas(userId)
                                viewModel.fetchPendingRequests(userId)
                            }
                        }) {
                            Icon(Icons.Default.Refresh, contentDescription = "重新整理")
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp)
                )
                
                Spacer(Modifier.width(12.dp))
                
                BadgedBox(
                    badge = {
                        if (pendingRequests.isNotEmpty()) {
                            Badge(
                                containerColor = MaterialTheme.colorScheme.error,
                                contentColor = Color.White
                            ) {
                                Text(pendingRequests.size.toString())
                            }
                        }
                    }
                ) {
                    IconButton(
                        onClick = { 
                            if (pendingRequests.isNotEmpty()) {
                                showRequestsDialog = true
                            } else {
                                showAddFriendDialog = true 
                            }
                        },
                        modifier = Modifier
                            .size(48.dp)
                            .background(getAppGradient(), RoundedCornerShape(12.dp))
                    ) {
                        Icon(
                            if (pendingRequests.isNotEmpty()) Icons.Default.Notifications else Icons.Default.PersonAdd, 
                            contentDescription = "好友申請",
                            tint = Color.White
                        )
                    }
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End
            ) {
                Icon(
                    imageVector = Icons.Default.Sort,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(8.dp))
                SortOrder.entries.forEach { order ->
                    // 簡化 FilterChip 以相容不同版本的 M3
                    FilterChip(
                        selected = sortOrder == order,
                        onClick = { sortOrder = order },
                        label = { Text(order.label, style = MaterialTheme.typography.labelMedium) },
                        modifier = Modifier.padding(start = 8.dp),
                        shape = RoundedCornerShape(8.dp)
                    )
                }
            }

            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(strokeWidth = 3.dp)
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 16.dp, top = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(filteredDramas) { drama ->
                        FriendDramaCard(drama, onClick = { onCardClick(drama) })
                    }
                    if (filteredDramas.isEmpty() && !isLoading) {
                        item {
                            Box(modifier = Modifier.fillMaxWidth().padding(top = 32.dp), contentAlignment = Alignment.Center) {
                                Text("查無結果", style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f))
                            }
                        }
                    }
                }
            }
        }
    }

    if (showAddFriendDialog) {
        AlertDialog(
            onDismissRequest = { 
                showAddFriendDialog = false
                friendNameInput = ""
            },
            title = { Text("新增好友") },
            text = {
                Column {
                    Text("請輸入對方的帳號名稱：")
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(
                        value = friendNameInput,
                        onValueChange = { friendNameInput = it },
                        placeholder = { Text("帳號名稱") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (friendNameInput.isNotBlank()) {
                            viewModel.addFriend(userId, friendNameInput) { error ->
                                if (error == null) {
                                    Toast.makeText(context, "已發送好友申請: $friendNameInput", Toast.LENGTH_SHORT).show()
                                    showAddFriendDialog = false
                                    friendNameInput = ""
                                } else {
                                    Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    },
                    enabled = friendNameInput.isNotBlank()
                ) {
                    Text("新增")
                }
            },
            dismissButton = {
                TextButton(onClick = { 
                    showAddFriendDialog = false
                    friendNameInput = ""
                }) {
                    Text("取消")
                }
            }
        )
    }

    if (showRequestsDialog) {
        AlertDialog(
            onDismissRequest = { showRequestsDialog = false },
            title = { Text("好友申請") },
            text = {
                if (pendingRequests.isEmpty()) {
                    Text("目前沒有待處理的申請")
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxWidth().heightIn(max = 300.dp)
                    ) {
                        items(pendingRequests) { requester ->
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(requester.userName, style = MaterialTheme.typography.bodyLarge)
                                Row {
                                    IconButton(
                                        onClick = {
                                            viewModel.acceptFriendRequest(userId, requester.id ?: 0L) {
                                                viewModel.fetchFriendsDramas(userId)
                                                Toast.makeText(context, "已接受 ${requester.userName} 的好友申請", Toast.LENGTH_SHORT).show()
                                                if (pendingRequests.isEmpty()) showRequestsDialog = false
                                            }
                                        }
                                    ) {
                                        Icon(Icons.Default.Check, contentDescription = "接受", tint = Color(0xFF4CAF50))
                                    }
                                    IconButton(
                                        onClick = {
                                            viewModel.declineFriendRequest(userId, requester.id ?: 0L) {
                                                Toast.makeText(context, "已拒絕申請", Toast.LENGTH_SHORT).show()
                                                if (pendingRequests.isEmpty()) showRequestsDialog = false
                                            }
                                        }
                                    ) {
                                        Icon(Icons.Default.Close, contentDescription = "拒絕", tint = MaterialTheme.colorScheme.error)
                                    }
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showRequestsDialog = false }) {
                    Text("關閉")
                }
            }
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun FriendDramaCard(drama: DramaResponse, onClick: () -> Unit = {}) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(getGlassBorder(), RoundedCornerShape(20.dp)),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = getGlassBackground(0.6f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onClick() }
        ) {
            Box {
                AsyncImage(
                    model = if (drama.posterPath != null) "https://image.tmdb.org/t/p/w500${drama.posterPath}" else null,
                    contentDescription = null,
                    modifier = Modifier
                        .width(110.dp)
                        .height(160.dp)
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                    contentScale = ContentScale.Crop,
                    error = null
                )
                if (drama.grade != null && drama.grade!! >= 8.0f) {
                    Box(
                        modifier = Modifier
                            .padding(8.dp)
                            .background(getAppGradient(), RoundedCornerShape(4.dp))
                            .padding(horizontal = 4.dp, vertical = 2.dp)
                    ) {
                        Text("TOP", color = Color.White, fontSize = 8.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Column(modifier = Modifier.padding(16.dp).weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = drama.userName,
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary
                    )
                    if (drama.grade != null) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Filled.Star,
                                contentDescription = null,
                                tint = Color(0xFFFFD700),
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(Modifier.width(4.dp))
                            Text(
                                text = "%.1f".format(drama.grade),
                                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = drama.title,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier.align(Alignment.Start),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                
                Spacer(modifier = Modifier.weight(1f))
                
                if (!drama.tag.isNullOrBlank()) {
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.padding(top = 8.dp)
                    ) {
                        drama.tag.split(",").filter { it.isNotBlank() }.take(3).forEach { tag ->
                            FriendFilterBadge(label = tag.trim(), color = MaterialTheme.colorScheme.primary)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun FriendFilterBadge(label: String, color: Color) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = color.copy(alpha = 0.1f),
        border = BorderStroke(0.5.dp, color.copy(alpha = 0.2f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label, 
                fontSize = 10.sp, 
                color = color, 
                fontWeight = FontWeight.Bold
            )
        }
    }
}
