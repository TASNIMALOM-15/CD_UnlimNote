package com.example.ui.screens

import android.content.Intent
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.NoteEntity
import com.example.ui.components.ColorPickerDialog
import com.example.ui.components.EditorToolbar
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteEditorScreen(
    initialNote: NoteEntity,
    onSaveNote: (NoteEntity) -> Unit,
    onDeleteNote: (Long) -> Unit,
    onBack: () -> Unit
) {
    var title by remember { mutableStateOf(initialNote.title.ifEmpty { "My Note" }) }
    var content by remember { mutableStateOf(initialNote.content) }
    var colorHex by remember { mutableStateOf(initialNote.colorHex) }
    var isStarred by remember { mutableStateOf(initialNote.isStarred) }
    var isPinned by remember { mutableStateOf(initialNote.isPinned) }
    var isLocked by remember { mutableStateOf(initialNote.isLocked) }
    var categoryName by remember { mutableStateOf(initialNote.categoryName) }

    // Text formatting state
    var isBold by remember { mutableStateOf(false) }
    var isItalic by remember { mutableStateOf(false) }
    var isUnderline by remember { mutableStateOf(false) }
    var isStrike by remember { mutableStateOf(false) }

    // Undo / Redo history
    val history = remember { mutableStateListOf(initialNote.content) }
    var historyIndex by remember { mutableIntStateOf(0) }

    fun pushHistory(newContent: String) {
        if (newContent != content) {
            content = newContent
            if (historyIndex < history.size - 1) {
                while (history.size > historyIndex + 1) {
                    history.removeAt(history.size - 1)
                }
            }
            history.add(newContent)
            historyIndex = history.size - 1
        }
    }

    var showColorPicker by remember { mutableStateOf(false) }
    var showOverflowMenu by remember { mutableStateOf(false) }
    var showAttachmentDialog by remember { mutableStateOf(false) }

    val context = LocalContext.current

    fun autoSave() {
        onSaveNote(
            initialNote.copy(
                title = title.trim(),
                content = content,
                colorHex = colorHex,
                isStarred = isStarred,
                isPinned = isPinned,
                isLocked = isLocked,
                categoryName = categoryName,
                modifiedAt = System.currentTimeMillis()
            )
        )
    }

    BackHandler {
        autoSave()
        onBack()
    }

    // Auto save on every content or title change
    LaunchedEffect(title, content, colorHex, isStarred, isPinned, isLocked, categoryName) {
        autoSave()
    }

    // Word and character stats
    val charCount = content.length
    val wordCount = remember(content) {
        content.trim().split("\\s+".toRegex()).count { it.isNotEmpty() }
    }
    val readingTime = (wordCount / 200).coerceAtLeast(1)

    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = {
                        autoSave()
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
                    // Attachment paperclip icon matching Screenshot 3
                    IconButton(onClick = { showAttachmentDialog = true }) {
                        Icon(Icons.Default.AttachFile, contentDescription = "Attachments")
                    }

                    // Bookmark / Favorite icon matching Screenshot 3
                    IconButton(onClick = { isStarred = !isStarred }) {
                        Icon(
                            imageVector = if (isStarred) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                            contentDescription = "Bookmark",
                            tint = if (isStarred) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    // Overflow 3-dots menu matching Screenshot 3
                    IconButton(onClick = { showOverflowMenu = true }) {
                        Icon(Icons.Default.MoreVert, contentDescription = "More")
                    }

                    DropdownMenu(
                        expanded = showOverflowMenu,
                        onDismissRequest = { showOverflowMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Share Note") },
                            leadingIcon = { Icon(Icons.Default.Share, contentDescription = null) },
                            onClick = {
                                showOverflowMenu = false
                                val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                    type = "text/plain"
                                    putExtra(Intent.EXTRA_SUBJECT, title)
                                    putExtra(Intent.EXTRA_TEXT, "$title\n\n$content")
                                }
                                context.startActivity(Intent.createChooser(shareIntent, "Share Note"))
                            }
                        )
                        DropdownMenuItem(
                            text = { Text(if (isPinned) "Unpin Note" else "Pin Note") },
                            leadingIcon = { Icon(Icons.Default.PushPin, contentDescription = null) },
                            onClick = {
                                showOverflowMenu = false
                                isPinned = !isPinned
                            }
                        )
                        DropdownMenuItem(
                            text = { Text(if (isLocked) "Unlock Note" else "Lock with Password") },
                            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                            onClick = {
                                showOverflowMenu = false
                                isLocked = !isLocked
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Change Note Color") },
                            leadingIcon = { Icon(Icons.Default.Palette, contentDescription = null) },
                            onClick = {
                                showOverflowMenu = false
                                showColorPicker = true
                            }
                        )
                        HorizontalDivider()
                        DropdownMenuItem(
                            text = { Text("Delete to Trash", color = MaterialTheme.colorScheme.error) },
                            leadingIcon = { Icon(Icons.Default.Delete, contentDescription = null, tint = MaterialTheme.colorScheme.error) },
                            onClick = {
                                showOverflowMenu = false
                                onDeleteNote(initialNote.id)
                                onBack()
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
            EditorToolbar(
                canUndo = historyIndex > 0,
                canRedo = historyIndex < history.size - 1,
                onUndo = {
                    if (historyIndex > 0) {
                        historyIndex--
                        content = history[historyIndex]
                    }
                },
                onRedo = {
                    if (historyIndex < history.size - 1) {
                        historyIndex++
                        content = history[historyIndex]
                    }
                },
                isBold = isBold,
                onToggleBold = {
                    isBold = !isBold
                    pushHistory("$content**")
                },
                isItalic = isItalic,
                onToggleItalic = {
                    isItalic = !isItalic
                    pushHistory("$content*")
                },
                isUnderline = isUnderline,
                onToggleUnderline = {
                    isUnderline = !isUnderline
                    pushHistory("$content<u>")
                },
                isStrike = isStrike,
                onToggleStrike = {
                    isStrike = !isStrike
                    pushHistory("$content~~")
                },
                onInsertHeading = {
                    pushHistory("$content\n## ")
                },
                onInsertBullet = {
                    pushHistory("$content\n- ")
                },
                onInsertNumberList = {
                    pushHistory("$content\n1. ")
                },
                onInsertChecklist = {
                    pushHistory("$content\n- [ ] ")
                },
                onInsertCode = {
                    pushHistory("$content\n```\n\n```")
                },
                onInsertQuote = {
                    pushHistory("$content\n> ")
                },
                onInsertDate = {
                    val dateStr = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()).format(Date())
                    pushHistory("$content\n$dateStr\n")
                },
                onOpenColorPicker = { showColorPicker = true },
                onAddAttachment = { showAttachmentDialog = true }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 12.dp)
        ) {
            // Stats & Sync status strip
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "$wordCount words • $charCount chars • ~$readingTime min read",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Saved locally",
                        tint = Color(0xFF4CAF50),
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = "Saved",
                        fontSize = 11.sp,
                        color = Color(0xFF4CAF50),
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            // Text editor body matching Screenshot 3
            BasicTextField(
                value = content,
                onValueChange = { pushHistory(it) },
                textStyle = TextStyle(
                    fontSize = 16.sp,
                    lineHeight = 24.sp,
                    fontWeight = if (isBold) FontWeight.Bold else FontWeight.Normal,
                    fontStyle = if (isItalic) FontStyle.Italic else FontStyle.Normal,
                    textDecoration = when {
                        isUnderline && isStrike -> TextDecoration.combine(listOf(TextDecoration.Underline, TextDecoration.LineThrough))
                        isUnderline -> TextDecoration.Underline
                        isStrike -> TextDecoration.LineThrough
                        else -> TextDecoration.None
                    },
                    color = MaterialTheme.colorScheme.onBackground
                ),
                cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                modifier = Modifier
                    .fillMaxWidth()
                    .defaultMinSize(minHeight = 400.dp)
            )
        }
    }

    if (showColorPicker) {
        ColorPickerDialog(
            initialColorHex = colorHex,
            onColorSelected = { selectedHex ->
                colorHex = selectedHex
                autoSave()
            },
            onDismissRequest = { showColorPicker = false }
        )
    }

    if (showAttachmentDialog) {
        AlertDialog(
            onDismissRequest = { showAttachmentDialog = false },
            title = { Text("Attach Media & Files") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Select type of file to attach to this note:")
                    Button(
                        onClick = {
                            pushHistory("$content\n![Image](https://images.unsplash.com/photo-1517842645767-c639042777db?w=600)\n")
                            showAttachmentDialog = false
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Image, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Add Sample Image")
                    }
                    OutlinedButton(
                        onClick = {
                            pushHistory("$content\n📎 [Document.pdf](#)\n")
                            showAttachmentDialog = false
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.PictureAsPdf, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Attach PDF")
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showAttachmentDialog = false }) { Text("Cancel") }
            }
        )
    }
}
