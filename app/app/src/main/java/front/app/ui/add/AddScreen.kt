package front.app.ui.add

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import front.app.model.Drama
import front.app.model.Tag
import front.app.ui.home.dummyTags
import front.app.viewmodel.DramaViewModel

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
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
            delay(500) // 等待 500ms
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
        (commonTags + backendTags.map { it.tagName }).distinct()
    }

    var selectedTags by remember { mutableStateOf(setOf<String>()) }
    var showTagSelectDialog by remember { mutableStateOf(false) }

    LaunchedEffect(userId) {
        viewModel.fetchTags(userId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("新增劇集") },
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
                        Text("儲存")
                    }
                }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
            contentPadding = PaddingValues(vertical = 20.dp)
        ) {
            // ── 標題 ──
            item {
                Box(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("劇名 *") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    if (showSuggestions && suggestions.isNotEmpty()) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 64.dp), // 放在輸入框下面
                            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
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
            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Tag", style = MaterialTheme.typography.bodyMedium)
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
                                trailingIcon = {
                                    Icon(
                                        Icons.Outlined.Close,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            )
                        }
                        AssistChip(
                            onClick = { showTagSelectDialog = true },
                            label = { Text("選擇 / 新增 Tag") },
                            leadingIcon = {
                                Icon(Icons.Outlined.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                            }
                        )
                    }
                }
            }

            // ── 評分 ──
            item {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("評分", style = MaterialTheme.typography.bodyMedium)
                        Text(
                            text = if (grade == 0f) "未評分" else "%.1f".format(grade),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    Slider(
                        value = grade,
                        onValueChange = { grade = it },
                        valueRange = 0f..10f,
                        steps = 99, // 0.1 步長
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            // ── 演員 ──
            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("演員", style = MaterialTheme.typography.bodyMedium)
                    actors.forEachIndexed { index, actor ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            OutlinedTextField(
                                value = actor,
                                onValueChange = { newVal ->
                                    actors = actors.toMutableList().also { it[index] = newVal }
                                },
                                label = { Text("演員 ${index + 1}") },
                                singleLine = true,
                                modifier = Modifier.weight(1f)
                            )
                            if (actors.size > 1) {
                                IconButton(onClick = {
                                    actors = actors.toMutableList().also { it.removeAt(index) }
                                }) {
                                    Icon(
                                        Icons.Outlined.Close,
                                        contentDescription = "移除",
                                        tint = MaterialTheme.colorScheme.error
                                    )
                                }
                            }
                        }
                    }
                    if (actors.size < 5) {
                        TextButton(
                            onClick = { actors = actors + "" },
                            modifier = Modifier.align(Alignment.Start)
                        ) {
                            Icon(Icons.Outlined.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(4.dp))
                            Text("新增演員")
                        }
                    }
                }
            }

            // ── 觀後感 ──
            item {
                OutlinedTextField(
                    value = viewPoint,
                    onValueChange = { viewPoint = it },
                    label = { Text("觀後感") },
                    minLines = 3,
                    maxLines = 6,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // ── 連結 ──
            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("相關連結", style = MaterialTheme.typography.bodyMedium)
                    links.forEachIndexed { index, link ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            OutlinedTextField(
                                value = link,
                                onValueChange = { newVal ->
                                    links = links.toMutableList().also { it[index] = newVal }
                                },
                                label = { Text("連結 ${index + 1}") },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Uri),
                                modifier = Modifier.weight(1f)
                            )
                            if (links.size > 1) {
                                IconButton(onClick = {
                                    links = links.toMutableList().also { it.removeAt(index) }
                                }) {
                                    Icon(
                                        Icons.Outlined.Close,
                                        contentDescription = "移除",
                                        tint = MaterialTheme.colorScheme.error
                                    )
                                }
                            }
                        }
                    }
                    if (links.size < 3) {
                        TextButton(
                            onClick = { links = links + "" },
                            modifier = Modifier.align(Alignment.Start)
                        ) {
                            Icon(Icons.Outlined.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(4.dp))
                            Text("新增連結")
                        }
                    }
                }
            }

            // ── Public / Private ──
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("公開", style = MaterialTheme.typography.bodyMedium)
                        Text(
                            if (shown) "所有人可見" else "僅自己可見",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Switch(
                        checked = shown,
                        onCheckedChange = { shown = it }
                    )
                }
            }

            item { Spacer(Modifier.height(60.dp)) }
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
