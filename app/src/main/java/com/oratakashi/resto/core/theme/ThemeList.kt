package com.oratakashi.resto.core.theme

/**
 * State Management for Theme
 *
 * State Management untuk Theme
 */
sealed class ThemeList {
    object System : ThemeList()
    object Light : ThemeList()
    object Dark : ThemeList()
}