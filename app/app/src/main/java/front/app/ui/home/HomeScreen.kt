package front.app.ui.home

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.material.icons.outlined.*
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import front.app.model.Drama
import front.app.model.Tag
import front.app.viewmodel.DramaViewModel
import coil.compose.AsyncImage
import front.app.ui.theme.getAppGradient
import front.app.ui.theme.getGlassBackground
import front.app.ui.theme.getGlassBorder
import front.app.ui.theme.dummyTags

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

    var isAddingTag by remember { mutableStateOf(false) }
    var newTagInput by remember { mutableStateOf("") }
    val backendTags by viewModel.tags.collectAsState()
    val commonTags = remember { dummyTags.filter { it != "全部" } }
    val displayTags = remember(backendTags) {
        val backendTagNames = backendTags.map { tag: Tag -> tag.tagName }
        (commonTags + backendTagNames).distinct()
    }

    val dramas by viewModel.dramas.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    LaunchedEffect(userId) {
        viewModel.fetchDramas(userId)
        viewModel.fetchTags(userId)
    }

    val filteredDramas = remember(dramas, searchQuery, selectedTags, sortType, shownFilter) {
        dramas
            .filter { drama: Drama ->
                if (searchQuery.isBlank()) true
                else drama.title.contains(searchQuery, ignoreCase = true) ||
                        (drama.actors ?: "").contains(searchQuery, ignoreCase = true)
            }
            .filter { drama ->
                if (selectedTags.isEmpty()) true
                else {
                    val dramaTags = drama.tag?.split(",")?.map { it.trim() }?.toSet() ?: emptySet()
                    selectedTags.any { it in dramaTags }
                }
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
                    SortType.ADDED_TIME -> 0L
                    SortType.GRADE -> it.grade ?: 0f
                }
            })
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
                        IconButton(onClick = { 
                            viewModel.fetchDramas(userId)
                            viewModel.fetchTags(userId)
                        }) {
                            Icon(Icons.Outlined.Refresh, contentDescription = "重新整理")
                        }
                    },
                singleLine = true,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            )

            // ── Tag 列 ──
            Surface(
                tonalElevation = 2.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .clip(RoundedCornerShape(12.dp)),
                color = getGlassBackground(),
                border = getGlassBorder()
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
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(getAppGradient())
                        )
                        Spacer(Modifier.width(12.dp))
                        Text(
                            text = "tag：" + if (selectedTags.isEmpty()) "全部"
                            else selectedTags.joinToString("、"),
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
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
                            if (isAddingTag) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    OutlinedTextField(
                                        value = newTagInput,
                                        onValueChange = { newTagInput = it },
                                        label = { Text("新 Tag") },
                                        modifier = Modifier.weight(1f),
                                        singleLine = true
                                    )
                                    TextButton(onClick = {
                                        isAddingTag = false
                                        newTagInput = ""
                                    }) {
                                        Text("取消")
                                    }
                                    Button(
                                        onClick = {
                                            if (newTagInput.isNotBlank()) {
                                                if (displayTags.none { it == newTagInput }) {
                                                    viewModel.saveTag(Tag(userId = userId, tagName = newTagInput))
                                                }
                                                pendingTags = pendingTags + newTagInput
                                            }
                                            isAddingTag = false
                                            newTagInput = ""
                                        },
                                        enabled = newTagInput.isNotBlank(),
                                        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                                        contentPadding = PaddingValues(),
                                        modifier = Modifier.height(40.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxHeight()
                                                .background(getAppGradient(), RoundedCornerShape(8.dp))
                                                .padding(horizontal = 16.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text("確認", color = Color.White)
                                        }
                                    }
                                }
                            } else {
                                TextButton(onClick = { isAddingTag = true }) {
                                    Icon(Icons.Outlined.Add, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                    Spacer(Modifier.width(4.dp))
                                    Text("+ add tag", color = MaterialTheme.colorScheme.primary)
                                }
                            }
                            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.1f))
                            Spacer(Modifier.height(4.dp))
                            displayTags.forEach { tagName ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            pendingTags = if (pendingTags.contains(tagName))
                                                pendingTags - tagName else pendingTags + tagName
                                        }
                                        .padding(vertical = 10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Checkbox(
                                        checked = pendingTags.contains(tagName),
                                        onCheckedChange = {
                                            pendingTags = if (it) pendingTags + tagName
                                            else pendingTags - tagName
                                        }
                                    )
                                    Spacer(Modifier.width(8.dp))
                                    Text(tagName, style = MaterialTheme.typography.bodyMedium)
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
                                    .height(48.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                                contentPadding = PaddingValues()
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(getAppGradient(), RoundedCornerShape(12.dp)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("確定", color = Color.White, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }

            // ── 篩選列 ──
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "排序：${sortType.label}",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.width(16.dp))
                Text(
                    text = "狀態：${shownFilter.label}",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.weight(1f))
                IconButton(
                    onClick = { showFilterSheet = true },
                    modifier = Modifier
                        .size(32.dp)
                        .background(MaterialTheme.colorScheme.surface, CircleShape)
                        .border(getGlassBorder(), CircleShape)
                ) {
                    Icon(
                        Icons.Outlined.FilterList,
                        contentDescription = "篩選",
                        modifier = Modifier.size(18.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }

            // ── 劇集列表 ──
            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(strokeWidth = 3.dp, color = MaterialTheme.colorScheme.primary)
                }
            } else if (filteredDramas.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "沒有符合的結果",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                    )
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 80.dp, top = 4.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
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
            shape = RoundedCornerShape(12.dp),
            containerColor = Color.Transparent,
            elevation = FloatingActionButtonDefaults.elevation(0.dp),
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(24.dp)
                .size(64.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(getAppGradient(), RoundedCornerShape(12.dp))
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Outlined.Add, contentDescription = "新增", tint = Color.White, modifier = Modifier.size(32.dp))
            }
        }
    }

    if (showFilterSheet) {
        ModalBottomSheet(
            onDismissRequest = { showFilterSheet = false },
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f),
            tonalElevation = 8.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .padding(bottom = 32.dp)
            ) {
                Text("排序方式", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(12.dp))
                SortType.entries.forEach { type ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { sortType = type }
                            .padding(vertical = 12.dp, horizontal = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(selected = sortType == type, onClick = { sortType = type })
                        Spacer(Modifier.width(12.dp))
                        Text(type.label, style = MaterialTheme.typography.bodyLarge)
                    }
                }
                Spacer(Modifier.height(20.dp))
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.1f))
                Spacer(Modifier.height(20.dp))
                Text("顯示範圍", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(12.dp))
                ShownFilter.entries.forEach { filter ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { shownFilter = filter }
                            .padding(vertical = 12.dp, horizontal = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(selected = shownFilter == filter, onClick = { shownFilter = filter })
                        Spacer(Modifier.width(12.dp))
                        Text(filter.label, style = MaterialTheme.typography.bodyLarge)
                    }
                }
                Spacer(Modifier.height(24.dp))
                Button(
                    onClick = { showFilterSheet = false },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                    contentPadding = PaddingValues()
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(getAppGradient(), RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("完成", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DramaCard(
    drama: Drama,
    showTag: Boolean,
    showShown: Boolean,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .border(getGlassBorder(), RoundedCornerShape(20.dp)),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = getGlassBackground(0.6f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            // 海報
            Box {
                AsyncImage(
                    model = if (drama.posterPath != null) "https://image.tmdb.org/t/p/w500${drama.posterPath}" else null,
                    contentDescription = null,
                    modifier = Modifier
                        .width(110.dp)
                        .height(160.dp)
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                    contentScale = ContentScale.Crop
                )
                // 海報上的小裝飾
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
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = drama.title,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        modifier = Modifier.weight(1f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    if (drama.grade != null) {
                        Icon(
                            Icons.Filled.Star,
                            contentDescription = null,
                            tint = Color(0xFFFFD700), // 黃金色
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(Modifier.width(4.dp))
                        Text(
                            text = "%.1f".format(drama.grade),
                            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                }
                if (!drama.viewPoint.isNullOrBlank()) {
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = drama.viewPoint!!,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        lineHeight = 16.sp
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
                if (drama.tag != null || showShown) {
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.padding(top = 8.dp)
                    ) {
                        drama.tag?.split(",")?.filter { it.isNotBlank() }?.take(3)?.forEach { tag ->
                            FilterBadge(label = tag, color = MaterialTheme.colorScheme.primary)
                        }
                        if (showShown) {
                            FilterBadge(
                                label = if (drama.shown) "public" else "private",
                                color = if (drama.shown) MaterialTheme.colorScheme.secondary else Color.Gray
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun FilterBadge(label: String, color: Color) {
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
