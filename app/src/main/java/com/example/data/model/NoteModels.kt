package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notes")
data class NoteEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String = "",
    val content: String = "",
    val categoryName: String = "",
    val colorHex: String = "#80939F", // Default card color matching screenshot
    val isPinned: Boolean = false,
    val isStarred: Boolean = false,
    val isArchived: Boolean = false,
    val isLocked: Boolean = false,
    val isDeleted: Boolean = false,
    val deletedTimestamp: Long = 0L,
    val isTodo: Boolean = false,
    val todoItemsJson: String = "[]",
    val createdAt: Long = System.currentTimeMillis(),
    val modifiedAt: Long = System.currentTimeMillis(),
    val fontFamily: String = "Standard",
    val fontSizeMultiplier: Float = 1.0f
)

data class TodoItem(
    val id: String = java.util.UUID.randomUUID().toString(),
    val text: String,
    val isCompleted: Boolean = false,
    val priority: String = "Medium" // Low, Medium, High
)

@Entity(tableName = "categories")
data class CategoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val colorHex: String = "#E53935",
    val iconName: String = "Bookmark",
    val applyColorToCards: Boolean = true
)

@Entity(tableName = "attachments")
data class AttachmentEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val noteId: Long,
    val fileName: String,
    val filePath: String,
    val mimeType: String,
    val fileSize: Long = 0L,
    val createdAt: Long = System.currentTimeMillis()
)

data class NoteTemplate(
    val id: String,
    val name: String,
    val description: String,
    val defaultTitle: String,
    val defaultContent: String,
    val isTodo: Boolean = false,
    val defaultCategory: String = "",
    val defaultColorHex: String = "#80939F"
)
