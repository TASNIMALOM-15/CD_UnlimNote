package com.example.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.example.data.db.AppDatabase
import com.example.data.model.AppSettings
import com.example.data.model.AttachmentEntity
import com.example.data.model.CategoryEntity
import com.example.data.model.NoteEntity
import com.example.data.model.NoteTemplate
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.File

class NoteRepository(
    private val context: Context,
    private val database: AppDatabase
) {
    private val noteDao = database.noteDao()
    private val categoryDao = database.categoryDao()
    private val attachmentDao = database.attachmentDao()

    private val prefs: SharedPreferences =
        context.getSharedPreferences("uninotes_preferences", Context.MODE_PRIVATE)

    private val _settingsFlow = MutableStateFlow(loadSettingsFromPrefs())
    val settingsFlow: Flow<AppSettings> = _settingsFlow.asStateFlow()

    // Notes queries
    val activeNotes: Flow<List<NoteEntity>> = noteDao.getActiveNotes()
    val starredNotes: Flow<List<NoteEntity>> = noteDao.getStarredNotes()
    val archivedNotes: Flow<List<NoteEntity>> = noteDao.getArchivedNotes()
    val pinnedNotes: Flow<List<NoteEntity>> = noteDao.getPinnedNotes()
    val trashNotes: Flow<List<NoteEntity>> = noteDao.getTrashNotes()
    val categories: Flow<List<CategoryEntity>> = categoryDao.getAllCategories()
    val allAttachments: Flow<List<AttachmentEntity>> = attachmentDao.getAllAttachments()

    fun getNotesByCategory(category: String): Flow<List<NoteEntity>> =
        noteDao.getNotesByCategory(category)

    fun searchNotes(query: String): Flow<List<NoteEntity>> =
        noteDao.searchNotes(query)

    fun getAttachmentsForNote(noteId: Long): Flow<List<AttachmentEntity>> =
        attachmentDao.getAttachmentsForNote(noteId)

    suspend fun getNoteById(id: Long): NoteEntity? = noteDao.getNoteById(id)

    suspend fun saveNote(note: NoteEntity): Long = withContext(Dispatchers.IO) {
        if (note.id == 0L) {
            noteDao.insertNote(note)
        } else {
            noteDao.updateNote(note)
            note.id
        }
    }

    suspend fun moveNoteToTrash(id: Long) = withContext(Dispatchers.IO) {
        noteDao.moveToTrash(id)
    }

    suspend fun restoreNote(id: Long) = withContext(Dispatchers.IO) {
        noteDao.restoreFromTrash(id)
    }

    suspend fun deletePermanently(id: Long) = withContext(Dispatchers.IO) {
        noteDao.deletePermanently(id)
        attachmentDao.deleteAttachmentsForNote(id)
    }

    suspend fun emptyTrash() = withContext(Dispatchers.IO) {
        noteDao.emptyTrash()
    }

    suspend fun togglePin(note: NoteEntity) = withContext(Dispatchers.IO) {
        noteDao.updateNote(note.copy(isPinned = !note.isPinned, modifiedAt = System.currentTimeMillis()))
    }

    suspend fun toggleStar(note: NoteEntity) = withContext(Dispatchers.IO) {
        noteDao.updateNote(note.copy(isStarred = !note.isStarred, modifiedAt = System.currentTimeMillis()))
    }

    suspend fun toggleArchive(note: NoteEntity) = withContext(Dispatchers.IO) {
        noteDao.updateNote(note.copy(isArchived = !note.isArchived, modifiedAt = System.currentTimeMillis()))
    }

    suspend fun updateNoteColor(noteId: Long, colorHex: String) = withContext(Dispatchers.IO) {
        val note = noteDao.getNoteById(noteId)
        if (note != null) {
            noteDao.updateNote(note.copy(colorHex = colorHex, modifiedAt = System.currentTimeMillis()))
        }
    }

    suspend fun updateNoteCategory(noteId: Long, categoryName: String) = withContext(Dispatchers.IO) {
        val note = noteDao.getNoteById(noteId)
        if (note != null) {
            noteDao.updateNote(note.copy(categoryName = categoryName, modifiedAt = System.currentTimeMillis()))
        }
    }

    suspend fun duplicateNote(note: NoteEntity) = withContext(Dispatchers.IO) {
        val copy = note.copy(
            id = 0,
            title = if (note.title.isNotEmpty()) "${note.title} (Copy)" else "Untitled (Copy)",
            createdAt = System.currentTimeMillis(),
            modifiedAt = System.currentTimeMillis()
        )
        noteDao.insertNote(copy)
    }

    // Categories
    suspend fun addCategory(name: String, colorHex: String, applyToCards: Boolean = true) = withContext(Dispatchers.IO) {
        categoryDao.insertCategory(
            CategoryEntity(
                name = name.trim(),
                colorHex = colorHex,
                applyColorToCards = applyToCards
            )
        )
    }

    suspend fun deleteCategory(id: Long) = withContext(Dispatchers.IO) {
        categoryDao.deleteCategoryById(id)
    }

    // Attachments
    suspend fun addAttachment(noteId: Long, fileName: String, filePath: String, mimeType: String, fileSize: Long) =
        withContext(Dispatchers.IO) {
            attachmentDao.insertAttachment(
                AttachmentEntity(
                    noteId = noteId,
                    fileName = fileName,
                    filePath = filePath,
                    mimeType = mimeType,
                    fileSize = fileSize
                )
            )
        }

    suspend fun deleteAttachment(id: Long) = withContext(Dispatchers.IO) {
        attachmentDao.deleteAttachment(id)
    }

    // Templates
    fun getPredefinedTemplates(): List<NoteTemplate> = listOf(
        NoteTemplate(
            id = "blank",
            name = "Blank Note",
            description = "Start from scratch with clean slate",
            defaultTitle = "New Note",
            defaultContent = ""
        ),
        NoteTemplate(
            id = "meeting",
            name = "Meeting Notes",
            description = "Agenda, attendees, discussion points, action items",
            defaultTitle = "Meeting: Project Sync",
            defaultContent = "## 👥 Attendees\n- \n\n## 📋 Agenda\n1. \n2. \n\n## 💬 Notes & Discussion\n- \n\n## ✅ Action Items\n- [ ] Follow up on deliverables\n- [ ] Share meeting recap",
            defaultCategory = "Work",
            defaultColorHex = "#80939F"
        ),
        NoteTemplate(
            id = "study",
            name = "Study Notes",
            description = "Cornell style: Key concepts, summaries & review questions",
            defaultTitle = "Study: Chapter Review",
            defaultContent = "## 🎯 Key Concepts & Definitions\n- **Topic**:\n\n## 📝 Detailed Notes\n- \n\n## ❓ Questions for Review\n1. \n\n## 💡 Summary\n",
            defaultCategory = "Study",
            defaultColorHex = "#E1D56F"
        ),
        NoteTemplate(
            id = "journal",
            name = "Daily Journal",
            description = "Gratitude, daily highlights, personal reflections",
            defaultTitle = "Daily Reflection",
            defaultContent = "## 🌅 Morning Intentions\n- Today I will focus on:\n\n## 🙏 Gratitude\n1. \n2. \n3. \n\n## 🌙 Evening Reflections\n- Wins today:\n- What could have gone better:",
            defaultCategory = "Ideas 💡",
            defaultColorHex = "#63A4D8"
        ),
        NoteTemplate(
            id = "project",
            name = "Project Planning",
            description = "Goals, milestones, deadlines, resources",
            defaultTitle = "Project Roadmap",
            defaultContent = "## 🎯 Objective\n\n## 🚀 Milestones\n- Phase 1: Planning\n- Phase 2: Execution\n- Phase 3: Launch\n\n## 📌 Requirements & Tech Stack\n- \n\n## ⚠️ Risks & Mitigation\n- ",
            defaultCategory = "Ideas 💡",
            defaultColorHex = "#5E35B1"
        ),
        NoteTemplate(
            id = "todo",
            name = "To-Do List",
            description = "Task checklist with priority indicators",
            defaultTitle = "Weekly Tasks",
            defaultContent = "",
            isTodo = true,
            defaultCategory = "Work",
            defaultColorHex = "#E53935"
        ),
        NoteTemplate(
            id = "shopping",
            name = "Shopping List",
            description = "Categorized grocery and household supplies",
            defaultTitle = "Shopping List",
            defaultContent = "",
            isTodo = true,
            defaultCategory = "General",
            defaultColorHex = "#D95886"
        )
    )

    // Export & Backup
    suspend fun exportAllNotesJson(password: String? = null): String = withContext(Dispatchers.IO) {
        val notes = noteDao.getAllNotesRaw()
        val categoriesList = categoryDao.getAllCategoriesRaw()

        val root = JSONObject()
        root.put("version", 1)
        root.put("timestamp", System.currentTimeMillis())
        root.put("app", "UniNotes")

        val catArray = JSONArray()
        for (c in categoriesList) {
            val cObj = JSONObject()
            cObj.put("id", c.id)
            cObj.put("name", c.name)
            cObj.put("colorHex", c.colorHex)
            cObj.put("applyColorToCards", c.applyColorToCards)
            catArray.put(cObj)
        }
        root.put("categories", catArray)

        val noteArray = JSONArray()
        for (n in notes) {
            val nObj = JSONObject()
            nObj.put("id", n.id)
            nObj.put("title", n.title)
            nObj.put("content", n.content)
            nObj.put("categoryName", n.categoryName)
            nObj.put("colorHex", n.colorHex)
            nObj.put("isPinned", n.isPinned)
            nObj.put("isStarred", n.isStarred)
            nObj.put("isArchived", n.isArchived)
            nObj.put("isLocked", n.isLocked)
            nObj.put("isDeleted", n.isDeleted)
            nObj.put("isTodo", n.isTodo)
            nObj.put("todoItemsJson", n.todoItemsJson)
            nObj.put("createdAt", n.createdAt)
            nObj.put("modifiedAt", n.modifiedAt)
            noteArray.put(nObj)
        }
        root.put("notes", noteArray)

        val jsonString = root.toString(2)
        if (!password.isNullOrBlank()) {
            CryptoManager.encrypt(jsonString, password)
        } else {
            jsonString
        }
    }

    suspend fun importNotesJson(payload: String, password: String? = null): Result<Int> = withContext(Dispatchers.IO) {
        runCatching {
            val jsonString = if (!password.isNullOrBlank()) {
                CryptoManager.decrypt(payload, password).getOrThrow()
            } else {
                payload
            }

            val root = JSONObject(jsonString)
            val noteArray = root.getJSONArray("notes")
            val newNotes = mutableListOf<NoteEntity>()

            for (i in 0 until noteArray.length()) {
                val obj = noteArray.getJSONObject(i)
                newNotes.add(
                    NoteEntity(
                        title = obj.optString("title", ""),
                        content = obj.optString("content", ""),
                        categoryName = obj.optString("categoryName", ""),
                        colorHex = obj.optString("colorHex", "#80939F"),
                        isPinned = obj.optBoolean("isPinned", false),
                        isStarred = obj.optBoolean("isStarred", false),
                        isArchived = obj.optBoolean("isArchived", false),
                        isLocked = obj.optBoolean("isLocked", false),
                        isDeleted = obj.optBoolean("isDeleted", false),
                        isTodo = obj.optBoolean("isTodo", false),
                        todoItemsJson = obj.optString("todoItemsJson", "[]"),
                        createdAt = obj.optLong("createdAt", System.currentTimeMillis()),
                        modifiedAt = obj.optLong("modifiedAt", System.currentTimeMillis())
                    )
                )
            }

            if (root.has("categories")) {
                val catArray = root.getJSONArray("categories")
                val newCategories = mutableListOf<CategoryEntity>()
                for (i in 0 until catArray.length()) {
                    val cObj = catArray.getJSONObject(i)
                    newCategories.add(
                        CategoryEntity(
                            name = cObj.optString("name", "Category"),
                            colorHex = cObj.optString("colorHex", "#E53935"),
                            applyColorToCards = cObj.optBoolean("applyColorToCards", true)
                        )
                    )
                }
                categoryDao.insertCategoriesBatch(newCategories)
            }

            noteDao.insertNotesBatch(newNotes)
            newNotes.size
        }
    }

    // Settings
    private fun loadSettingsFromPrefs(): AppSettings {
        return AppSettings(
            fontName = prefs.getString("fontName", "Standard") ?: "Standard",
            fontSizeMultiplier = prefs.getFloat("fontSizeMultiplier", 1.0f),
            biometricSignIn = prefs.getBoolean("biometricSignIn", false),
            biometricAutoTrigger = prefs.getBoolean("biometricAutoTrigger", false),
            appLockEnabled = prefs.getBoolean("appLockEnabled", false),
            appLockPin = prefs.getString("appLockPin", "") ?: "",
            hideFromRecentApps = prefs.getBoolean("hideFromRecentApps", false),
            trashAutoDeleteDays = prefs.getInt("trashAutoDeleteDays", 30),
            saveModeInstant = prefs.getBoolean("saveModeInstant", true),
            readOnlyMode = prefs.getBoolean("readOnlyMode", false),
            isListView = prefs.getBoolean("isListView", false),
            scrollbarOverview = prefs.getBoolean("scrollbarOverview", true),
            scrollbarEditor = prefs.getBoolean("scrollbarEditor", true),
            previewLineCount = prefs.getInt("previewLineCount", 3),
            categoryAsLine = prefs.getBoolean("categoryAsLine", false),
            defaultNoteColorHex = prefs.getString("defaultNoteColorHex", "#80939F") ?: "#80939F",
            confirmDeleteNote = prefs.getBoolean("confirmDeleteNote", true),
            keepScreenAwake = prefs.getBoolean("keepScreenAwake", false),
            categoryFoldersEnabled = prefs.getBoolean("categoryFoldersEnabled", true),
            folderColorAsBackground = prefs.getBoolean("folderColorAsBackground", true),
            showPinnedNotes = prefs.getBoolean("showPinnedNotes", true),
            colorSchema = prefs.getString("colorSchema", "Foxy Orange") ?: "Foxy Orange",
            themeMode = prefs.getString("themeMode", "System") ?: "System",
            customAccentHex = prefs.getString("customAccentHex", "#E65100") ?: "#E65100"
        )
    }

    suspend fun updateSettings(transform: (AppSettings) -> AppSettings) = withContext(Dispatchers.IO) {
        val updated = transform(_settingsFlow.value)
        prefs.edit().apply {
            putString("fontName", updated.fontName)
            putFloat("fontSizeMultiplier", updated.fontSizeMultiplier)
            putBoolean("biometricSignIn", updated.biometricSignIn)
            putBoolean("biometricAutoTrigger", updated.biometricAutoTrigger)
            putBoolean("appLockEnabled", updated.appLockEnabled)
            putString("appLockPin", updated.appLockPin)
            putBoolean("hideFromRecentApps", updated.hideFromRecentApps)
            putInt("trashAutoDeleteDays", updated.trashAutoDeleteDays)
            putBoolean("saveModeInstant", updated.saveModeInstant)
            putBoolean("readOnlyMode", updated.readOnlyMode)
            putBoolean("isListView", updated.isListView)
            putBoolean("scrollbarOverview", updated.scrollbarOverview)
            putBoolean("scrollbarEditor", updated.scrollbarEditor)
            putInt("previewLineCount", updated.previewLineCount)
            putBoolean("categoryAsLine", updated.categoryAsLine)
            putString("defaultNoteColorHex", updated.defaultNoteColorHex)
            putBoolean("confirmDeleteNote", updated.confirmDeleteNote)
            putBoolean("keepScreenAwake", updated.keepScreenAwake)
            putBoolean("categoryFoldersEnabled", updated.categoryFoldersEnabled)
            putBoolean("folderColorAsBackground", updated.folderColorAsBackground)
            putBoolean("showPinnedNotes", updated.showPinnedNotes)
            putString("colorSchema", updated.colorSchema)
            putString("themeMode", updated.themeMode)
            putString("customAccentHex", updated.customAccentHex)
            apply()
        }
        _settingsFlow.value = updated
    }

    suspend fun populateInitialDataIfEmpty() = withContext(Dispatchers.IO) {
        val existingCats = categoryDao.getAllCategoriesRaw()
        if (existingCats.isEmpty()) {
            val initialCategories = listOf(
                CategoryEntity(name = "Accounts information", colorHex = "#C2185B"),
                CategoryEntity(name = "Content making", colorHex = "#455A64"),
                CategoryEntity(name = "Ideas 💡", colorHex = "#7B1FA2"),
                CategoryEntity(name = "Passwords🔑", colorHex = "#1976D2"),
                CategoryEntity(name = "Script", colorHex = "#E64A19"),
                CategoryEntity(name = "Study", colorHex = "#AFB42B"),
                CategoryEntity(name = "Work", colorHex = "#F57C00")
            )
            categoryDao.insertCategoriesBatch(initialCategories)
        }

        val existingNotes = noteDao.getAllNotesRaw()
        if (existingNotes.isEmpty()) {
            val sampleNotes = listOf(
                NoteEntity(
                    title = "Ideas 💡",
                    content = "YouTube automation\nCreate a personal web app\nAI powered agent workflows",
                    categoryName = "Ideas 💡",
                    colorHex = "#80939F", // Exactly matching screenshot 1
                    isPinned = true,
                    createdAt = System.currentTimeMillis(),
                    modifiedAt = System.currentTimeMillis()
                ),
                NoteEntity(
                    title = "Passwords & Auth",
                    content = "AI of google accounts and protons accounts\nReview two-factor authenticators",
                    categoryName = "Passwords🔑",
                    colorHex = "#63A4D8", // Matching screenshot 1
                    isPinned = true,
                    createdAt = System.currentTimeMillis(),
                    modifiedAt = System.currentTimeMillis()
                ),
                NoteEntity(
                    title = "Account information",
                    content = "Cloud storage tokens and device backups",
                    categoryName = "Accounts information",
                    colorHex = "#D95886", // Matching screenshot 1
                    createdAt = System.currentTimeMillis(),
                    modifiedAt = System.currentTimeMillis()
                ),
                NoteEntity(
                    title = "My Note",
                    content = "Welcome to UniNotes!\n\nAll notes are stored securely on your device. You can customize fonts, colors, lock notes with PIN/biometrics, and format text with rich tools.",
                    categoryName = "Study",
                    colorHex = "#E1D56F", // Matching screenshot 1
                    createdAt = System.currentTimeMillis(),
                    modifiedAt = System.currentTimeMillis()
                )
            )
            noteDao.insertNotesBatch(sampleNotes)
        }
    }
}
