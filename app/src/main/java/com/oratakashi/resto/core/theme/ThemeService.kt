package com.oratakashi.resto.core.theme

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

/**
 * Theme service
 *
 * Fungsi theme service untuk memantau setiap perubahan yg terjadi di pengaturan
 * @property currentTheme MutableLiveData<ThemeList>
 */

class ThemeService : ViewModel() {
    val currentTheme: MutableLiveData<ThemeList> by lazy {
        MutableLiveData<ThemeList>()
    }
}