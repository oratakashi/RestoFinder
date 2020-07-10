package com.oratakashi.resto.data.storage

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import com.oratakashi.resto.BuildConfig
import com.oratakashi.resto.core.theme.ThemeList
import com.oratakashi.resto.core.theme.ThemeList.*

class Sessions(context: Context) {
    companion object {
        val PREF_NAME = BuildConfig.APPLICATION_ID + ".Sessions"
        val Theme = "Theme"
    }

    var pref: SharedPreferences

    var context: Context? = null
    val PRIVATE_MODE: Int = 0

    init {
        this.context = context
        pref = context.getSharedPreferences(PREF_NAME, PRIVATE_MODE)
    }

    fun putTheme(theme: ThemeList) {
        when (theme) {
            is System -> pref.edit {
                putInt(Theme, 0)
            }
            is Light -> pref.edit {
                putInt(Theme, 1)
            }
            is Dark -> pref.edit {
                putInt(Theme, 2)
            }
        }
    }

    fun getTheme(): ThemeList = when (pref.getInt(Theme, 0)) {
        0 -> System
        1 -> Light
        else -> Dark
    }

    fun getSavedTheme(): Int = pref.getInt(Theme, 0)
}