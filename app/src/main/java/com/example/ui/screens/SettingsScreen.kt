package com.example.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ViewList
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AppSettings

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    settings: AppSettings,
    onUpdateSettings: ((AppSettings) -> AppSettings) -> Unit,
    onExportBackup: (password: String?) -> Unit,
    onImportBackup: (payload: String, password: String?) -> Unit,
    onBack: () -> Unit
) {
    var showFontDialog by remember { mutableStateOf(false) }
    var showFontSizeDialog by remember { mutableStateOf(false) }
    var showSchemaDialog by remember { mutableStateOf(false) }
    var showThemeDialog by remember { mutableStateOf(false) }
    var showPinSetupDialog by remember { mutableStateOf(false) }
    var showExportDialog by remember { mutableStateOf(false) }
    var showImportDialog by remember { mutableStateOf(false) }

    val fonts = listOf(
        "Standard", "Roboto", "Montserrat", "Open Sans",
        "Inter", "Lato", "Noto Sans", "Patrick Hand", "Gloria Hallelujah", "Handlee"
    )

    val fontSizes = listOf(
        "Standard (100%)" to 1.0f,
        "110%" to 1.1f,
        "120%" to 1.2f,
        "130%" to 1.3f
    )

    val colorSchemas = listOf(
        "Foxy Orange", "Sky Blue", "Sakura Red",
        "Night Town Purple", "Forest Green", "Earth Brown", "Material You"
    )

    val themeModes = listOf(
        "System", "Light", "Dark", "AMOLED Black"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings", fontWeight = FontWeight.SemiBold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            // ================= GENERAL =================
            item {
                SettingsSectionHeader("GENERAL")
            }

            item {
                SettingsItem(
                    icon = Icons.Default.FontDownload,
                    title = "Application Font",
                    subtitle = settings.fontName,
                    onClick = { showFontDialog = true }
                )
            }

            item {
                SettingsItem(
                    icon = Icons.Default.FormatSize,
                    title = "Application Font Size",
                    subtitle = "${(settings.fontSizeMultiplier * 100).toInt()}%",
                    onClick = { showFontSizeDialog = true }
                )
            }

            // ================= SECURITY matching Screenshot 5 =================
            item {
                SettingsSectionHeader("SECURITY")
            }

            item {
                SettingsSwitchItem(
                    icon = Icons.Default.Fingerprint,
                    title = "Biometric sign in",
                    subtitle = "Protect notes with your biometrics",
                    checked = settings.biometricSignIn,
                    onCheckedChange = { isChecked ->
                        onUpdateSettings { it.copy(biometricSignIn = isChecked) }
                    }
                )
            }

            item {
                SettingsSwitchItem(
                    icon = Icons.Default.SyncLock,
                    title = "Biometric Auto Trigger",
                    subtitle = "Start biometric authentication immediately",
                    checked = settings.biometricAutoTrigger,
                    onCheckedChange = { isChecked ->
                        onUpdateSettings { it.copy(biometricAutoTrigger = isChecked) }
                    }
                )
            }

            item {
                SettingsSwitchItem(
                    icon = Icons.Default.Lock,
                    title = "App Lock with PIN",
                    subtitle = if (settings.appLockPin.isNotEmpty()) "PIN is set" else "Configure 4-digit PIN",
                    checked = settings.appLockEnabled,
                    onCheckedChange = { isChecked ->
                        if (isChecked && settings.appLockPin.isEmpty()) {
                            showPinSetupDialog = true
                        } else {
                            onUpdateSettings { it.copy(appLockEnabled = isChecked) }
                        }
                    }
                )
            }

            item {
                SettingsSwitchItem(
                    icon = Icons.Default.Security,
                    title = "Hide Note Content from Recent Apps",
                    subtitle = "Blurs overview in task switcher",
                    checked = settings.hideFromRecentApps,
                    onCheckedChange = { isChecked ->
                        onUpdateSettings { it.copy(hideFromRecentApps = isChecked) }
                    }
                )
            }

            // ================= NOTES matching Screenshot 5 =================
            item {
                SettingsSectionHeader("NOTES")
            }

            item {
                SettingsItem(
                    icon = Icons.Default.Save,
                    title = "Save Mode",
                    subtitle = if (settings.saveModeInstant) "Automatically on every change" else "Save when returning to overview",
                    onClick = {
                        onUpdateSettings { it.copy(saveModeInstant = !it.saveModeInstant) }
                    }
                )
            }

            item {
                SettingsSwitchItem(
                    icon = Icons.Default.KeyboardHide,
                    title = "Readonly Mode",
                    subtitle = "Open your existing notes with a closed keyboard",
                    checked = settings.readOnlyMode,
                    onCheckedChange = { isChecked ->
                        onUpdateSettings { it.copy(readOnlyMode = isChecked) }
                    }
                )
            }

            item {
                SettingsSwitchItem(
                    icon = Icons.AutoMirrored.Filled.ViewList,
                    title = "List View",
                    subtitle = "View your notes in a list format rather than a grid",
                    checked = settings.isListView,
                    onCheckedChange = { isChecked ->
                        onUpdateSettings { it.copy(isListView = isChecked) }
                    }
                )
            }

            item {
                SettingsSwitchItem(
                    icon = Icons.Default.Tune,
                    title = "Scrollbar in Notes Overview",
                    subtitle = "Display a scrollbar in the notes list",
                    checked = settings.scrollbarOverview,
                    onCheckedChange = { isChecked ->
                        onUpdateSettings { it.copy(scrollbarOverview = isChecked) }
                    }
                )
            }

            item {
                SettingsSwitchItem(
                    icon = Icons.Default.Tune,
                    title = "Scrollbar in Notes",
                    subtitle = "Show scrollbar in text and to-do notes",
                    checked = settings.scrollbarEditor,
                    onCheckedChange = { isChecked ->
                        onUpdateSettings { it.copy(scrollbarEditor = isChecked) }
                    }
                )
            }

            item {
                SettingsSwitchItem(
                    icon = Icons.Default.DeleteForever,
                    title = "Confirm Before Deleting Notes",
                    subtitle = "Ask for confirmation before moving to trash",
                    checked = settings.confirmDeleteNote,
                    onCheckedChange = { isChecked ->
                        onUpdateSettings { it.copy(confirmDeleteNote = isChecked) }
                    }
                )
            }

            // ================= THEME & COLORS =================
            item {
                SettingsSectionHeader("THEME & COLOR SCHEMA")
            }

            item {
                SettingsItem(
                    icon = Icons.Default.Palette,
                    title = "Color Schema",
                    subtitle = settings.colorSchema,
                    onClick = { showSchemaDialog = true }
                )
            }

            item {
                SettingsItem(
                    icon = Icons.Default.Brightness4,
                    title = "Theme Mode",
                    subtitle = settings.themeMode,
                    onClick = { showThemeDialog = true }
                )
            }

            // ================= BACKUP & DATA =================
            item {
                SettingsSectionHeader("BACKUP & DATA MANAGEMENT")
            }

            item {
                SettingsItem(
                    icon = Icons.Default.CloudUpload,
                    title = "Export Backup (E2EE AES-256)",
                    subtitle = "Export all notes & categories with optional password",
                    onClick = { showExportDialog = true }
                )
            }

            item {
                SettingsItem(
                    icon = Icons.Default.CloudDownload,
                    title = "Restore from Backup",
                    subtitle = "Import notes from JSON or encrypted backup",
                    onClick = { showImportDialog = true }
                )
            }

            item {
                Spacer(modifier = Modifier.height(30.dp))
            }
        }
    }

    // Font selection dialog
    if (showFontDialog) {
        AlertDialog(
            onDismissRequest = { showFontDialog = false },
            title = { Text("Select Application Font") },
            text = {
                Column {
                    fonts.forEach { font ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    onUpdateSettings { it.copy(fontName = font) }
                                    showFontDialog = false
                                }
                                .padding(vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = settings.fontName == font,
                                onClick = null
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(font, fontSize = 16.sp)
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showFontDialog = false }) { Text("Close") }
            }
        )
    }

    // Font size dialog
    if (showFontSizeDialog) {
        AlertDialog(
            onDismissRequest = { showFontSizeDialog = false },
            title = { Text("Application Font Size") },
            text = {
                Column {
                    fontSizes.forEach { (label, mult) ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    onUpdateSettings { it.copy(fontSizeMultiplier = mult) }
                                    showFontSizeDialog = false
                                }
                                .padding(vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = settings.fontSizeMultiplier == mult,
                                onClick = null
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(label, fontSize = 16.sp)
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showFontSizeDialog = false }) { Text("Close") }
            }
        )
    }

    // Color schema dialog
    if (showSchemaDialog) {
        AlertDialog(
            onDismissRequest = { showSchemaDialog = false },
            title = { Text("Select Color Schema") },
            text = {
                Column {
                    colorSchemas.forEach { schema ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    onUpdateSettings { it.copy(colorSchema = schema) }
                                    showSchemaDialog = false
                                }
                                .padding(vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = settings.colorSchema == schema,
                                onClick = null
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(schema, fontSize = 16.sp)
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showSchemaDialog = false }) { Text("Close") }
            }
        )
    }

    // Theme mode dialog
    if (showThemeDialog) {
        AlertDialog(
            onDismissRequest = { showThemeDialog = false },
            title = { Text("Theme Mode") },
            text = {
                Column {
                    themeModes.forEach { mode ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    onUpdateSettings { it.copy(themeMode = mode) }
                                    showThemeDialog = false
                                }
                                .padding(vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = settings.themeMode == mode,
                                onClick = null
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(mode, fontSize = 16.sp)
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showThemeDialog = false }) { Text("Close") }
            }
        )
    }

    // Setup PIN dialog
    if (showPinSetupDialog) {
        var pinInput by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { showPinSetupDialog = false },
            title = { Text("Set 4-Digit Security PIN") },
            text = {
                OutlinedTextField(
                    value = pinInput,
                    onValueChange = { if (it.length <= 4 && it.all { c -> c.isDigit() }) pinInput = it },
                    label = { Text("4-Digit PIN") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (pinInput.length == 4) {
                            onUpdateSettings { it.copy(appLockPin = pinInput, appLockEnabled = true) }
                            showPinSetupDialog = false
                        }
                    }
                ) {
                    Text("Save PIN")
                }
            },
            dismissButton = {
                TextButton(onClick = { showPinSetupDialog = false }) { Text("Cancel") }
            }
        )
    }

    // Export Dialog with optional password
    if (showExportDialog) {
        var exportPassword by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { showExportDialog = false },
            title = { Text("Export Encrypted Backup") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Optionally enter a master password to encrypt your backup with AES-256-GCM:")
                    OutlinedTextField(
                        value = exportPassword,
                        onValueChange = { exportPassword = it },
                        label = { Text("Password (optional)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(onClick = {
                    onExportBackup(exportPassword.ifBlank { null })
                    showExportDialog = false
                }) {
                    Text("Export")
                }
            },
            dismissButton = {
                TextButton(onClick = { showExportDialog = false }) { Text("Cancel") }
            }
        )
    }

    // Import Dialog
    if (showImportDialog) {
        var importPayload by remember { mutableStateOf("") }
        var importPassword by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { showImportDialog = false },
            title = { Text("Restore Backup") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Paste your backup JSON or encrypted string:")
                    OutlinedTextField(
                        value = importPayload,
                        onValueChange = { importPayload = it },
                        label = { Text("Backup Data") },
                        maxLines = 4,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = importPassword,
                        onValueChange = { importPassword = it },
                        label = { Text("Password (if encrypted)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(onClick = {
                    if (importPayload.isNotBlank()) {
                        onImportBackup(importPayload, importPassword.ifBlank { null })
                        showImportDialog = false
                    }
                }) {
                    Text("Restore")
                }
            },
            dismissButton = {
                TextButton(onClick = { showImportDialog = false }) { Text("Cancel") }
            }
        )
    }
}

@Composable
fun SettingsSectionHeader(title: String) {
    Text(
        text = title,
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.padding(top = 20.dp, bottom = 8.dp, start = 4.dp)
    )
}

@Composable
fun SettingsItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp, horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(24.dp)
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = subtitle,
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
fun SettingsSwitchItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp, horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(24.dp)
            )
            Column {
                Text(
                    text = title,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = subtitle,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
    }
}
