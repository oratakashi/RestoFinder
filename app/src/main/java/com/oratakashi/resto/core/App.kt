package com.oratakashi.resto.core

import androidx.appcompat.app.AppCompatDelegate
import com.oratakashi.resto.core.theme.ThemeList.*
import com.oratakashi.resto.data.storage.Sessions
import com.oratakashi.resto.di.DaggerAppComponent
import dagger.android.AndroidInjector
import dagger.android.DaggerApplication
import io.reactivex.disposables.CompositeDisposable

class App : DaggerApplication() {

    companion object {
        val disposable: CompositeDisposable by lazy {
            CompositeDisposable()
        }
        lateinit var sessions: Sessions
    }

    override fun onCreate() {
        super.onCreate()
        sessions = Sessions(this)

        /**
         * Apply theme from saved settings
         *
         * Menerapkan Theme dari pengaturan yang tersimpan
         */

        AppCompatDelegate.setDefaultNightMode(
            when (sessions.getTheme()) {
                is System -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
                is Light -> AppCompatDelegate.MODE_NIGHT_NO
                is Dark -> AppCompatDelegate.MODE_NIGHT_YES
            }
        )
    }

    override fun applicationInjector(): AndroidInjector<out DaggerApplication> {
        return DaggerAppComponent.create().apply { inject(this@App) }
    }
}