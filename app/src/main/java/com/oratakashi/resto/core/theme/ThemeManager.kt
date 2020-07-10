package com.oratakashi.resto.core.theme

import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProviders
import com.oratakashi.resto.core.theme.ThemeList.*
import com.oratakashi.resto.data.storage.Sessions

/**
 * Theme Manager for handle theme
 *
 * Theme manager berfungsi untuk menghandle perubahan theme
 * @property sessions Sessions
 * @property isSystem Boolean
 * @property isLight Boolean
 * @property isDark Boolean
 * @property service ThemeService
 * @constructor
 */

class ThemeManager(private val sessions: Sessions, activity: FragmentActivity) {
    var isSystem: Boolean = true
        get() = when (sessions.getTheme()) {
            is System -> true
            else -> false
        }
        set(value) {
            if (value) {
                sessions.putTheme(System)
                service.currentTheme.postValue(System)
            }
            AppCompatDelegate.setDefaultNightMode(
                AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
            )
            field = value
        }

    var isLight: Boolean = false
        get() = when (sessions.getTheme()) {
            is Light -> true
            else -> false
        }
        set(value) {
            if (value) {
                sessions.putTheme(Light)
                service.currentTheme.postValue(Light)
                AppCompatDelegate.setDefaultNightMode(
                    AppCompatDelegate.MODE_NIGHT_NO
                )
            }
            field = value

        }

    var isDark: Boolean = false
        get() = when (sessions.getTheme()) {
            is Dark -> true
            else -> false
        }
        set(value) {
            if (value) {
                sessions.putTheme(Dark)
                service.currentTheme.postValue(Dark)
                AppCompatDelegate.setDefaultNightMode(
                    AppCompatDelegate.MODE_NIGHT_YES
                )
            }
            field = value
        }

    private val service: ThemeService by lazy {
        ViewModelProviders.of(activity).get(ThemeService::class.java)
    }

    fun changeTheme(theme: ThemeList) {
        when (theme) {
            is System -> {
                isSystem = true
                isDark = false
                isLight = false
            }
            is Dark -> {
                isSystem = false
                isDark = true
                isLight = false
            }
            is Light -> {
                isSystem = false
                isDark = false
                isLight = true
            }
        }
    }
}