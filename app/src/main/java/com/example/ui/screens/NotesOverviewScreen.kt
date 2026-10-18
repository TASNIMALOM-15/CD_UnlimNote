package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.NoteEntity
import com.example.ui.components.BottomDock
import com.example.ui.components.ColorPickerDialog
import com.example.ui.components.NoteCard
import com.example.ui.components.SecurityPinDialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotesOverviewScreen(
    title: String,
    notes: List<NoteEntity>,
    isListView: Boolean,
    onToggleListView: () -> Unit,
    onNoteClick: (NoteEntity) -> Unit,
    onNewNote: () -> Unit,
    onNewTodo: () -> Unit,
    onOpenDrawer: () -> Unit,
    onPinToggle: (NoteEntity) -> Unit,
    onStarToggle: (NoteEntity) -> Unit,
    onArchiveToggle: (NoteEntity) -> Unit,
    onLockToggle: (NoteEntity) -> Unit,
    onDeleteNote: (Long) -> Unit,
    onDuplicateNote: (NoteEntity) -> Unit,
    onUpdateNoteColor: (Long, String) -> Unit,
    appLockPin: String,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    var isSearchVisible by remember { mutableStateOf(false) }
    var selectedNoteForColor by remember { mutableStateOf<NoteEntity?>(null) }
    var noteToUnlock by remember { mutableStateOf<NoteEntity?>(null) }
    var showSortMenu by remember { mutableStateOf(false) }
    var sortBy by remember { mutableStateOf("Modified") } // Modified, Created, Title

    val filteredNotes = remember(notes, searchQuery, sortBy) {
        val filtered = if (searchQuery.isBlank()) notes
        else notes.filter {
            it.title.contains(searchQuery, ignoreCase = true) ||
                    it.content.contains(searchQuery, ignoreCase = true)
        }
        when (sortBy) {
            "Title" -> filtered.sortedBy { it.title.lowercase() }
            "Created" -> filtered.sortedByDescending { it.createdAt }
            else -> filtered.sortedWith(compareByDescending<NoteEntity> { it.isPinned }.thenByDescending { it.modifiedAt })
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = onOpenDrawer) {
                        Icon(
                            imageVector = Icons.Default.Menu,
                            contentDescription = "Open Drawer",
                            modifier = Modifier.size(28.dp)
                        )
                    }
                },
                title = {
                    Text(
                        text = title,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                },
                actions = {
                    // Sort / Filter icon matching Screenshot 1
                    IconButton(onClick = { showSortMenu = true }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Sort,
                            contentDescription = "Sort notes",
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    // User Profile Avatar matching Screenshot 1
                    IconButton(onClick = { /* User info / account info */ }) {
                        Icon(
                            imageVector = Icons.Default.AccountCircle,
                            contentDescription = "User account",
                            modifier = Modifier.size(28.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    DropdownMenu(
                        expanded = showSortMenu,
                        onDismissRequest = { showSortMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Sort by Last Modified") },
                            leadingIcon = { if (sortBy == "Modified") Icon(Icons.Default.Check, null) },
                            onClick = {
                                sortBy = "Modified"
                                showSortMenu = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Sort by Date Created") },
                            leadingIcon = { if (sortBy == "Created") Icon(Icons.Default.Check, null) },
                            onClick = {
                                sortBy = "Created"
                                showSortMenu = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Sort by Title (A-Z)") },
                            leadingIcon = { if (sortBy == "Title") Icon(Icons.Default.Check, null) },
                            onClick = {
                                sortBy = "Title"
                                showSortMenu = false
                            }
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        bottomBar = {
            BottomDock(
                isListView = isListView,
                onToggleLayout = onToggleListView,
                onSearchClick = { isSearchVisible = !isSearchVisible },
                onQuickEditClick = onNewNote,
                onTodoListClick = onNewTodo,
                onNewNoteClick = onNewNote
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Animated Search Input Bar
            AnimatedVisibility(visible = isSearchVisible) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search by title or content...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(Icons.Default.Close, contentDescription = "Clear")
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(24.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp)
                )
            }

            if (filteredNotes.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.NoteAlt,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                            modifier = Modifier.size(72.dp)
                        )
                        Text(
                            text = if (searchQuery.isNotBlank()) "No notes match \"$searchQuery\"" else "No notes here yet",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "Tap the + button below to create your first note",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                        )
                    }
                }
            } else if (isListView) {
                // List View
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(top = 8.dp, bottom = 16.dp)
                ) {
                    items(filteredNotes, key = { it.id }) { note ->
                        NoteCard(
                            note = note,
                            onClick = {
                                if (note.isLocked) {
                                    noteToUnlock = note
                                } else {
                                    onNoteClick(note)
                                }
                            },
                            onPinToggle = { onPinToggle(note) },
                            onStarToggle = { onStarToggle(note) },
                            onArchiveToggle = { onArchiveToggle(note) },
                            onLockToggle = { onLockToggle(note) },
                            onDelete = { onDeleteNote(note.id) },
                            onDuplicate = { onDuplicateNote(note) },
                            onChangeColor = { selectedNoteForColor = note },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            } else {
                // 2-Column Grid matching Screenshot 1
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(top = 8.dp, bottom = 16.dp)
                ) {
                    items(filteredNotes, key = { it.id }) { note ->
                        NoteCard(
                            note = note,
                            onClick = {
                                if (note.isLocked) {
                                    noteToUnlock = note
                                } else {
                                    onNoteClick(note)
                                }
                            },
                            onPinToggle = { onPinToggle(note) },
                            onStarToggle = { onStarToggle(note) },
                            onArchiveToggle = { onArchiveToggle(note) },
                            onLockToggle = { onLockToggle(note) },
                            onDelete = { onDeleteNote(note.id) },
                            onDuplicate = { onDuplicateNote(note) },
                            onChangeColor = { selectedNoteForColor = note }
                        )
                    }
                }
            }
        }
    }

    // Color picker dialog for note card
    selectedNoteForColor?.let { note ->
        ColorPickerDialog(
            initialColorHex = note.colorHex,
            onColorSelected = { newHex ->
                onUpdateNoteColor(note.id, newHex)
                selectedNoteForColor = null
            },
            onDismissRequest = { selectedNoteForColor = null }
        )
    }

    // Security PIN unlock dialog for locked notes
    noteToUnlock?.let { note ->
        SecurityPinDialog(
            title = "Unlock Note",
            subtitle = "Enter PIN to view \"${note.title.ifEmpty { "Note" }}\"",
            expectedPin = appLockPin,
            onSuccess = {
                val target = note
                noteToUnlock = null
                onNoteClick(target)
            },
            onDismissRequest = { noteToUnlock = null }
        )
    }
}
