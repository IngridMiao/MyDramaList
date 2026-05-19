package front.app.ui.detail

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import front.app.model.Drama
import front.app.ui.home.dummyTags
import front.app.viewmodel.DramaViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    title: String,
    userId: Long,
    onBack: () -> Unit = {},
    viewModel: DramaViewModel = viewModel()
) {
    val dramaState by viewModel.currentDrama.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    var isEditing by remember { mutableStateOf(false) }

    // 編輯狀態
    var editTitle by remember { mutableStateOf("") }
    var editGrade by remember { mutableStateOf(0f) }
    var editViewPoint by remember { mutableStateOf("") }
    var editShown by remember { mutableStateOf(true) }
    var editLinks by remember { mutableStateOf(listOf("")) }
    var editActors by remember { mutableStateOf(listOf("")) }
    var editSelectedTag by remember { mutableStateOf("") }

    var tagDropdownExpanded by remember { mutableStateOf(false) }
    var tagList by remember { mutableStateOf(dummyTags.filter { it != "全部" }) }
    var showAddTagDialog by remember { mutableStateOf(false) }
    var newTagInput by remember { mutableStateOf("") }

    LaunchedEffect(title, userId) {
        viewModel.fetchDrama(title, userId)
    }

    LaunchedEffect(dramaState) {
        dramaState?.let { drama ->
            editTitle = drama.title
            editGrade = drama.grade ?: 0f
            editViewPoint = drama.viewPoint ?: ""
            editShown = drama.shown
            editLinks = listOfNotNull(drama.link1, drama.link2, drama.link3).ifEmpty { listOf("") }
            editActors = drama.actors?.split(",")?.filter { it.isNotBlank() }?.ifEmpty { listOf("") } ?: listOf("")
            editSelectedTag = drama.tag ?: ""
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (isEditing) "編輯" else dramaState?.title ?: "詳情") },
                navigationIcon = {
                    IconButton(onClick = {
                        if (isEditing) isEditing = false else onBack()
                    }) {
                        Icon(Icons.Outlined.ArrowBack, contentDescription = "返回")
                    }
                },
                actions = {
                    if (dramaState != null) {
                        if (isEditing) {
                            TextButton(
                                onClick = {
                                    val updatedDrama = Drama(
                                        title = editTitle,
                                        userId = userId,
                                        actors = editActors.filter { it.isNotBlank() }.joinToString(","),
                                        tag = editSelectedTag,
                                        shown = editShown,
                                        grade = if (editGrade == 0f) null else editGrade,
                                        viewPoint = editViewPoint,
                                        link1 = editLinks.getOrNull(0),
                                        link2 = editLinks.getOrNull(1),
                                        link3 = editLinks.getOrNull(2)
                                    )
                                    viewModel.saveDrama(updatedDrama) {
                                        viewModel.fetchDrama(editTitle, userId)
                                        isEditing = false
                                    }
                                },
                                enabled = editTitle.isNotBlank()
                            ) { Text("儲存") }
                        } else {
                            IconButton(onClick = { isEditing = true }) {
                                Icon(Icons.Outlined.Edit, contentDescription = "編輯")
                            }
                            IconButton(onClick = {
                                viewModel.deleteDrama(title, userId) {
                                    onBack()
                                }
                            }) {
                                Icon(Icons.Outlined.Delete, contentDescription = "刪除", tint = MaterialTheme.colorScheme.error)
                            }
                        }
                    }
                }
            )
        }
    ) { innerPadding ->
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (dramaState == null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("找不到資料")
            }
        } else {
            val drama = dramaState!!
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
                    if (isEditing) {
                        OutlinedTextField(
                            value = editTitle,
                            onValueChange = { editTitle = it },
                            label = { Text("劇名 *") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                    } else {
                        DetailRow(label = "劇名", value = drama.title)
                    }
                }

                // ── Tag ──
                item {
                    if (isEditing) {
                        ExposedDropdownMenuBox(
                            expanded = tagDropdownExpanded,
                            onExpandedChange = { tagDropdownExpanded = it }
                        ) {
                            OutlinedTextField(
                                value = editSelectedTag,
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
                                tagList.forEach { tag ->
                                    DropdownMenuItem(
                                        text = { Text(tag) },
                                        onClick = {
                                            editSelectedTag = tag
                                            tagDropdownExpanded = false
                                        }
                                    )
                                }
                                HorizontalDivider()
                                DropdownMenuItem(
                                    text = {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(Icons.Outlined.Add, contentDescription = null,
                                                modifier = Modifier.size(16.dp))
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
                    } else {
                        DetailRow(label = "Tag", value = drama.tag ?: "—")
                    }
                }

                // ── 評分 ──
                item {
                    if (isEditing) {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("評分", style = MaterialTheme.typography.bodyMedium)
                                Text(
                                    text = if (editGrade == 0f) "未評分" else "%.1f".format(editGrade),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                            Slider(
                                value = editGrade,
                                onValueChange = { editGrade = it },
                                valueRange = 0f..10f,
                                steps = 99,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    } else {
                        DetailRow(
                            label = "評分",
                            value = if (drama.grade == null) "未評分" else "%.1f".format(drama.grade)
                        )
                    }
                }

                // ── 演員 ──
                item {
                    if (isEditing) {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text("演員", style = MaterialTheme.typography.bodyMedium)
                            editActors.forEachIndexed { index, actor ->
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    OutlinedTextField(
                                        value = actor,
                                        onValueChange = { newVal ->
                                            editActors = editActors.toMutableList().also { it[index] = newVal }
                                        },
                                        label = { Text("演員 ${index + 1}") },
                                        singleLine = true,
                                        modifier = Modifier.weight(1f)
                                    )
                                    if (editActors.size > 1) {
                                        IconButton(onClick = {
                                            editActors = editActors.toMutableList().also { it.removeAt(index) }
                                        }) {
                                            Icon(Icons.Outlined.Close, contentDescription = "移除",
                                                tint = MaterialTheme.colorScheme.error)
                                        }
                                    }
                                }
                            }
                            if (editActors.size < 5) {
                                TextButton(
                                    onClick = { editActors = editActors + "" },
                                    modifier = Modifier.align(Alignment.Start)
                                ) {
                                    Icon(Icons.Outlined.Add, contentDescription = null,
                                        modifier = Modifier.size(16.dp))
                                    Spacer(Modifier.width(4.dp))
                                    Text("新增演員")
                                }
                            }
                        }
                    } else {
                        DetailRow(
                            label = "演員",
                            value = drama.actors?.replace(",", "、") ?: "—"
                        )
                    }
                }

                // ── 觀後感 ──
                item {
                    if (isEditing) {
                        OutlinedTextField(
                            value = editViewPoint,
                            onValueChange = { editViewPoint = it },
                            label = { Text("觀後感") },
                            minLines = 3,
                            maxLines = 6,
                            modifier = Modifier.fillMaxWidth()
                        )
                    } else {
                        DetailRow(label = "觀後感", value = drama.viewPoint?.ifBlank { "—" } ?: "—")
                    }
                }

                // ── 連結 ──
                item {
                    if (isEditing) {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text("相關連結", style = MaterialTheme.typography.bodyMedium)
                            editLinks.forEachIndexed { index, link ->
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    OutlinedTextField(
                                        value = link,
                                        onValueChange = { newVal ->
                                            editLinks = editLinks.toMutableList().also { it[index] = newVal }
                                        },
                                        label = { Text("連結 ${index + 1}") },
                                        singleLine = true,
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Uri),
                                        modifier = Modifier.weight(1f)
                                    )
                                    if (editLinks.size > 1) {
                                        IconButton(onClick = {
                                            editLinks = editLinks.toMutableList().also { it.removeAt(index) }
                                        }) {
                                            Icon(Icons.Outlined.Close, contentDescription = "移除",
                                                tint = MaterialTheme.colorScheme.error)
                                        }
                                    }
                                }
                            }
                            if (editLinks.size < 3) {
                                TextButton(
                                    onClick = { editLinks = editLinks + "" },
                                    modifier = Modifier.align(Alignment.Start)
                                ) {
                                    Icon(Icons.Outlined.Add, contentDescription = null,
                                        modifier = Modifier.size(16.dp))
                                    Spacer(Modifier.width(4.dp))
                                    Text("新增連結")
                                }
                            }
                        }
                    } else {
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text("相關連結", style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant)
                            val links = listOfNotNull(drama.link1, drama.link2, drama.link3).filter { it.isNotBlank() }
                            if (links.isEmpty()) {
                                Text("—", style = MaterialTheme.typography.bodyMedium)
                            } else {
                                links.forEach { link ->
                                    Text(
                                        text = link,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                        }
                    }
                }

                // ── Public / Private ──
                item {
                    if (isEditing) {
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
                                    if (editShown) "所有人可見" else "僅自己可見",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Switch(checked = editShown, onCheckedChange = { editShown = it })
                        }
                    } else {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                shape = RoundedCornerShape(20.dp),
                                color = (if (drama.shown) Color(0xFF2196F3)
                                else Color(0xFF9C27B0)).copy(alpha = 0.12f)
                            ) {
                                Text(
                                    text = if (drama.shown) "public" else "private",
                                    color = if (drama.shown) Color(0xFF2196F3) else Color(0xFF9C27B0),
                                    style = MaterialTheme.typography.bodySmall,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }
                }

                item { Spacer(Modifier.height(60.dp)) }
            }
        }
    }

    if (showAddTagDialog) {
        AlertDialog(
            onDismissRequest = { showAddTagDialog = false; newTagInput = "" },
            title = { Text("新增 Tag") },
            text = {
                OutlinedTextField(
                    value = newTagInput,
                    onValueChange = { newTagInput = it },
                    label = { Text("Tag 名稱") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (newTagInput.isNotBlank() && !tagList.contains(newTagInput)) {
                            tagList = tagList + newTagInput
                        }
                        editSelectedTag = newTagInput
                        showAddTagDialog = false
                        newTagInput = ""
                    },
                    enabled = newTagInput.isNotBlank()
                ) { Text("確認") }
            },
            dismissButton = {
                TextButton(onClick = { showAddTagDialog = false; newTagInput = "" }) {
                    Text("取消")
                }
            }
        )
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(text = value, style = MaterialTheme.typography.bodyMedium)
    }
}
