package com.oratakashi.resto.di.builder

import androidx.lifecycle.ViewModelProvider
import com.oratakashi.resto.di.factory.ViewModelFactory
import com.oratakashi.resto.di.module.CollectionModule
import com.oratakashi.resto.di.module.DetailModule
import com.oratakashi.resto.di.module.MainModule
import com.oratakashi.resto.di.module.SearchModule
import com.oratakashi.resto.ui.collection.CollectionActivity
import com.oratakashi.resto.ui.detail.DetailActivity
import com.oratakashi.resto.ui.main.MainActivity
import com.oratakashi.resto.ui.search.SearchActivity
import com.oratakashi.resto.ui.settings.SettingsActivity
import com.oratakashi.resto.di.scope.Presentation
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector

/**
 * This class for Depedency Injection,
 * All ViewModel will injected with Retrofit
 *
 * Class ini berfungsi untuk depedency injection, Semua ViewModel yang ada di aplikasi ini
 * akan otomatis terinject dengan retrofit dengan syarat harus terdaftar semuanya di class ini
 * dan tiap activity mempunyai module / ViewModel yang berbeda
 */

@Module
abstract class AppBuilder {
    @Binds
    abstract fun bindViewModelFactory(factory: ViewModelFactory): ViewModelProvider.Factory

    @Presentation
    @ContributesAndroidInjector(modules = [MainModule::class])
    abstract fun contributeMainActivity(): MainActivity

    @Presentation
    @ContributesAndroidInjector(modules = [DetailModule::class])
    abstract fun contributeDetailActivity(): DetailActivity

    @Presentation
    @ContributesAndroidInjector(modules = [CollectionModule::class])
    abstract fun contributeCollectionActivity(): CollectionActivity

    @Presentation
    @ContributesAndroidInjector(modules = [SearchModule::class])
    abstract fun contributeSearchActivity(): SearchActivity

    @Presentation
    @ContributesAndroidInjector
    abstract fun contributeSettingsActivity(): SettingsActivity
}