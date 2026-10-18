package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Redo
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun EditorToolbar(
    canUndo: Boolean,
    canRedo: Boolean,
    onUndo: () -> Unit,
    onRedo: () -> Unit,
    isBold: Boolean,
    onToggleBold: () -> Unit,
    isItalic: Boolean,
    onToggleItalic: () -> Unit,
    isUnderline: Boolean,
    onToggleUnderline: () -> Unit,
    isStrike: Boolean,
    onToggleStrike: () -> Unit,
    onInsertHeading: () -> Unit,
    onInsertBullet: () -> Unit,
    onInsertNumberList: () -> Unit,
    onInsertChecklist: () -> Unit,
    onInsertCode: () -> Unit,
    onInsertQuote: () -> Unit,
    onInsertDate: () -> Unit,
    onOpenColorPicker: () -> Unit,
    onAddAttachment: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
        tonalElevation = 3.dp,
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 8.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            // Plus / Add Attachment button
            IconButton(
                onClick = onAddAttachment,
                modifier = Modifier.size(40.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Item", modifier = Modifier.size(24.dp))
            }

            // Undo
            IconButton(
                onClick = onUndo,
                enabled = canUndo,
                modifier = Modifier.size(40.dp)
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.Undo,
                    contentDescription = "Undo",
                    modifier = Modifier.size(22.dp),
                    tint = if (canUndo) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                )
            }

            // Redo
            IconButton(
                onClick = onRedo,
                enabled = canRedo,
                modifier = Modifier.size(40.dp)
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.Redo,
                    contentDescription = "Redo",
                    modifier = Modifier.size(22.dp),
                    tint = if (canRedo) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                )
            }

            // Divider matching Screenshot 3
            VerticalDivider(
                modifier = Modifier
                    .height(28.dp)
                    .width(1.dp)
                    .padding(horizontal = 4.dp),
                color = MaterialTheme.colorScheme.outlineVariant
            )

            // Bold with circular pill matching Screenshot 3
            ToolbarPillButton(
                isSelected = isBold,
                onClick = onToggleBold
            ) {
                Text(
                    text = "B",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isBold) Color.White else MaterialTheme.colorScheme.onSurface
                )
            }

            // Italic with circular pill matching Screenshot 3
            ToolbarPillButton(
                isSelected = isItalic,
                onClick = onToggleItalic
            ) {
                Text(
                    text = "I",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    fontStyle = FontStyle.Italic,
                    color = if (isItalic) Color.White else MaterialTheme.colorScheme.onSurface
                )
            }

            // Underline matching Screenshot 3
            ToolbarPillButton(
                isSelected = isUnderline,
                onClick = onToggleUnderline
            ) {
                Text(
                    text = "U",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    textDecoration = TextDecoration.Underline,
                    color = if (isUnderline) Color.White else MaterialTheme.colorScheme.onSurface
                )
            }

            // Strikethrough matching Screenshot 3
            ToolbarPillButton(
                isSelected = isStrike,
                onClick = onToggleStrike
            ) {
                Text(
                    text = "S",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    textDecoration = TextDecoration.LineThrough,
                    color = if (isStrike) Color.White else MaterialTheme.colorScheme.onSurface
                )
            }

            // Color Selector icon matching Screenshot 3 (Palette / Fill)
            IconButton(
                onClick = onOpenColorPicker,
                modifier = Modifier.size(40.dp)
            ) {
                Icon(Icons.Default.Palette, contentDescription = "Card Color", modifier = Modifier.size(22.dp))
            }

            // Heading
            IconButton(
                onClick = onInsertHeading,
                modifier = Modifier.size(40.dp)
            ) {
                Icon(Icons.Default.Title, contentDescription = "Heading", modifier = Modifier.size(22.dp))
            }

            // Checklist
            IconButton(
                onClick = onInsertChecklist,
                modifier = Modifier.size(40.dp)
            ) {
                Icon(Icons.Default.Checklist, contentDescription = "Checklist", modifier = Modifier.size(22.dp))
            }

            // Bullet List
            IconButton(
                onClick = onInsertBullet,
                modifier = Modifier.size(40.dp)
            ) {
                Icon(Icons.Default.FormatListBulleted, contentDescription = "Bullet List", modifier = Modifier.size(22.dp))
            }

            // Numbered List
            IconButton(
                onClick = onInsertNumberList,
                modifier = Modifier.size(40.dp)
            ) {
                Icon(Icons.Default.FormatListNumbered, contentDescription = "Numbered List", modifier = Modifier.size(22.dp))
            }

            // Code
            IconButton(
                onClick = onInsertCode,
                modifier = Modifier.size(40.dp)
            ) {
                Icon(Icons.Default.Code, contentDescription = "Code", modifier = Modifier.size(22.dp))
            }

            // Quote
            IconButton(
                onClick = onInsertQuote,
                modifier = Modifier.size(40.dp)
            ) {
                Icon(Icons.Default.FormatQuote, contentDescription = "Quote", modifier = Modifier.size(22.dp))
            }

            // Insert Date
            IconButton(
                onClick = onInsertDate,
                modifier = Modifier.size(40.dp)
            ) {
                Icon(Icons.Default.CalendarToday, contentDescription = "Date", modifier = Modifier.size(20.dp))
            }
        }
    }
}

@Composable
private fun ToolbarPillButton(
    isSelected: Boolean,
    onClick: () -> Unit,
    content: @Composable () -> Unit
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(38.dp)
            .clip(CircleShape)
            .background(
                if (isSelected) Color(0xFF2C567A) // Deep steel blue circular selection pill matching screenshot 3
                else Color.Transparent
            )
            .clickable(onClick = onClick)
    ) {
        content()
    }
}
