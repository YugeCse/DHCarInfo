package com.car.dh.utils

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import java.util.Locale

fun Color.toHexCode(includeAlpha: Boolean = true): String {
    val argb = toArgb()
    return if (includeAlpha) {
        // AARRGGBB
        String.format(Locale.US, "#%08X", argb)
    } else {
        // RRGGBB
        String.format(Locale.US, "#%06X", argb and 0xFFFFFF)
    }
}


fun String.toColor(): Color = Color(android.graphics.Color.parseColor(this))