package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ui.theme.MaterialColorFamilies

@Composable
fun ColorPickerDialog(
    initialColorHex: String,
    onColorSelected: (String) -> Unit,
    onDismissRequest: () -> Unit
) {
    var selectedTab by remember { mutableStateOf(0) } // 0: Material, 1: Color, 2: Clear
    var selectedFamilyIndex by remember { mutableStateOf(6) } // Default to Light Blue / Blue
    var currentColor by remember {
        mutableStateOf(
            try {
                Color(android.graphics.Color.parseColor(initialColorHex))
            } catch (e: Exception) {
                Color(0xFF63A4D8)
            }
        )
    }

    // HSV representation for custom color tab
    var hue by remember {
        val hsv = FloatArray(3)
        android.graphics.Color.colorToHSV(currentColor.toArgb(), hsv)
        mutableStateOf(hsv[0])
    }
    var saturation by remember {
        val hsv = FloatArray(3)
        android.graphics.Color.colorToHSV(currentColor.toArgb(), hsv)
        mutableStateOf(hsv[1])
    }
    var value by remember {
        val hsv = FloatArray(3)
        android.graphics.Color.colorToHSV(currentColor.toArgb(), hsv)
        mutableStateOf(hsv[2])
    }
    var alpha by remember { mutableStateOf(currentColor.alpha) }

    fun updateFromHsv() {
        val rgb = android.graphics.Color.HSVToColor((alpha * 255).toInt(), floatArrayOf(hue, saturation, value))
        currentColor = Color(rgb)
    }

    Dialog(onDismissRequest = onDismissRequest) {
        Card(
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(vertical = 16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Text(
                    text = "Select Color",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // Tabs: Material, Color, Clear matching screenshot
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    listOf("Material", "Color", "Clear").forEachIndexed { index, tabName ->
                        val isSelected = selectedTab == index
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(
                                    if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                                    else Color.Transparent
                                )
                                .clickable {
                                    selectedTab = index
                                    if (index == 2) {
                                        currentColor = Color(0xFF80939F) // Default neutral
                                    }
                                }
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = tabName,
                                fontSize = 14.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                // Tab Content
                when (selectedTab) {
                    0 -> { // Material Tab matching Screenshot 2
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(380.dp)
                        ) {
                            // Left Column: Family Dots
                            LazyColumn(
                                modifier = Modifier
                                    .width(60.dp)
                                    .fillMaxHeight(),
                                verticalArrangement = Arrangement.spacedBy(10.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                itemsIndexed(MaterialColorFamilies) { index, family ->
                                    val isSelected = selectedFamilyIndex == index
                                    Box(
                                        contentAlignment = Alignment.Center,
                                        modifier = Modifier
                                            .size(42.dp)
                                            .clip(CircleShape)
                                            .clickable {
                                                selectedFamilyIndex = index
                                                currentColor = family.primary
                                            }
                                    ) {
                                        if (isSelected) {
                                            Box(
                                                modifier = Modifier
                                                    .size(42.dp)
                                                    .clip(CircleShape)
                                                    .background(Color(0xFF03A9F4).copy(alpha = 0.35f))
                                            )
                                        }
                                        Box(
                                            modifier = Modifier
                                                .size(28.dp)
                                                .clip(CircleShape)
                                                .background(family.primary)
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            // Divider
                            Box(
                                modifier = Modifier
                                    .width(1.dp)
                                    .fillMaxHeight()
                                    .background(MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                            )

                            Spacer(modifier = Modifier.width(12.dp))

                            // Right Column: Shades matching Screenshot 2
                            val currentFamily = MaterialColorFamilies.getOrNull(selectedFamilyIndex)
                                ?: MaterialColorFamilies.first()
                            LazyColumn(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxHeight(),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                items(currentFamily.shades.size) { shadeIndex ->
                                    val shadeColor = currentFamily.shades[shadeIndex]
                                    val isCurrent = currentColor == shadeColor
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(34.dp)
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(shadeColor)
                                            .border(
                                                width = if (isCurrent) 2.dp else 0.dp,
                                                color = if (isCurrent) MaterialTheme.colorScheme.primary else Color.Transparent,
                                                shape = RoundedCornerShape(4.dp)
                                            )
                                            .clickable {
                                                currentColor = shadeColor
                                            }
                                    )
                                }
                            }
                        }
                    }

                    1 -> { // Custom HSV Color Tab matching Screenshot 3
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(380.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            // 2D Gradient Box (Hue as base, horizontal Saturation, vertical Value)
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(160.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(
                                        Brush.horizontalGradient(
                                            listOf(
                                                Color.White,
                                                Color(android.graphics.Color.HSVToColor(floatArrayOf(hue, 1f, 1f)))
                                            )
                                        )
                                    )
                                    .pointerInput(hue) {
                                        detectTapGestures { offset ->
                                            saturation = (offset.x / size.width).coerceIn(0f, 1f)
                                            value = (1f - (offset.y / size.height)).coerceIn(0f, 1f)
                                            updateFromHsv()
                                        }
                                    }
                                    .pointerInput(hue) {
                                        detectDragGestures { change, _ ->
                                            saturation = (change.position.x / size.width).coerceIn(0f, 1f)
                                            value = (1f - (change.position.y / size.height)).coerceIn(0f, 1f)
                                            updateFromHsv()
                                        }
                                    }
                            ) {
                                // Overlay vertical black gradient
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(
                                            Brush.verticalGradient(
                                                listOf(Color.Transparent, Color.Black)
                                            )
                                        )
                                    )
                            }

                            // Sliders row: Preview circle + Rainbow Hue Slider + Alpha Slider
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(44.dp)
                                        .clip(CircleShape)
                                        .background(currentColor)
                                        .border(1.dp, Color.LightGray, CircleShape)
                                )

                                Column(modifier = Modifier.weight(1f)) {
                                    // Rainbow Hue Slider
                                    Slider(
                                        value = hue,
                                        onValueChange = {
                                            hue = it
                                            updateFromHsv()
                                        },
                                        valueRange = 0f..360f,
                                        modifier = Modifier.fillMaxWidth()
                                    )

                                    // Alpha Slider
                                    Slider(
                                        value = alpha,
                                        onValueChange = {
                                            alpha = it
                                            updateFromHsv()
                                        },
                                        valueRange = 0f..1f,
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }
                            }

                            // Readouts (RGB / A) & Hex Input
                            val red = (currentColor.red * 255).toInt()
                            val green = (currentColor.green * 255).toInt()
                            val blue = (currentColor.blue * 255).toInt()
                            val alphaPercent = (alpha * 100).toInt()
                            val hexString = String.format("#%08X", currentColor.toArgb())

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text("RGB", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Text("R: $red  G: $green  B: $blue", fontSize = 13.sp, fontWeight = FontWeight.Medium)
                                }
                                Text("A: $alphaPercent%", fontSize = 13.sp, fontWeight = FontWeight.Medium)
                            }

                            // Hex display pill
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                                    .padding(horizontal = 12.dp, vertical = 8.dp)
                            ) {
                                Text("Hex:", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                Text(hexString, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                                Spacer(modifier = Modifier.weight(1f))
                                Box(
                                    modifier = Modifier
                                        .size(24.dp, 16.dp)
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(currentColor)
                                )
                            }
                        }
                    }

                    2 -> { // Clear Tab
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                        ) {
                            Text(
                                text = "Reset to default note card color",
                                fontSize = 15.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Action buttons: "Ok" matching screenshot
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismissRequest) {
                        Text("Cancel")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            val hex = String.format("#%06X", 0xFFFFFF and currentColor.toArgb())
                            onColorSelected(hex)
                            onDismissRequest()
                        }
                    ) {
                        Text("Ok")
                    }
                }
            }
        }
    }
}
