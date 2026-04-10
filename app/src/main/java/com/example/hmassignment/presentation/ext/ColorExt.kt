package com.example.hmassignment.presentation.ext

import androidx.compose.ui.graphics.Color
import androidx.core.graphics.toColorInt

internal fun String.toSafeColor(): Color? {
    val formatted = if (startsWith("#")) this else "#$this"
    return try {
        Color(formatted.toColorInt())
    } catch (e: IllegalArgumentException) {
        null
    }
}
