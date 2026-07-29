package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

@Composable
fun FrostedMeshBackground(
    content: @Composable BoxScope.() -> Unit
) {
    val isDark = isSystemInDarkTheme()

    val blob1 = if (isDark) Color(0x33D0BCFF) else Color(0x77D0BCFF)
    val blob2 = if (isDark) Color(0x22E8DEF8) else Color(0x66E8DEF8)
    val blob3 = if (isDark) Color(0x22B69DF8) else Color(0x55B69DF8)
    val bg = MaterialTheme.colorScheme.background

    Box(modifier = Modifier.fillMaxSize()) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawRect(color = bg)

            // Top-Left Mesh Blob
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(blob1, Color.Transparent),
                    center = Offset(size.width * 0.15f, size.height * 0.1f),
                    radius = size.width * 0.7f
                )
            )

            // Center-Right Mesh Blob
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(blob2, Color.Transparent),
                    center = Offset(size.width * 0.85f, size.height * 0.45f),
                    radius = size.width * 0.8f
                )
            )

            // Bottom Mesh Blob
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(blob3, Color.Transparent),
                    center = Offset(size.width * 0.35f, size.height * 0.85f),
                    radius = size.width * 0.75f
                )
            )
        }

        content()
    }
}
