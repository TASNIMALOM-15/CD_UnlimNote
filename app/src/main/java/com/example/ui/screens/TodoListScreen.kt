package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.NoteEntity
import com.example.data.model.TodoItem
import org.json.JSONArray
import org.json.JSONObject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodoListScreen(
    initialNote: NoteEntity,
    onSaveNote: (NoteEntity) -> Unit,
    onBack: () -> Unit
) {
    var title by remember { mutableStateOf(initialNote.title.ifEmpty { "To-Do Checklist" }) }
    val tasks = remember {
        mutableStateListOf<TodoItem>().apply {
            addAll(parseTodoItems(initialNote.todoItemsJson))
        }
    }
    var newTaskText by remember { mutableStateOf("") }
    var selectedPriority by remember { mutableStateOf("Medium") }

    fun serializeTasks(): String {
        val array = JSONArray()
        for (t in tasks) {
            val obj = JSONObject()
            obj.put("id", t.id)
            obj.put("text", t.text)
            obj.put("isCompleted", t.isCompleted)
            obj.put("priority", t.priority)
            array.put(obj)
        }
        return array.toString()
    }

    fun saveChanges() {
        val json = serializeTasks()
        onSaveNote(
            initialNote.copy(
                title = title.trim(),
                isTodo = true,
                todoItemsJson = json,
                content = tasks.joinToString("\n") { (if (it.isCompleted) "[x] " else "[ ] ") + it.text },
                modifiedAt = System.currentTimeMillis()
            )
        )
    }

    BackHandler {
        saveChanges()
        onBack()
    }

    LaunchedEffect(title, tasks.size) {
        saveChanges()
    }

    val completedCount = tasks.count { it.isCompleted }
    val progress = if (tasks.isEmpty()) 0f else completedCount.toFloat() / tasks.size
    val animatedProgress by animateFloatAsState(targetValue = progress, label = "progress")

    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = {
                        saveChanges()
                        onBack()
                    }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                title = {
                    BasicTextField(
                        value = title,
                        onValueChange = { title = it },
                        singleLine = true,
                        textStyle = TextStyle(
                            fontSize = 18.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onBackground
                        ),
                        cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                        modifier = Modifier.fillMaxWidth()
                    )
                },
                actions = {
                    IconButton(onClick = {
                        // Clear completed
                        tasks.removeAll { it.isCompleted }
                        saveChanges()
                    }) {
                        Icon(Icons.Default.ClearAll, contentDescription = "Clear completed")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        bottomBar = {
            // Quick task bar
            Surface(
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 6.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = newTaskText,
                        onValueChange = { newTaskText = it },
                        placeholder = { Text("Add new task...") },
                        singleLine = true,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(20.dp)
                    )

                    IconButton(
                        onClick = {
                            if (newTaskText.isNotBlank()) {
                                tasks.add(
                                    0,
                                    TodoItem(
                                        text = newTaskText.trim(),
                                        priority = selectedPriority
                                    )
                                )
                                newTaskText = ""
                                saveChanges()
                            }
                        },
                        colors = IconButtonDefaults.filledIconButtonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Add", tint = Color.White)
                    }
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ) {
            // Progress Bar & Count
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Task Progress",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "$completedCount of ${tasks.size} done (${(progress * 100).toInt()}%)",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    LinearProgressIndicator(
                        progress = { animatedProgress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp),
                    )
                }
            }

            // Tasks List
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(tasks, key = { it.id }) { item ->
                    val index = tasks.indexOf(item)
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (item.isCompleted) MaterialTheme.colorScheme.surface.copy(alpha = 0.6f)
                            else MaterialTheme.colorScheme.surface
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = item.isCompleted,
                                onCheckedChange = { checked ->
                                    tasks[index] = item.copy(isCompleted = checked)
                                    // Optionally reorder completed to bottom
                                    if (checked) {
                                        val finished = tasks.removeAt(index)
                                        tasks.add(finished)
                                    }
                                    saveChanges()
                                }
                            )

                            Text(
                                text = item.text,
                                fontSize = 15.sp,
                                textDecoration = if (item.isCompleted) TextDecoration.LineThrough else TextDecoration.None,
                                color = if (item.isCompleted) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f) else MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(horizontal = 8.dp)
                            )

                            IconButton(
                                onClick = {
                                    tasks.removeAt(index)
                                    saveChanges()
                                }
                            ) {
                                Icon(
                                    Icons.Default.Close,
                                    contentDescription = "Delete",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun parseTodoItems(json: String): List<TodoItem> {
    if (json.isBlank()) return emptyList()
    val list = mutableListOf<TodoItem>()
    try {
        val array = JSONArray(json)
        for (i in 0 until array.length()) {
            val obj = array.getJSONObject(i)
            list.add(
                TodoItem(
                    id = obj.optString("id", java.util.UUID.randomUUID().toString()),
                    text = obj.optString("text", ""),
                    isCompleted = obj.optBoolean("isCompleted", false),
                    priority = obj.optString("priority", "Medium")
                )
            )
        }
    } catch (e: Exception) {
        // Fallback for empty/plain text
    }
    return list
}
