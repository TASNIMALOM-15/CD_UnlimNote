package com.example

import android.os.Bundle
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.SecurityPinDialog
import com.example.ui.components.SidebarDrawer
import com.example.ui.components.ScreenDestination
import com.example.ui.components.CategoryManagerScreen
import com.example.ui.screens.*
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.AppScreen
import com.example.viewmodel.MainViewModel
import kotlinx.coroutines.launch

class MainActivity : FragmentActivity() {
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val settings by viewModel.settings.collectAsStateWithLifecycle()
            val currentScreen by viewModel.currentScreen.collectAsStateWithLifecycle()
            val currentDestination by viewModel.currentDestination.collectAsStateWithLifecycle()
            val selectedCategory by viewModel.selectedCategory.collectAsStateWithLifecycle()
            val isAppLocked by viewModel.isAppLocked.collectAsStateWithLifecycle()

            val activeNotes by viewModel.activeNotes.collectAsStateWithLifecycle()
            val starredNotes by viewModel.starredNotes.collectAsStateWithLifecycle()
            val archivedNotes by viewModel.archivedNotes.collectAsStateWithLifecycle()
            val pinnedNotes by viewModel.pinnedNotes.collectAsStateWithLifecycle()
            val trashNotes by viewModel.trashNotes.collectAsStateWithLifecycle()
            val categories by viewModel.categories.collectAsStateWithLifecycle()
            val allAttachments by viewModel.allAttachments.collectAsStateWithLifecycle()

            MyApplicationTheme(
                colorSchema = settings.colorSchema,
                themeMode = settings.themeMode
            ) {
                val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
                val scope = rememberCoroutineScope()

                // Handle system back button for screens
                if (currentScreen !is AppScreen.Overview) {
                    BackHandler {
                        viewModel.backToOverview()
                    }
                }

                // App Lock Security Gate
                if (isAppLocked && settings.appLockPin.isNotEmpty()) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        SecurityPinDialog(
                            title = "UniNotes Locked",
                            subtitle = "Enter your PIN to open app",
                            expectedPin = settings.appLockPin,
                            onSuccess = { viewModel.unlockApp() },
                            onDismissRequest = { /* Modal lock, require PIN */ }
                        )
                    }
                } else {
                    ModalNavigationDrawer(
                        drawerState = drawerState,
                        drawerContent = {
                            SidebarDrawer(
                                currentDestination = currentDestination,
                                selectedCategory = selectedCategory,
                                categories = categories,
                                onSelectDestination = { dest ->
                                    viewModel.navigateTo(dest)
                                },
                                onSelectCategory = { cat ->
                                    viewModel.filterByCategory(cat)
                                },
                                onCloseDrawer = {
                                    scope.launch { drawerState.close() }
                                }
                            )
                        }
                    ) {
                        when (val screen = currentScreen) {
                            is AppScreen.Overview -> {
                                val displayedNotes = when {
                                    selectedCategory != null -> activeNotes.filter { it.categoryName == selectedCategory }
                                    currentDestination == ScreenDestination.STARRED -> starredNotes
                                    currentDestination == ScreenDestination.ARCHIVED -> archivedNotes
                                    currentDestination == ScreenDestination.PINNED -> pinnedNotes
                                    else -> activeNotes
                                }

                                val overviewTitle = when {
                                    selectedCategory != null -> selectedCategory!!
                                    currentDestination == ScreenDestination.STARRED -> "Starred Notes"
                                    currentDestination == ScreenDestination.ARCHIVED -> "Archived Notes"
                                    currentDestination == ScreenDestination.PINNED -> "Pinned Notes"
                                    else -> "All Notes"
                                }

                                NotesOverviewScreen(
                                    title = overviewTitle,
                                    notes = displayedNotes,
                                    isListView = settings.isListView,
                                    onToggleListView = { viewModel.toggleListView() },
                                    onNoteClick = { viewModel.openNote(it) },
                                    onNewNote = { viewModel.openNewNote() },
                                    onNewTodo = { viewModel.openNewTodo() },
                                    onOpenDrawer = { scope.launch { drawerState.open() } },
                                    onPinToggle = { viewModel.togglePin(it) },
                                    onStarToggle = { viewModel.toggleStar(it) },
                                    onArchiveToggle = { viewModel.toggleArchive(it) },
                                    onLockToggle = { viewModel.toggleLock(it) },
                                    onDeleteNote = { viewModel.deleteNoteToTrash(it) },
                                    onDuplicateNote = { viewModel.duplicateNote(it) },
                                    onUpdateNoteColor = { id, hex -> viewModel.updateNoteColor(id, hex) },
                                    appLockPin = settings.appLockPin
                                )
                            }

                            is AppScreen.Editor -> {
                                NoteEditorScreen(
                                    initialNote = screen.note,
                                    onSaveNote = { viewModel.saveNote(it) },
                                    onDeleteNote = { viewModel.deleteNoteToTrash(it) },
                                    onBack = { viewModel.backToOverview() }
                                )
                            }

                            is AppScreen.TodoEditor -> {
                                TodoListScreen(
                                    initialNote = screen.note,
                                    onSaveNote = { viewModel.saveNote(it) },
                                    onBack = { viewModel.backToOverview() }
                                )
                            }

                            is AppScreen.Categories -> {
                                CategoryManagerScreen(
                                    categories = categories,
                                    onAddCategory = { name, colorHex, applyToCards ->
                                        viewModel.addCategory(name, colorHex, applyToCards)
                                    },
                                    onDeleteCategory = { id ->
                                        viewModel.deleteCategory(id)
                                    },
                                    onBack = { viewModel.backToOverview() }
                                )
                            }

                            is AppScreen.Settings -> {
                                SettingsScreen(
                                    settings = settings,
                                    onUpdateSettings = { viewModel.updateSettings(it) },
                                    onExportBackup = { password -> viewModel.exportBackup(password) },
                                    onImportBackup = { payload, password -> viewModel.importBackup(payload, password) },
                                    onBack = { viewModel.backToOverview() }
                                )
                            }

                            is AppScreen.Trash -> {
                                TrashScreen(
                                    trashNotes = trashNotes,
                                    onRestoreNote = { viewModel.restoreNote(it) },
                                    onDeletePermanently = { viewModel.deletePermanently(it) },
                                    onEmptyTrash = { viewModel.emptyTrash() },
                                    onBack = { viewModel.backToOverview() }
                                )
                            }

                            is AppScreen.Templates -> {
                                TemplatesScreen(
                                    templates = viewModel.repository.getPredefinedTemplates(),
                                    onSelectTemplate = { viewModel.createFromTemplate(it) },
                                    onBack = { viewModel.backToOverview() }
                                )
                            }

                            is AppScreen.Calendar -> {
                                CalendarScreen(
                                    notes = activeNotes,
                                    onNoteClick = { viewModel.openNote(it) },
                                    onBack = { viewModel.backToOverview() }
                                )
                            }

                            is AppScreen.AllFiles -> {
                                AllFilesScreen(
                                    attachments = allAttachments,
                                    onDeleteAttachment = { viewModel.deleteAttachment(it) },
                                    onBack = { viewModel.backToOverview() }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
