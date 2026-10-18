package com.example.data.model

data class AppSettings(
    // General
    val fontName: String = "Standard",
    val fontSizeMultiplier: Float = 1.0f,
    // Security
    val biometricSignIn: Boolean = false,
    val biometricAutoTrigger: Boolean = false,
    val appLockEnabled: Boolean = false,
    val appLockPin: String = "",
    val hideFromRecentApps: Boolean = false,
    val trashAutoDeleteDays: Int = 30,
    // Notes Settings
    val saveModeInstant: Boolean = true,
    val readOnlyMode: Boolean = false,
    val isListView: Boolean = false,
    val scrollbarOverview: Boolean = true,
    val scrollbarEditor: Boolean = true,
    val previewLineCount: Int = 3,
    val categoryAsLine: Boolean = false,
    val defaultNoteColorHex: String = "#80939F",
    val confirmDeleteNote: Boolean = true,
    val keepScreenAwake: Boolean = false,
    // Category Settings
    val categoryFoldersEnabled: Boolean = true,
    val folderColorAsBackground: Boolean = true,
    val showPinnedNotes: Boolean = true,
    // Theme & Colors
    val colorSchema: String = "Foxy Orange", // Foxy Orange, Sky Blue, Sakura Red, Night Town Purple, Forest Green, Earth Brown, Material You
    val themeMode: String = "System", // System, Light, Dark, AMOLED Black
    val customAccentHex: String = "#E65100"
)
