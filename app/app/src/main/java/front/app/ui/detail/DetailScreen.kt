package front.app.ui.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import front.app.model.Drama
import front.app.model.Tag
import front.app.ui.add.TagSelectDialog
import front.app.ui.theme.*
import front.app.viewmodel.DramaViewModel
import coil.compose.AsyncImage

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun DetailScreen(
    title: String,
    userId: Long, // 劇集的擁有者 ID
    onBack: () -> Unit = {},
    viewModel: DramaViewModel = viewModel(),
    currentLoginUserId: Long = -1L // 目前登入者的 ID
) {
    val uriHandler = LocalUriHandler.current
    val dramaState by viewModel.currentDrama.collectAsState()
    val isDetailLoading by viewModel.isDetailLoading.collectAsState()
    val hasFetchedDetail by viewModel.hasFetchedDetail.collectAsState()
    val backendTags by viewModel.tags.collectAsState()
    val commonTags = remember { dummyTags.filter { it != "全部" } }
    val displayTags = remember(backendTags) {
        val backendTagNames = backendTags.map { tag: Tag -> tag.tagName }
        (commonTags + backendTagNames).distinct()
    }

    // 是否為自己的劇集
    val isMine = userId == currentLoginUserId

    var isEditing by remember { mutableStateOf(false) }

    // 編輯狀態
    var editTitle by remember { mutableStateOf("") }
    var editGrade by remember { mutableStateOf(0f) }
    var editViewPoint by remember { mutableStateOf("") }
    var editShown by remember { mutableStateOf(true) }
    var editLinks by remember { mutableStateOf(listOf("")) }
    var editActors by remember { mutableStateOf(listOf("")) }
    var editSelectedTags by remember { mutableStateOf(setOf<String>()) }
    var editCategory by remember { mutableStateOf("長劇") }
    var editCustomCategory by remember { mutableStateOf("") }
    val backendCategories by viewModel.categories.collectAsState()
    val defaultCategories = remember(backendCategories) {
        val names = backendCategories.map { it.name }
        if (names.isEmpty()) listOf("長劇", "短劇", "綜藝", "其他")
        else names + "其他"
    }

    var showTagSelectDialog by remember { mutableStateOf(false) }
    var showDeleteConfirmDialog by remember { mutableStateOf(false) }
    var isSaving by remember { mutableStateOf(false) }

    LaunchedEffect(title, userId) {
        viewModel.fetchDrama(title, userId)
        viewModel.fetchTags(userId)
        viewModel.fetchCategories(userId)
    }

    LaunchedEffect(dramaState) {
        dramaState?.let { drama ->
            editTitle = drama.title
            editGrade = drama.grade ?: 0f
            editViewPoint = drama.viewPoint ?: ""
            editShown = drama.shown
            editLinks = listOfNotNull(drama.link1, drama.link2, drama.link3).filter { it.isNotBlank() }.ifEmpty { listOf("") }
            editActors = drama.actors?.split(",")?.filter { it.isNotBlank() }?.ifEmpty { listOf("") } ?: listOf("")
            editSelectedTags = drama.tag?.split(",")?.filter { it.isNotBlank() }?.toSet() ?: emptySet()
            
            val cat = drama.category ?: "長劇"
            val baseCatNames = backendCategories.map { it.name }.ifEmpty { listOf("長劇", "短劇", "綜藝") }
            if (cat in baseCatNames) {
                editCategory = cat
                editCustomCategory = ""
            } else {
                editCategory = "其他"
                editCustomCategory = cat
            }
        }
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
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent),
                    title = { 
                        Text(
                            text = if (isEditing) "編輯劇集" else dramaState?.title ?: "詳情",
                            fontWeight = FontWeight.Bold
                        ) 
                    },
                    navigationIcon = {
                        IconButton(onClick = {
                            if (isEditing) isEditing = false else onBack()
                        }) {
                            Icon(Icons.Outlined.ArrowBack, contentDescription = "返回")
                        }
                    },
                    actions = {
                        if (dramaState != null && isMine) {
                                    if (isEditing) {
                                        TextButton(
                                            onClick = {
                                                val roundedGrade = if (editGrade == 0f) null else (Math.round(editGrade * 10) / 10f)
                                                isSaving = true
                                                val finalCategory = if (editCategory == "其他") editCustomCategory else editCategory
                                                val updatedDrama = Drama(
                                                    title = editTitle,
                                                    userId = userId,
                                                    actors = editActors.filter { it.isNotBlank() }.joinToString(","),
                                                    tag = if (editSelectedTags.isEmpty()) null else editSelectedTags.joinToString(","),
                                                    shown = editShown,
                                                    grade = roundedGrade,
                                                    viewPoint = editViewPoint,
                                                    link1 = editLinks.getOrNull(0),
                                                    link2 = editLinks.getOrNull(1),
                                                    link3 = editLinks.getOrNull(2),
                                                    posterPath = dramaState?.posterPath,
                                                    category = finalCategory
                                                )

                                                val newCat = if (editCategory == "其他" && editCustomCategory.isNotBlank() && backendCategories.none { it.name == editCustomCategory }) {
                                                    front.app.model.Category(userId = userId, name = editCustomCategory)
                                                } else null

                                                viewModel.saveDrama(updatedDrama, newCategory = newCat) {
                                                    isSaving = false
                                                    isEditing = false
                                                }
                                            },
                                            enabled = editTitle.isNotBlank() && !isSaving
                                        ) { 
                                            if (isSaving) {
                                                CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                                            } else {
                                                Text("儲存", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary) 
                                            }
                                        }
                                    } else {
                                IconButton(onClick = { isEditing = true }) {
                                    Icon(Icons.Outlined.Edit, contentDescription = "編輯")
                                }
                                IconButton(onClick = { showDeleteConfirmDialog = true }) {
                                    Icon(Icons.Outlined.Delete, contentDescription = "刪除", tint = MaterialTheme.colorScheme.error)
                                }
                            }
                        }
                    }
                )
            }
        ) { innerPadding ->
            if (dramaState != null) {
                val drama = dramaState!!
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentPadding = PaddingValues(bottom = 40.dp)
                ) {
                    // ── 海報與基本資訊 ──
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp, vertical = 8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Card(
                                shape = RoundedCornerShape(24.dp),
                                modifier = Modifier
                                    .width(200.dp)
                                    .height(280.dp)
                                    .border(getGlassBorder(), RoundedCornerShape(24.dp)),
                                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                            ) {
                                AsyncImage(
                                    model = if (drama.posterPath != null) "https://image.tmdb.org/t/p/w500${drama.posterPath}" else null,
                                    contentDescription = null,
                                    modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.surfaceVariant),
                                    contentScale = ContentScale.Crop
                                )
                            }
                            
                            if (!isEditing) {
                                Spacer(Modifier.height(24.dp))
                                Text(
                                    text = drama.title,
                                    style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onBackground
                                )
                                if (drama.grade != null) {
                                    Spacer(Modifier.height(8.dp))
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Filled.Star, contentDescription = null, tint = Color(0xFFFFD700), modifier = Modifier.size(20.dp))
                                        Spacer(Modifier.width(6.dp))
                                        Text(
                                            text = "%.1f".format(drama.grade),
                                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // ── 詳情卡片 ──
                    item {
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp)
                                .border(getGlassBorder(), RoundedCornerShape(24.dp)),
                            shape = RoundedCornerShape(24.dp),
                            color = getGlassBackground(0.5f),
                            tonalElevation = 2.dp
                        ) {
                            Column(
                                modifier = Modifier.padding(24.dp),
                                verticalArrangement = Arrangement.spacedBy(24.dp)
                            ) {
                                if (isEditing) {
                                    // 編輯模式的欄位
                                    OutlinedTextField(
                                        value = editTitle,
                                        onValueChange = { editTitle = it },
                                        label = { Text("劇名 *") },
                                        singleLine = true,
                                        shape = RoundedCornerShape(12.dp),
                                        modifier = Modifier.fillMaxWidth()
                                    )

                                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                        Text("Tag", style = MaterialTheme.typography.titleSmall)
                                        FlowRow(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                                            verticalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            editSelectedTags.forEach { tagName ->
                                                InputChip(
                                                    selected = true,
                                                    onClick = { editSelectedTags = editSelectedTags - tagName },
                                                    label = { Text(tagName) },
                                                    trailingIcon = { Icon(Icons.Outlined.Close, null, modifier = Modifier.size(16.dp)) }
                                                )
                                            }
                                            AssistChip(
                                                onClick = { showTagSelectDialog = true },
                                                label = { Text("編輯 Tag") },
                                                leadingIcon = { Icon(Icons.Outlined.Add, null, modifier = Modifier.size(16.dp)) }
                                            )
                                        }
                                    }

                                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                        Text("分類", style = MaterialTheme.typography.titleSmall)
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .horizontalScroll(rememberScrollState()),
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            defaultCategories.forEach { cat ->
                                                FilterChip(
                                                    selected = editCategory == cat,
                                                    onClick = { editCategory = cat },
                                                    label = { Text(cat) }
                                                )
                                            }
                                        }
                                        if (editCategory == "其他") {
                                            OutlinedTextField(
                                                value = editCustomCategory,
                                                onValueChange = { editCustomCategory = it },
                                                label = { Text("自訂分類名稱") },
                                                singleLine = true,
                                                shape = RoundedCornerShape(12.dp),
                                                modifier = Modifier.fillMaxWidth()
                                            )
                                        }
                                    }

                                    Column {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text("評分", style = MaterialTheme.typography.titleSmall)
                                            Text("%.1f".format(editGrade), color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                                        }
                                        Slider(value = editGrade, onValueChange = { editGrade = it }, valueRange = 0f..10f, steps = 99)
                                    }

                                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                        Text("演員", style = MaterialTheme.typography.titleSmall)
                                        editActors.forEachIndexed { index, actor ->
                                            OutlinedTextField(
                                                value = actor,
                                                onValueChange = { newVal -> editActors = editActors.toMutableList().also { it[index] = newVal } },
                                                label = { Text("演員 ${index + 1}") },
                                                singleLine = true,
                                                trailingIcon = {
                                                    if (editActors.size > 1) {
                                                        IconButton(onClick = { editActors = editActors.toMutableList().also { it.removeAt(index) } }) {
                                                            Icon(Icons.Outlined.RemoveCircleOutline, null, tint = MaterialTheme.colorScheme.error)
                                                        }
                                                    }
                                                },
                                                modifier = Modifier.fillMaxWidth()
                                            )
                                        }
                                        if (editActors.size < 5) {
                                            TextButton(onClick = { editActors = editActors + "" }) {
                                                Icon(Icons.Outlined.Add, null, modifier = Modifier.size(16.dp))
                                                Spacer(Modifier.width(4.dp))
                                                Text("新增演員")
                                            }
                                        }
                                    }

                                    OutlinedTextField(
                                        value = editViewPoint,
                                        onValueChange = { editViewPoint = it },
                                        label = { Text("觀後感") },
                                        minLines = 3,
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                    
                                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                        Text("相關連結", style = MaterialTheme.typography.titleSmall)
                                        editLinks.forEachIndexed { index, link ->
                                            OutlinedTextField(
                                                value = link,
                                                onValueChange = { newVal -> editLinks = editLinks.toMutableList().also { it[index] = newVal } },
                                                label = { Text("連結 ${index + 1}") },
                                                singleLine = true,
                                                modifier = Modifier.fillMaxWidth()
                                            )
                                        }
                                        if (editLinks.size < 3) {
                                            TextButton(onClick = { editLinks = editLinks + "" }) {
                                                Icon(Icons.Outlined.Add, null, modifier = Modifier.size(16.dp))
                                                Spacer(Modifier.width(4.dp))
                                                Text("新增連結")
                                            }
                                        }
                                    }
                                } else {
                                    // 瀏覽模式
                                    if (!drama.category.isNullOrBlank()) {
                                        DetailSection(label = "分類") {
                                            FilterBadge(label = drama.category, color = MaterialTheme.colorScheme.secondary)
                                        }
                                    }

                                    if (!drama.tag.isNullOrBlank()) {
                                        DetailSection(label = "分類標籤") {
                                            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                                drama.tag.split(",").forEach { tag ->
                                                    FilterBadge(label = tag.trim(), color = MaterialTheme.colorScheme.primary)
                                                }
                                            }
                                        }
                                    }

                                    if (!drama.actors.isNullOrBlank()) {
                                        DetailSection(label = "主演陣容") {
                                            Text(drama.actors.replace(",", " 、 "), style = MaterialTheme.typography.bodyLarge)
                                        }
                                    }

                                    DetailSection(label = "觀後感 / 心得") {
                                        Text(
                                            text = drama.viewPoint?.ifBlank { "尚未填寫心得" } ?: "尚未填寫心得",
                                            style = MaterialTheme.typography.bodyLarge,
                                            lineHeight = 24.sp
                                        )
                                    }

                                    val links = listOfNotNull(drama.link1, drama.link2, drama.link3).filter { it.isNotBlank() }
                                    if (links.isNotEmpty()) {
                                        DetailSection(label = "相關連結") {
                                            links.forEach { link ->
                                                Surface(
                                                    onClick = {
                                                        val trimmed = link.trim()
                                                        if (trimmed.isNotBlank()) {
                                                            try {
                                                                // 使用正則表達式尋找第一個網址
                                                                val urlRegex = "(https?://[^\\s]+)".toRegex()
                                                                val matchResult = urlRegex.find(trimmed)
                                                                
                                                                val urlToOpen = if (matchResult != null) {
                                                                    matchResult.value
                                                                } else {
                                                                    // 如果沒找到 http，嘗試原本的補齊邏輯
                                                                    if (trimmed.startsWith("http://") || trimmed.startsWith("https://")) {
                                                                        trimmed
                                                                    } else {
                                                                        "https://$trimmed"
                                                                    }
                                                                }
                                                                uriHandler.openUri(urlToOpen)
                                                            } catch (e: Exception) {}
                                                        }
                                                    },
                                                    shape = RoundedCornerShape(12.dp),
                                                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.05f),
                                                    modifier = Modifier.fillMaxWidth()
                                                ) {
                                                    Row(
                                                        modifier = Modifier.padding(12.dp),
                                                        verticalAlignment = Alignment.CenterVertically
                                                    ) {
                                                        Icon(Icons.Outlined.Link, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
                                                        Spacer(Modifier.width(12.dp))
                                                        Text(link, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.primary, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                                    }
                                                }
                                                Spacer(Modifier.height(8.dp))
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            } else if (isDetailLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(strokeWidth = 3.dp)
                }
            } else if (hasFetchedDetail) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("找不到資料", style = MaterialTheme.typography.bodyLarge)
                }
            }
        }
    }

    if (showDeleteConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmDialog = false },
            title = { Text("刪除劇集") },
            text = { Text("確定要從清單中移除「${dramaState?.title}」嗎？此動作無法復原。") },
            confirmButton = {
                Button(
                    onClick = {
                        showDeleteConfirmDialog = false
                        viewModel.deleteDrama(title, userId) { onBack() }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) { Text("確定刪除") }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirmDialog = false }) { Text("取消") }
            }
        )
    }

    if (showTagSelectDialog) {
        TagSelectDialog(
            allTags = displayTags,
            selectedTags = editSelectedTags,
            onDismiss = { showTagSelectDialog = false },
            onConfirm = {
                editSelectedTags = it
                showTagSelectDialog = false
            },
            onAddNewTag = { tagName ->
                if (displayTags.none { it == tagName }) {
                    viewModel.saveTag(Tag(userId = userId, tagName = tagName))
                }
            }
        )
    }
}

@Composable
private fun DetailSection(label: String, content: @Composable () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold
        )
        content()
    }
}

@Composable
private fun FilterBadge(label: String, color: Color) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = color.copy(alpha = 0.1f),
        border = androidx.compose.foundation.BorderStroke(0.5.dp, color.copy(alpha = 0.2f))
    ) {
        Text(
            text = label,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            fontSize = 12.sp,
            color = color,
            fontWeight = FontWeight.Bold
        )
    }
}
