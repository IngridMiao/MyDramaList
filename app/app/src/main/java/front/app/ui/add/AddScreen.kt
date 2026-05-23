package front.app.ui.add

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import front.app.model.Drama
import front.app.model.Tag
import front.app.ui.theme.*
import front.app.viewmodel.DramaViewModel
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AddScreen(
    userId: Long,
    onBack: () -> Unit = {},
    onSave: () -> Unit = {},
    viewModel: DramaViewModel = viewModel()
) {
    var title by remember { mutableStateOf("") }
    var grade by remember { mutableStateOf(0f) }
    var actors by remember { mutableStateOf(listOf("")) }
    var viewPoint by remember { mutableStateOf("") }
    var shown by remember { mutableStateOf(true) }
    var links by remember { mutableStateOf(listOf("")) }
    var posterPath by remember { mutableStateOf<String?>(null) }

    val suggestions by viewModel.suggestions.collectAsState()
    var showSuggestions by remember { mutableStateOf(false) }

    // 搜尋防抖 (Debounce)
    LaunchedEffect(title) {
        if (title.length >= 2) {
            delay(500)
            viewModel.searchTmdb(title)
            showSuggestions = true
        } else {
            viewModel.clearSuggestions()
            showSuggestions = false
        }
    }

    // tag 相關
    val backendTags by viewModel.tags.collectAsState()
    val commonTags = remember { dummyTags.filter { it != "全部" } }
    val displayTags = remember(backendTags) {
        val backendTagNames = backendTags.map { tag: Tag -> tag.tagName }
        (commonTags + backendTagNames).distinct()
    }

    var selectedTags by remember { mutableStateOf(setOf<String>()) }
    var showTagSelectDialog by remember { mutableStateOf(false) }

    LaunchedEffect(userId) {
        viewModel.fetchTags(userId)
    }

    Box(modifier = Modifier
        .fillMaxSize()
        .background(
            androidx.compose.ui.graphics.Brush.verticalGradient(
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
                    title = { Text("新增影劇", fontWeight = FontWeight.Bold) },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(Icons.Outlined.ArrowBack, contentDescription = "返回")
                        }
                    },
                    actions = {
                        TextButton(
                            onClick = {
                                val roundedGrade = if (grade == 0f) null else (Math.round(grade * 10) / 10f)
                                val drama = Drama(
                                    title = title,
                                    userId = userId,
                                    actors = actors.filter { it.isNotBlank() }.joinToString(","),
                                    tag = if (selectedTags.isEmpty()) null else selectedTags.joinToString(","),
                                    shown = shown,
                                    grade = roundedGrade,
                                    viewPoint = viewPoint,
                                    link1 = links.getOrNull(0),
                                    link2 = links.getOrNull(1),
                                    link3 = links.getOrNull(2),
                                    posterPath = posterPath
                                )
                                viewModel.saveDrama(drama) {
                                    onSave()
                                }
                            },
                            enabled = title.isNotBlank()
                        ) {
                            Text("儲存", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                        }
                    }
                )
            }
        ) { innerPadding ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentPadding = PaddingValues(start = 20.dp, end = 20.dp, bottom = 40.dp)
            ) {
                item {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp)
                            .border(getGlassBorder(), RoundedCornerShape(24.dp)),
                        shape = RoundedCornerShape(24.dp),
                        color = getGlassBackground(0.5f),
                        tonalElevation = 2.dp
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            verticalArrangement = Arrangement.spacedBy(24.dp)
                        ) {
                            // ── 劇名與建議 ──
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Box(modifier = Modifier.fillMaxWidth()) {
                                    OutlinedTextField(
                                        value = title,
                                        onValueChange = { title = it },
                                        label = { Text("劇名 *") },
                                        singleLine = true,
                                        shape = RoundedCornerShape(12.dp),
                                        modifier = Modifier.fillMaxWidth()
                                    )

                                    if (showSuggestions && suggestions.isNotEmpty()) {
                                        Surface(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(top = 68.dp)
                                                .border(getGlassBorder(), RoundedCornerShape(16.dp)),
                                            shape = RoundedCornerShape(16.dp),
                                            tonalElevation = 8.dp,
                                            shadowElevation = 8.dp
                                        ) {
                                            Column {
                                                suggestions.forEach { suggestion ->
                                                    DropdownMenuItem(
                                                        text = {
                                                            Column {
                                                                Text(suggestion.title, fontWeight = FontWeight.Bold)
                                                                if (suggestion.actors.isNotEmpty()) {
                                                                    Text(
                                                                        "主演: ${suggestion.actors.joinToString(", ")}",
                                                                        style = MaterialTheme.typography.bodySmall,
                                                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                                                    )
                                                                }
                                                            }
                                                        },
                                                        onClick = {
                                                            title = suggestion.title
                                                            actors = suggestion.actors.ifEmpty { listOf("") }
                                                            posterPath = suggestion.posterPath
                                                            showSuggestions = false
                                                            viewModel.clearSuggestions()
                                                        }
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }

                            // ── Tag ──
                            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                Text("分類標籤", style = MaterialTheme.typography.titleSmall)
                                FlowRow(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    selectedTags.forEach { tagName ->
                                        InputChip(
                                            selected = true,
                                            onClick = { selectedTags = selectedTags - tagName },
                                            label = { Text(tagName) },
                                            trailingIcon = { Icon(Icons.Outlined.Close, null, modifier = Modifier.size(16.dp)) }
                                        )
                                    }
                                    AssistChip(
                                        onClick = { showTagSelectDialog = true },
                                        label = { Text("編輯標籤") },
                                        leadingIcon = { Icon(Icons.Outlined.Add, null, modifier = Modifier.size(16.dp)) }
                                    )
                                }
                            }

                            // ── 評分 ──
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("給個評分", style = MaterialTheme.typography.titleSmall)
                                    Text(
                                        text = if (grade == 0f) "未評分" else "%.1f".format(grade),
                                        color = MaterialTheme.colorScheme.primary,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Slider(
                                    value = grade,
                                    onValueChange = { grade = it },
                                    valueRange = 0f..10f,
                                    steps = 99
                                )
                            }

                            // ── 演員 ──
                            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                Text("主演陣容", style = MaterialTheme.typography.titleSmall)
                                actors.forEachIndexed { index, actor ->
                                    OutlinedTextField(
                                        value = actor,
                                        onValueChange = { newVal -> actors = actors.toMutableList().also { it[index] = newVal } },
                                        label = { Text("演員 ${index + 1}") },
                                        singleLine = true,
                                        shape = RoundedCornerShape(12.dp),
                                        trailingIcon = {
                                            if (actors.size > 1) {
                                                IconButton(onClick = { actors = actors.toMutableList().also { it.removeAt(index) } }) {
                                                    Icon(Icons.Outlined.RemoveCircleOutline, null, tint = MaterialTheme.colorScheme.error)
                                                }
                                            }
                                        },
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }
                                if (actors.size < 5) {
                                    TextButton(onClick = { actors = actors + "" }) {
                                        Icon(Icons.Outlined.Add, null, modifier = Modifier.size(16.dp))
                                        Spacer(Modifier.width(4.dp))
                                        Text("新增演員")
                                    }
                                }
                            }

                            // ── 觀後感 ──
                            OutlinedTextField(
                                value = viewPoint,
                                onValueChange = { viewPoint = it },
                                label = { Text("觀後感 / 心得") },
                                minLines = 3,
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth()
                            )

                            // ── 連結 ──
                            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                Text("相關連結 (如：預告、劇照)", style = MaterialTheme.typography.titleSmall)
                                links.forEachIndexed { index, link ->
                                    OutlinedTextField(
                                        value = link,
                                        onValueChange = { newVal -> links = links.toMutableList().also { it[index] = newVal } },
                                        label = { Text("網址 ${index + 1}") },
                                        singleLine = true,
                                        shape = RoundedCornerShape(12.dp),
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Uri),
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }
                                if (links.size < 3) {
                                    TextButton(onClick = { links = links + "" }) {
                                        Icon(Icons.Outlined.Add, null, modifier = Modifier.size(16.dp))
                                        Spacer(Modifier.width(4.dp))
                                        Text("新增網址")
                                    }
                                }
                            }

                            // ── 隱私設定 ──
                            Surface(
                                shape = RoundedCornerShape(16.dp),
                                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.05f),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column {
                                        Text("公開這部影劇", style = MaterialTheme.typography.labelLarge)
                                        Text(
                                            if (shown) "所有人可見" else "僅自己可見",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                    Switch(checked = shown, onCheckedChange = { shown = it })
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showTagSelectDialog) {
        TagSelectDialog(
            allTags = displayTags,
            selectedTags = selectedTags,
            onDismiss = { showTagSelectDialog = false },
            onConfirm = { 
                selectedTags = it
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TagSelectDialog(
    allTags: List<String>,
    selectedTags: Set<String>,
    onDismiss: () -> Unit,
    onConfirm: (Set<String>) -> Unit,
    onAddNewTag: (String) -> Unit
) {
    var tempSelected by remember { mutableStateOf(selectedTags) }
    var newTagInput by remember { mutableStateOf("") }
    var isAddingNew by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("選擇 Tag") },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 400.dp)
            ) {
                if (isAddingNew) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = newTagInput,
                            onValueChange = { newTagInput = it },
                            label = { Text("新 Tag") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                        IconButton(onClick = {
                            if (newTagInput.isNotBlank()) {
                                val trimmedTag = newTagInput.trim()
                                onAddNewTag(trimmedTag)
                                tempSelected = tempSelected + trimmedTag
                                newTagInput = ""
                                isAddingNew = false
                            }
                        }) {
                            Icon(Icons.Outlined.Check, contentDescription = "確認")
                        }
                        IconButton(onClick = { isAddingNew = false }) {
                            Icon(Icons.Outlined.Close, contentDescription = "取消")
                        }
                    }
                } else {
                    TextButton(onClick = { isAddingNew = true }) {
                        Icon(Icons.Outlined.Add, contentDescription = null)
                        Spacer(Modifier.width(4.dp))
                        Text("新增自定義 Tag")
                    }
                }
                
                Spacer(Modifier.height(8.dp))
                HorizontalDivider()
                Spacer(Modifier.height(8.dp))

                Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                    allTags.forEach { tag ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    tempSelected = if (tag in tempSelected) {
                                        tempSelected - tag
                                    } else {
                                        tempSelected + tag
                                    }
                                }
                                .padding(vertical = 8.dp)
                        ) {
                            Checkbox(
                                checked = tag in tempSelected,
                                onCheckedChange = { checked ->
                                    tempSelected = if (checked) {
                                        tempSelected + tag
                                    } else {
                                        tempSelected - tag
                                    }
                                }
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(tag)
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = { onConfirm(tempSelected) }) {
                Text("確定")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("取消")
            }
        }
    )
}
