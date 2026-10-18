package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.InsertDriveFile
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CategoryEntity

enum class ScreenDestination {
    ALL_NOTES,
    ALL_FILES,
    STARRED,
    ARCHIVED,
    PINNED,
    CATEGORIES,
    TRASH,
    CALENDAR,
    TEMPLATES,
    SETTINGS
}

@Composable
fun SidebarDrawer(
    currentDestination: ScreenDestination,
    selectedCategory: String?,
    categories: List<CategoryEntity>,
    onSelectDestination: (ScreenDestination) -> Unit,
    onSelectCategory: (String) -> Unit,
    onCloseDrawer: () -> Unit,
    modifier: Modifier = Modifier
) {
    ModalDrawerSheet(
        modifier = modifier.widthIn(max = 310.dp),
        drawerShape = RoundedCornerShape(topEnd = 24.dp, bottomEnd = 24.dp),
        drawerContainerColor = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(vertical = 16.dp, horizontal = 12.dp)
        ) {
            // App Branding Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFE65100)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.EditNote,
                        contentDescription = "Logo",
                        tint = Color.White,
                        modifier = Modifier.size(28.dp)
                    )
                }
                Column {
                    Text(
                        text = "UniNotes",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Unlimited Notebook & Vault",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 10.dp, horizontal = 8.dp),
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
            )

            // Primary Navigation Items
            DrawerNavItem(
                icon = Icons.Default.Notes,
                label = "All Notes",
                isSelected = currentDestination == ScreenDestination.ALL_NOTES && selectedCategory == null,
                onClick = {
                    onSelectDestination(ScreenDestination.ALL_NOTES)
                    onCloseDrawer()
                }
            )

            DrawerNavItem(
                icon = Icons.AutoMirrored.Filled.InsertDriveFile,
                label = "All Files & Attachments",
                isSelected = currentDestination == ScreenDestination.ALL_FILES,
                onClick = {
                    onSelectDestination(ScreenDestination.ALL_FILES)
                    onCloseDrawer()
                }
            )

            DrawerNavItem(
                icon = Icons.Default.Star,
                label = "Starred Notes",
                isSelected = currentDestination == ScreenDestination.STARRED,
                onClick = {
                    onSelectDestination(ScreenDestination.STARRED)
                    onCloseDrawer()
                }
            )

            DrawerNavItem(
                icon = Icons.Default.PushPin,
                label = "Pinned Notes",
                isSelected = currentDestination == ScreenDestination.PINNED,
                onClick = {
                    onSelectDestination(ScreenDestination.PINNED)
                    onCloseDrawer()
                }
            )

            DrawerNavItem(
                icon = Icons.Default.Archive,
                label = "Archived Notes",
                isSelected = currentDestination == ScreenDestination.ARCHIVED,
                onClick = {
                    onSelectDestination(ScreenDestination.ARCHIVED)
                    onCloseDrawer()
                }
            )

            DrawerNavItem(
                icon = Icons.Default.CalendarToday,
                label = "Calendar & Recent",
                isSelected = currentDestination == ScreenDestination.CALENDAR,
                onClick = {
                    onSelectDestination(ScreenDestination.CALENDAR)
                    onCloseDrawer()
                }
            )

            DrawerNavItem(
                icon = Icons.Default.DashboardCustomize,
                label = "Note Templates",
                isSelected = currentDestination == ScreenDestination.TEMPLATES,
                onClick = {
                    onSelectDestination(ScreenDestination.TEMPLATES)
                    onCloseDrawer()
                }
            )

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 10.dp, horizontal = 8.dp),
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
            )

            // Categories Section Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "CATEGORIES",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "Manage",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.clickable {
                        onSelectDestination(ScreenDestination.CATEGORIES)
                        onCloseDrawer()
                    }
                )
            }

            // Categories list items
            categories.forEach { category ->
                val isSelected = selectedCategory == category.name
                val catColor = try {
                    Color(android.graphics.Color.parseColor(category.colorHex))
                } catch (e: Exception) {
                    MaterialTheme.colorScheme.primary
                }

                NavigationDrawerItem(
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Bookmark,
                            contentDescription = null,
                            tint = catColor,
                            modifier = Modifier.size(20.dp)
                        )
                    },
                    label = {
                        Text(
                            text = category.name,
                            fontSize = 14.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                        )
                    },
                    selected = isSelected,
                    onClick = {
                        onSelectCategory(category.name)
                        onCloseDrawer()
                    },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.padding(vertical = 2.dp)
                )
            }

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 10.dp, horizontal = 8.dp),
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
            )

            // Utility Nav Items: Trash & Settings
            DrawerNavItem(
                icon = Icons.Default.Delete,
                label = "Trash / Recycle Bin",
                isSelected = currentDestination == ScreenDestination.TRASH,
                onClick = {
                    onSelectDestination(ScreenDestination.TRASH)
                    onCloseDrawer()
                }
            )

            DrawerNavItem(
                icon = Icons.Default.Settings,
                label = "Settings",
                isSelected = currentDestination == ScreenDestination.SETTINGS,
                onClick = {
                    onSelectDestination(ScreenDestination.SETTINGS)
                    onCloseDrawer()
                }
            )
        }
    }
}

@Composable
private fun DrawerNavItem(
    icon: ImageVector,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    NavigationDrawerItem(
        icon = {
            Icon(
                imageVector = icon,
                contentDescription = label,
                modifier = Modifier.size(22.dp)
            )
        },
        label = {
            Text(
                text = label,
                fontSize = 14.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
            )
        },
        selected = isSelected,
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.padding(vertical = 2.dp)
    )
}
