package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.Room
import com.example.data.db.AppDatabase
import com.example.data.model.AppSettings
import com.example.data.model.AttachmentEntity
import com.example.data.model.CategoryEntity
import com.example.data.model.NoteEntity
import com.example.data.model.NoteTemplate
import com.example.data.repository.NoteRepository
import com.example.ui.components.ScreenDestination
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

sealed class AppScreen {
    object Overview : AppScreen()
    data class Editor(val note: NoteEntity) : AppScreen()
    data class TodoEditor(val note: NoteEntity) : AppScreen()
    object Categories : AppScreen()
    object Settings : AppScreen()
    object Trash : AppScreen()
    object Templates : AppScreen()
    object Calendar : AppScreen()
    object AllFiles : AppScreen()
}

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val database = Room.databaseBuilder(
        application,
        AppDatabase::class.java,
        "uninotes_database"
    ).build()

    val repository = NoteRepository(application, database)

    val settings: StateFlow<AppSettings> = repository.settingsFlow
        .stateIn(viewModelScope, SharingStarted.Eagerly, AppSettings())

    val activeNotes: StateFlow<List<NoteEntity>> = repository.activeNotes
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val starredNotes: StateFlow<List<NoteEntity>> = repository.starredNotes
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val archivedNotes: StateFlow<List<NoteEntity>> = repository.archivedNotes
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val pinnedNotes: StateFlow<List<NoteEntity>> = repository.pinnedNotes
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val trashNotes: StateFlow<List<NoteEntity>> = repository.trashNotes
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val categories: StateFlow<List<CategoryEntity>> = repository.categories
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allAttachments: StateFlow<List<AttachmentEntity>> = repository.allAttachments
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _currentScreen = MutableStateFlow<AppScreen>(AppScreen.Overview)
    val currentScreen: StateFlow<AppScreen> = _currentScreen.asStateFlow()

    private val _currentDestination = MutableStateFlow(ScreenDestination.ALL_NOTES)
    val currentDestination: StateFlow<ScreenDestination> = _currentDestination.asStateFlow()

    private val _selectedCategory = MutableStateFlow<String?>(null)
    val selectedCategory: StateFlow<String?> = _selectedCategory.asStateFlow()

    // App Lock Gate
    private val _isAppLocked = MutableStateFlow(false)
    val isAppLocked: StateFlow<Boolean> = _isAppLocked.asStateFlow()

    init {
        viewModelScope.launch {
            repository.populateInitialDataIfEmpty()
            if (settings.value.appLockEnabled && settings.value.appLockPin.isNotEmpty()) {
                _isAppLocked.value = true
            }
        }
    }

    fun unlockApp() {
        _isAppLocked.value = false
    }

    fun navigateTo(destination: ScreenDestination) {
        _currentDestination.value = destination
        _selectedCategory.value = null
        _currentScreen.value = when (destination) {
            ScreenDestination.ALL_NOTES -> AppScreen.Overview
            ScreenDestination.ALL_FILES -> AppScreen.AllFiles
            ScreenDestination.STARRED -> AppScreen.Overview
            ScreenDestination.PINNED -> AppScreen.Overview
            ScreenDestination.ARCHIVED -> AppScreen.Overview
            ScreenDestination.CATEGORIES -> AppScreen.Categories
            ScreenDestination.TRASH -> AppScreen.Trash
            ScreenDestination.CALENDAR -> AppScreen.Calendar
            ScreenDestination.TEMPLATES -> AppScreen.Templates
            ScreenDestination.SETTINGS -> AppScreen.Settings
        }
    }

    fun filterByCategory(categoryName: String) {
        _selectedCategory.value = categoryName
        _currentDestination.value = ScreenDestination.ALL_NOTES
        _currentScreen.value = AppScreen.Overview
    }

    fun openNote(note: NoteEntity) {
        if (note.isTodo) {
            _currentScreen.value = AppScreen.TodoEditor(note)
        } else {
            _currentScreen.value = AppScreen.Editor(note)
        }
    }

    fun openNewNote() {
        val newNote = NoteEntity(
            id = 0,
            title = "",
            content = "",
            colorHex = settings.value.defaultNoteColorHex,
            categoryName = _selectedCategory.value ?: ""
        )
        _currentScreen.value = AppScreen.Editor(newNote)
    }

    fun openNewTodo() {
        val newTodo = NoteEntity(
            id = 0,
            title = "My Checklist",
            content = "",
            isTodo = true,
            todoItemsJson = "[]",
            colorHex = "#E53935",
            categoryName = _selectedCategory.value ?: ""
        )
        _currentScreen.value = AppScreen.TodoEditor(newTodo)
    }

    fun createFromTemplate(template: NoteTemplate) {
        val newNote = NoteEntity(
            id = 0,
            title = template.defaultTitle,
            content = template.defaultContent,
            isTodo = template.isTodo,
            categoryName = template.defaultCategory,
            colorHex = template.defaultColorHex
        )
        if (template.isTodo) {
            _currentScreen.value = AppScreen.TodoEditor(newNote)
        } else {
            _currentScreen.value = AppScreen.Editor(newNote)
        }
    }

    fun backToOverview() {
        _currentScreen.value = AppScreen.Overview
    }

    fun saveNote(note: NoteEntity) {
        viewModelScope.launch {
            repository.saveNote(note)
        }
    }

    fun deleteNoteToTrash(id: Long) {
        viewModelScope.launch {
            repository.moveNoteToTrash(id)
        }
    }

    fun restoreNote(id: Long) {
        viewModelScope.launch {
            repository.restoreNote(id)
        }
    }

    fun deletePermanently(id: Long) {
        viewModelScope.launch {
            repository.deletePermanently(id)
        }
    }

    fun emptyTrash() {
        viewModelScope.launch {
            repository.emptyTrash()
        }
    }

    fun togglePin(note: NoteEntity) {
        viewModelScope.launch {
            repository.togglePin(note)
        }
    }

    fun toggleStar(note: NoteEntity) {
        viewModelScope.launch {
            repository.toggleStar(note)
        }
    }

    fun toggleArchive(note: NoteEntity) {
        viewModelScope.launch {
            repository.toggleArchive(note)
        }
    }

    fun toggleLock(note: NoteEntity) {
        viewModelScope.launch {
            repository.saveNote(note.copy(isLocked = !note.isLocked, modifiedAt = System.currentTimeMillis()))
        }
    }

    fun duplicateNote(note: NoteEntity) {
        viewModelScope.launch {
            repository.duplicateNote(note)
        }
    }

    fun updateNoteColor(noteId: Long, colorHex: String) {
        viewModelScope.launch {
            repository.updateNoteColor(noteId, colorHex)
        }
    }

    fun addCategory(name: String, colorHex: String, applyToCards: Boolean) {
        viewModelScope.launch {
            repository.addCategory(name, colorHex, applyToCards)
        }
    }

    fun deleteCategory(id: Long) {
        viewModelScope.launch {
            repository.deleteCategory(id)
        }
    }

    fun deleteAttachment(id: Long) {
        viewModelScope.launch {
            repository.deleteAttachment(id)
        }
    }

    fun updateSettings(transform: (AppSettings) -> AppSettings) {
        viewModelScope.launch {
            repository.updateSettings(transform)
        }
    }

    fun toggleListView() {
        viewModelScope.launch {
            repository.updateSettings { it.copy(isListView = !it.isListView) }
        }
    }

    fun exportBackup(password: String?) {
        viewModelScope.launch {
            repository.exportAllNotesJson(password)
        }
    }

    fun importBackup(payload: String, password: String?) {
        viewModelScope.launch {
            repository.importNotesJson(payload, password)
        }
    }
}
