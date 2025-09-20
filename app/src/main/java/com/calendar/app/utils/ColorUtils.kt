package com.calendar.app.utils

import android.graphics.Color

object ColorUtils {
    
    val defaultEventColors = listOf(
        "#8E44AD", // Purple
        "#E67E22", // Orange
        "#F1C40F", // Yellow
        "#E74C3C", // Red
        "#3498DB", // Blue
        "#27AE60", // Green
        "#17A2B8", // Cyan
        "#8D6E63", // Brown
        "#9C27B0", // Purple variant
        "#FF5722", // Deep orange
        "#795548", // Brown variant
        "#607D8B"  // Blue grey
    )
    
    fun isColorDark(colorString: String): Boolean {
        return try {
            val color = Color.parseColor(colorString)
            val darkness = 1 - (0.299 * Color.red(color) + 0.587 * Color.green(color) + 0.114 * Color.blue(color)) / 255
            darkness >= 0.5
        } catch (e: IllegalArgumentException) {
            true // Default to dark if parsing fails
        }
    }
    
    fun getContrastColor(backgroundColorString: String): Int {
        return if (isColorDark(backgroundColorString)) {
            Color.WHITE
        } else {
            Color.BLACK
        }
    }
    
    fun parseColorSafely(colorString: String, defaultColor: Int = Color.GRAY): Int {
        return try {
            Color.parseColor(colorString)
        } catch (e: IllegalArgumentException) {
            defaultColor
        }
    }
    
    fun generateRandomEventColor(): String {
        return defaultEventColors.random()
    }
}