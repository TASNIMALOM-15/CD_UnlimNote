package com.example.ui.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.NoteEntity
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun NoteCard(
    note: NoteEntity,
    onClick: () -> Unit,
    onPinToggle: () -> Unit,
    onStarToggle: () -> Unit,
    onArchiveToggle: () -> Unit,
    onLockToggle: () -> Unit,
    onDelete: () -> Unit,
    onDuplicate: () -> Unit,
    onChangeColor: () -> Unit,
    previewLines: Int = 3,
    modifier: Modifier = Modifier
) {
    var showMenu by remember { mutableStateOf(false) }

    val cardColor = remember(note.colorHex) {
        try {
            Color(android.graphics.Color.parseColor(note.colorHex))
        } catch (e: Exception) {
            Color(0xFF80939F)
        }
    }

    // Determine contrast color for text (dark or light)
    val textColor = remember(cardColor) {
        val luminance = (0.299 * cardColor.red + 0.587 * cardColor.green + 0.114 * cardColor.blue)
        if (luminance > 0.55) Color(0xFF1E2229) else Color.White
    }

    val subtitleColor = textColor.copy(alpha = 0.8f)
    val dateColor = textColor.copy(alpha = 0.65f)

    val dateFormat = remember { SimpleDateFormat("dd MMM yy", Locale.getDefault()) }
    val formattedDate = remember(note.modifiedAt) { dateFormat.format(Date(note.modifiedAt)) }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(cardColor)
            .combinedClickable(
                onClick = onClick,
                onLongClick = { showMenu = true }
            )
            .padding(horizontal = 16.dp, vertical = 14.dp)
            .defaultMinSize(minHeight = 120.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Header: Title & Badges
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Top,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = if (note.isLocked) "Locked Note 🔒" else note.title.ifEmpty { "Untitled" },
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = textColor,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f, fill = false)
                    )

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (note.isPinned) {
                            Icon(
                                Icons.Default.PushPin,
                                contentDescription = "Pinned",
                                tint = textColor.copy(alpha = 0.85f),
                                modifier = Modifier.size(14.dp)
                            )
                        }
                        if (note.isStarred) {
                            Icon(
                                Icons.Default.Star,
                                contentDescription = "Starred",
                                tint = Color(0xFFFFD700),
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }
                }

                if (!note.isLocked && note.content.isNotBlank()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = note.content,
                        fontSize = 13.sp,
                        color = subtitleColor,
                        maxLines = previewLines,
                        overflow = TextOverflow.Ellipsis,
                        lineHeight = 18.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Footer: Date & Optional Category Tag
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = formattedDate,
                    fontSize = 11.sp,
                    color = dateColor,
                    fontWeight = FontWeight.Medium
                )

                if (note.categoryName.isNotBlank()) {
                    Text(
                        text = note.categoryName,
                        fontSize = 11.sp,
                        color = textColor.copy(alpha = 0.75f),
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }

        // Long Press Context Menu
        DropdownMenu(
            expanded = showMenu,
            onDismissRequest = { showMenu = false }
        ) {
            DropdownMenuItem(
                text = { Text(if (note.isPinned) "Unpin" else "Pin to top") },
                leadingIcon = { Icon(Icons.Default.PushPin, contentDescription = null) },
                onClick = {
                    showMenu = false
                    onPinToggle()
                }
            )
            DropdownMenuItem(
                text = { Text(if (note.isStarred) "Unstar" else "Add to Starred") },
                leadingIcon = { Icon(Icons.Default.Star, contentDescription = null) },
                onClick = {
                    showMenu = false
                    onStarToggle()
                }
            )
            DropdownMenuItem(
                text = { Text("Change Color") },
                leadingIcon = { Icon(Icons.Default.Palette, contentDescription = null) },
                onClick = {
                    showMenu = false
                    onChangeColor()
                }
            )
            DropdownMenuItem(
                text = { Text("Duplicate") },
                leadingIcon = { Icon(Icons.Default.ContentCopy, contentDescription = null) },
                onClick = {
                    showMenu = false
                    onDuplicate()
                }
            )
            DropdownMenuItem(
                text = { Text(if (note.isLocked) "Unlock Note" else "Lock with Password") },
                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                onClick = {
                    showMenu = false
                    onLockToggle()
                }
            )
            DropdownMenuItem(
                text = { Text(if (note.isArchived) "Unarchive" else "Archive") },
                leadingIcon = { Icon(Icons.Default.Archive, contentDescription = null) },
                onClick = {
                    showMenu = false
                    onArchiveToggle()
                }
            )
            HorizontalDivider()
            DropdownMenuItem(
                text = { Text("Delete to Trash", color = MaterialTheme.colorScheme.error) },
                leadingIcon = { Icon(Icons.Default.Delete, contentDescription = null, tint = MaterialTheme.colorScheme.error) },
                onClick = {
                    showMenu = false
                    onDelete()
                }
            )
        }
    }
}
