package front.app.ui.add

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import front.app.model.Drama
import front.app.model.Tag
import front.app.ui.home.dummyTags
import front.app.viewmodel.DramaViewModel

@OptIn(ExperimentalMaterial3Api::class)
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

    // tag 相關
    val backendTags by viewModel.tags.collectAsState()
    val commonTags = remember { dummyTags.filter { it != "全部" } }
    val displayTags = (commonTags + backendTags.map { it.tagName }).distinct()

    var selectedTag by remember { mutableStateOf("") }
    var tagDropdownExpanded by remember { mutableStateOf(false) }
    var showAddTagDialog by remember { mutableStateOf(false) }
    var newTagInput by remember { mutableStateOf("") }

    LaunchedEffect(userId) {
        viewModel.fetchTags(userId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("新增觀影紀錄") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Outlined.ArrowBack, contentDescription = "返回")
                    }
                },
                actions = {
                    TextButton(
                        onClick = {
                            val drama = Drama(
                                title = title,
                                userId = userId,
                                actors = actors.filter { it.isNotBlank() }.joinToString(","),
                                tag = selectedTag,
                                shown = shown,
                                grade = if (grade == 0f) null else grade,
                                viewPoint = viewPoint,
                                link1 = links.getOrNull(0),
                                link2 = links.getOrNull(1),
                                link3 = links.getOrNull(2)
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
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("劇名 *") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // ── Tag ──
            item {
                if (showAddTagDialog) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = newTagInput,
                            onValueChange = { newTagInput = it },
                            label = { Text("新增 Tag") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                        TextButton(onClick = {
                            showAddTagDialog = false
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
                                    selectedTag = newTagInput
                                }
                                showAddTagDialog = false
                                newTagInput = ""
                            },
                            enabled = newTagInput.isNotBlank()
                        ) {
                            Text("確認")
                        }
                    }
                } else {
                        ExposedDropdownMenuBox(
                            expanded = tagDropdownExpanded,
                            onExpandedChange = { tagDropdownExpanded = it }
                        ) {
                            OutlinedTextField(
                                value = selectedTag,
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Tag") },
                                trailingIcon = {
                                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = tagDropdownExpanded)
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .menuAnchor()
                            )
                            ExposedDropdownMenu(
                                expanded = tagDropdownExpanded,
                                onDismissRequest = { tagDropdownExpanded = false }
                            ) {
                                displayTags.forEach { tagName ->
                                    DropdownMenuItem(
                                        text = { Text(tagName) },
                                        onClick = {
                                            selectedTag = tagName
                                            tagDropdownExpanded = false
                                        }
                                    )
                                }
                            HorizontalDivider()
                            DropdownMenuItem(
                                text = {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            Icons.Outlined.Add,
                                            contentDescription = null,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(Modifier.width(4.dp))
                                        Text("新增 tag")
                                    }
                                },
                                onClick = {
                                    tagDropdownExpanded = false
                                    showAddTagDialog = true
                                }
                            )
                        }
                    }
                }
            }

            // ── 演員（動態新增，最多五個）──
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
                                    Icon(Icons.Outlined.Close, contentDescription = "移除",
                                        tint = MaterialTheme.colorScheme.error)
                                }
                            }
                        }
                    }
                    if (actors.size < 5) {
                        TextButton(
                            onClick = { actors = actors + "" },
                            modifier = Modifier.align(Alignment.Start)
                        ) {
                            Icon(Icons.Outlined.Add, contentDescription = null,
                                modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(4.dp))
                            Text("新增演員")
                        }
                    }
                }
            }

            // ── 評分滑桿 ──
            item {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "評分",
                            style = MaterialTheme.typography.bodyMedium
                        )
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
                        steps = 99,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("0", style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("10", style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant)
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

            // ── 連結（動態新增，最多三個）──
            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        "相關連結",
                        style = MaterialTheme.typography.bodyMedium
                    )
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
                                        contentDescription = "移除連結",
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
                            Icon(
                                Icons.Outlined.Add,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(Modifier.width(4.dp))
                            Text("新增連結")
                        }
                    }
                }
            }

            // ── Public / Private toggle ──
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
}