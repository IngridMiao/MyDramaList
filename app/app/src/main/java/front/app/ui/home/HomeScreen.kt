package front.app.ui.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import front.app.model.Drama
import front.app.viewmodel.DramaViewModel

// TODO: 之後換成從 ViewModel 取得的真實資料
val dummyTags = listOf("全部", "刑偵", "愛情", "懸疑", "古裝", "科幻")

enum class SortType(val label: String) {
    ADDED_TIME("觀看時間"),
    GRADE("評級")
}

enum class ShownFilter(val label: String) {
    ALL("全部"),
    PUBLIC("public"),
    PRIVATE("private")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    userId: Long,
    onAddClick: () -> Unit = {},
    onCardClick: (Drama) -> Unit = {},
    viewModel: DramaViewModel = viewModel()
) {
    var searchQuery by remember { mutableStateOf("") }
    var tagExpanded by remember { mutableStateOf(false) }
    var selectedTags by remember { mutableStateOf(setOf<String>()) }
    var pendingTags by remember { mutableStateOf(setOf<String>()) }
    var showFilterSheet by remember { mutableStateOf(false) }
    var sortType by remember { mutableStateOf(SortType.ADDED_TIME) }
    var shownFilter by remember { mutableStateOf(ShownFilter.ALL) }

    val dramas by viewModel.dramas.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    LaunchedEffect(userId) {
        viewModel.fetchDramas(userId)
    }

    val filteredDramas = remember(dramas, searchQuery, selectedTags, sortType, shownFilter) {
        dramas
            .filter { drama ->
                if (searchQuery.isBlank()) true
                else drama.title.contains(searchQuery, ignoreCase = true) ||
                        (drama.actors ?: "").contains(searchQuery, ignoreCase = true)
            }
            .filter { drama ->
                selectedTags.isEmpty() || selectedTags.contains(drama.tag)
            }
            .filter { drama ->
                when (shownFilter) {
                    ShownFilter.ALL -> true
                    ShownFilter.PUBLIC -> drama.shown
                    ShownFilter.PRIVATE -> !drama.shown
                }
            }
            .sortedWith(compareByDescending {
                when (sortType) {
                    SortType.ADDED_TIME -> 0L // 目前 Drama 沒有 addedTime
                    SortType.GRADE -> it.grade ?: 0f
                }
            })
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {

            // ── 搜尋框 ──
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("搜尋劇名或演員...") },
                leadingIcon = {
                    Icon(Icons.Outlined.Search, contentDescription = null)
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(Icons.Outlined.Close, contentDescription = "清除")
                        }
                    }
                },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            )

            // ── Tag 列 ──
            Surface(
                tonalElevation = 2.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                if (!tagExpanded) pendingTags = selectedTags
                                tagExpanded = !tagExpanded
                            }
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "tag：" + if (selectedTags.isEmpty()) "全部"
                            else selectedTags.joinToString("、"),
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.weight(1f),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Icon(
                            imageVector = if (tagExpanded)
                                Icons.Outlined.KeyboardArrowUp
                            else
                                Icons.Outlined.KeyboardArrowDown,
                            contentDescription = "展開 tag"
                        )
                    }

                    AnimatedVisibility(visible = tagExpanded) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp)
                        ) {
                            TextButton(onClick = { /* TODO: 新增 tag */ }) {
                                Icon(Icons.Outlined.Add, contentDescription = null)
                                Spacer(Modifier.width(4.dp))
                                Text("+ add tag")
                            }
                            HorizontalDivider()
                            Spacer(Modifier.height(4.dp))
                            dummyTags.filter { it != "全部" }.forEach { tag ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            pendingTags = if (pendingTags.contains(tag))
                                                pendingTags - tag else pendingTags + tag
                                        }
                                        .padding(vertical = 10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Checkbox(
                                        checked = pendingTags.contains(tag),
                                        onCheckedChange = {
                                            pendingTags = if (it) pendingTags + tag
                                            else pendingTags - tag
                                        }
                                    )
                                    Spacer(Modifier.width(8.dp))
                                    Text(tag, style = MaterialTheme.typography.bodyMedium)
                                }
                            }
                            Spacer(Modifier.height(8.dp))
                            Button(
                                onClick = {
                                    selectedTags = pendingTags
                                    tagExpanded = false
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 12.dp)
                            ) { Text("確定") }
                        }
                    }
                }
            }

            // ── 篩選列 ──
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "排序：${sortType.label}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.width(16.dp))
                Text(
                    text = "shown：${shownFilter.label}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.weight(1f))
                IconButton(onClick = { showFilterSheet = true }) {
                    Icon(
                        Icons.Outlined.FilterList,
                        contentDescription = "篩選",
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            HorizontalDivider()

            // ── 劇集列表 ──
            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else if (filteredDramas.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "沒有符合的結果",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(filteredDramas) { drama ->
                        DramaCard(
                            drama = drama,
                            showTag = selectedTags.isNotEmpty(),
                            showShown = shownFilter != ShownFilter.ALL,
                            onClick = { onCardClick(drama) }
                        )
                    }
                }
            }
        }

        FloatingActionButton(
            onClick = onAddClick,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(24.dp)
        ) {
            Icon(Icons.Outlined.Add, contentDescription = "新增")
        }
    }

    if (showFilterSheet) {
        ModalBottomSheet(onDismissRequest = { showFilterSheet = false }) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .padding(bottom = 32.dp)
            ) {
                Text("排序方式", style = MaterialTheme.typography.titleSmall)
                Spacer(Modifier.height(8.dp))
                SortType.entries.forEach { type ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { sortType = type }
                            .padding(vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(selected = sortType == type, onClick = { sortType = type })
                        Spacer(Modifier.width(8.dp))
                        Text(type.label)
                    }
                }
                Spacer(Modifier.height(16.dp))
                HorizontalDivider()
                Spacer(Modifier.height(16.dp))
                Text("顯示範圍", style = MaterialTheme.typography.titleSmall)
                Spacer(Modifier.height(8.dp))
                ShownFilter.entries.forEach { filter ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { shownFilter = filter }
                            .padding(vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(selected = shownFilter == filter, onClick = { shownFilter = filter })
                        Spacer(Modifier.width(8.dp))
                        Text(filter.label)
                    }
                }
                Spacer(Modifier.height(16.dp))
                Button(
                    onClick = { showFilterSheet = false },
                    modifier = Modifier.fillMaxWidth()
                ) { Text("完成") }
            }
        }
    }
}

@Composable
fun DramaCard(
    drama: Drama,
    showTag: Boolean,
    showShown: Boolean,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = drama.title,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.weight(1f)
                )
                if (drama.grade != null) {
                    Icon(
                        Icons.Outlined.Star,
                        contentDescription = null,
                        tint = Color(0xFFFFC107),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(Modifier.width(2.dp))
                    Text(
                        text = "%.1f".format(drama.grade),
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
            if (!drama.viewPoint.isNullOrBlank()) {
                Spacer(Modifier.height(4.dp))
                Text(
                    text = drama.viewPoint!!,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
            if ((showTag && drama.tag != null) || showShown) {
                Spacer(Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    if (showTag && drama.tag != null) {
                        FilterBadge(label = drama.tag!!, color = Color(0xFF4CAF50))
                    }
                    if (showShown) {
                        FilterBadge(
                            label = if (drama.shown) "public" else "private",
                            color = if (drama.shown) Color(0xFF2196F3) else Color(0xFF9C27B0)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun FilterBadge(label: String, color: Color) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = color.copy(alpha = 0.12f)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Outlined.Check,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(12.dp)
            )
            Spacer(Modifier.width(3.dp))
            Text(text = label, fontSize = 11.sp, color = color)
        }
    }
}
