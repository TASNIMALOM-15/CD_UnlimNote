package com.example.ui.theme

import androidx.compose.ui.graphics.Color

// Accent Themes
val FoxyOrangePrimary = Color(0xFFE65100)
val FoxyOrangeSecondary = Color(0xFFFF6F00)
val FoxyOrangeAccent = Color(0xFFFF9800)

val SkyBluePrimary = Color(0xFF1976D2)
val SkyBlueSecondary = Color(0xFF2196F3)
val SkyBlueAccent = Color(0xFF64B5F6)

val SakuraRedPrimary = Color(0xFFC2185B)
val SakuraRedSecondary = Color(0xFFE91E63)
val SakuraRedAccent = Color(0xFFF48FB1)

val NightTownPurplePrimary = Color(0xFF7B1FA2)
val NightTownPurpleSecondary = Color(0xFF9C27B0)
val NightTownPurpleAccent = Color(0xFFCE93D8)

val ForestGreenPrimary = Color(0xFF2E7D32)
val ForestGreenSecondary = Color(0xFF388E3C)
val ForestGreenAccent = Color(0xFF81C784)

val EarthBrownPrimary = Color(0xFF4E342E)
val EarthBrownSecondary = Color(0xFF6D4C41)
val EarthBrownAccent = Color(0xFFA1887F)

// Dark / AMOLED Colors
val AmoledBackground = Color(0xFF000000)
val AmoledSurface = Color(0xFF121212)
val DarkSurfaceElevated = Color(0xFF1E1E1E)

// Material Color Swatches matching Screenshot 2
val MaterialColorFamilies = listOf(
    ColorFamily("Red", Color(0xFFE53935), listOf(Color(0xFFFFEBEE), Color(0xFFFFCDD2), Color(0xFFEF9A9A), Color(0xFFE57373), Color(0xFFEF5350), Color(0xFFF44336), Color(0xFFE53935), Color(0xFFD32F2F), Color(0xFFC62828), Color(0xFFB71C1C))),
    ColorFamily("Pink", Color(0xFFD81B60), listOf(Color(0xFFFCE4EC), Color(0xFFF8BBD0), Color(0xFFF48FB1), Color(0xFFF06292), Color(0xFFEC407A), Color(0xFFE91E63), Color(0xFFD81B60), Color(0xFFC2185B), Color(0xFFAD1457), Color(0xFF880E4F))),
    ColorFamily("Purple", Color(0xFF8E24AA), listOf(Color(0xFFF3E5F5), Color(0xFFE1BEE7), Color(0xFFCE93D8), Color(0xFFBA68C8), Color(0xFFAB47BC), Color(0xFF9C27B0), Color(0xFF8E24AA), Color(0xFF7B1FA2), Color(0xFF6A1B9A), Color(0xFF4A148C))),
    ColorFamily("Deep Purple", Color(0xFF5E35B1), listOf(Color(0xFFEDE7F6), Color(0xFFD1C4E9), Color(0xFFB39DDB), Color(0xFF9575CD), Color(0xFF7E57C2), Color(0xFF673AB7), Color(0xFF5E35B1), Color(0xFF512DA8), Color(0xFF4527A0), Color(0xFF311B92))),
    ColorFamily("Indigo", Color(0xFF3949AB), listOf(Color(0xFFE8EAF6), Color(0xFFC5CAE9), Color(0xFF9FA8DA), Color(0xFF7986CB), Color(0xFF5C6BC0), Color(0xFF3F51B5), Color(0xFF3949AB), Color(0xFF303F9F), Color(0xFF283593), Color(0xFF1A237E))),
    ColorFamily("Blue", Color(0xFF1E88E5), listOf(Color(0xFFE3F2FD), Color(0xFFBBDEFB), Color(0xFF90CAF9), Color(0xFF64B5F6), Color(0xFF42A5F5), Color(0xFF2196F3), Color(0xFF1E88E5), Color(0xFF1976D2), Color(0xFF1565C0), Color(0xFF0D47A1))),
    ColorFamily("Light Blue", Color(0xFF039BE5), listOf(Color(0xFFE1F5FE), Color(0xFFB3E5FC), Color(0xFF81D4FA), Color(0xFF4FC3F7), Color(0xFF29B6F6), Color(0xFF03A9F4), Color(0xFF039BE5), Color(0xFF0288D1), Color(0xFF0277BD), Color(0xFF01579B))),
    ColorFamily("Cyan", Color(0xFF00ACC1), listOf(Color(0xFFE0F7FA), Color(0xFFB2EBF2), Color(0xFF80DEEA), Color(0xFF4DD0E1), Color(0xFF26C6DA), Color(0xFF00BCD4), Color(0xFF00ACC1), Color(0xFF0097A7), Color(0xFF00838F), Color(0xFF006064))),
    ColorFamily("Teal", Color(0xFF00897B), listOf(Color(0xFFE0F2F1), Color(0xFFB2DFDB), Color(0xFF80CBC4), Color(0xFF4DB6AC), Color(0xFF26A69A), Color(0xFF009688), Color(0xFF00897B), Color(0xFF00796B), Color(0xFF00695C), Color(0xFF004D40))),
    ColorFamily("Green", Color(0xFF43A047), listOf(Color(0xFFE8F5E9), Color(0xFFC8E6C9), Color(0xFFA5D6A7), Color(0xFF81C784), Color(0xFF66BB6A), Color(0xFF4CAF50), Color(0xFF43A047), Color(0xFF388E3C), Color(0xFF2E7D32), Color(0xFF1B5E20))),
    ColorFamily("Light Green", Color(0xFF7CB342), listOf(Color(0xFFF1F8E9), Color(0xFFDCEDC8), Color(0xFFC5E1A5), Color(0xFFAED581), Color(0xFF9CCC65), Color(0xFF8BC34A), Color(0xFF7CB342), Color(0xFF689F38), Color(0xFF558B2F), Color(0xFF33691E))),
    ColorFamily("Lime", Color(0xFFC0CA33), listOf(Color(0xFFF9FBE7), Color(0xFFF0F4C3), Color(0xFFE6EE9C), Color(0xFFDCE775), Color(0xFFD4E157), Color(0xFFCDDC39), Color(0xFFC0CA33), Color(0xFFAFB42B), Color(0xFF9E9D24), Color(0xFF827717))),
    ColorFamily("Yellow", Color(0xFFFDD835), listOf(Color(0xFFFFFDE7), Color(0xFFFFF9C4), Color(0xFFFFF59D), Color(0xFFFFF176), Color(0xFFFFEE58), Color(0xFFFFEB3B), Color(0xFFFDD835), Color(0xFFFBC02D), Color(0xFFF9A825), Color(0xFFF57F17)))
)

data class ColorFamily(
    val name: String,
    val primary: Color,
    val shades: List<Color>
)

// Category creation predefined color dots matching Screenshot 4
val CategoryPaletteDots = listOf(
    "#E53935", // Red
    "#D81B60", // Pink
    "#8E24AA", // Purple
    "#5E35B1", // Deep Purple
    "#3949AB", // Indigo
    "#039BE5", // Sky Blue
    "#00ACC1", // Cyan
    "#43A047"  // Green
)
