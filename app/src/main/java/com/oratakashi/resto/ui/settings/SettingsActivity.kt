package com.oratakashi.resto.ui.settings

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.oratakashi.resto.R
import com.oratakashi.resto.core.View
import com.oratakashi.resto.core.theme.ThemeList.*
import com.oratakashi.resto.core.theme.ThemeService
import com.oratakashi.resto.ui.main.MainActivity
import kotlinx.android.synthetic.main.content_settings.*

/**
 * This is a Settings Activity, All configuration on this apps saved on here
 * @property service ThemeService
 */

class SettingsActivity : View() {

    private val service: ThemeService by lazy {
        ViewModelProviders.of(this).get(ThemeService::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        setSupportActionBar(findViewById(R.id.toolbar))
        super.title(supportActionBar, resources.getString(R.string.title_settings))
        supportActionBar?.setHomeAsUpIndicator(
            ContextCompat.getDrawable(this, R.drawable.ic_arrow_back)
        )

        /**
         * Observe a Theme Configuration on Theme Service
         * and recreate MainActivity
         */

        service.currentTheme.observe(this, Observer { theme ->
            theme?.let {
                when (it) {
                    is System -> {
                        debug("Theme changed to System")
                        scSystem.isChecked = true
                        scDark.isChecked = false
                        scDark.isEnabled = false
                        scLight.isChecked = false
                        scLight.isEnabled = false
                        MainActivity.getInstance().recreate()
                    }
                    is Light -> {
                        debug("Theme changed to Light")
                        scSystem.isChecked = false
                        scDark.isChecked = false
                        scDark.isEnabled = true
                        scLight.isChecked = true
                        scLight.isEnabled = true
                        MainActivity.getInstance().recreate()
                    }
                    is Dark -> {
                        debug("Theme changed to Dark")
                        scSystem.isChecked = false
                        scDark.isChecked = true
                        scDark.isEnabled = true
                        scLight.isChecked = false
                        scLight.isEnabled = true
                        MainActivity.getInstance().recreate()
                    }
                }
            }
        })

        /**
         * Set Current Configuration
         *
         * Mengatur konfigurasi saat ini di UI
         */

        scSystem.isChecked = themeManager.isSystem
        scDark.isChecked = themeManager.isDark
        scLight.isChecked = themeManager.isLight

        /**
         * Apply if current configuration is System
         *
         * Menerapkan jika konfigurasi saat ini adalah system
         */

        if (themeManager.isSystem) {
            scDark.isChecked = false
            scDark.isEnabled = false
            scLight.isChecked = false
            scLight.isEnabled = false
        }

        llLanguage.setOnClickListener {
            startActivity(Intent(Settings.ACTION_LOCALE_SETTINGS))
        }

        /**
         * Swich Compat Listener
         *
         * Listener untuk komponen Switch Compat
         */

        scSystem.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                themeManager.changeTheme(System)
            } else {
                themeManager.changeTheme(Light)
            }
        }
        scLight.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                themeManager.changeTheme(Light)
            }
        }
        scDark.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                themeManager.changeTheme(Dark)
            }
        }
    }

    override fun darkStyle(): Int {
        return R.style.AppThemeCustomDark
    }

    override fun lightStyle(): Int {
        return R.style.AppThemeCustom
    }
}