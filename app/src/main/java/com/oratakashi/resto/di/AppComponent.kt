package com.oratakashi.resto.di

import com.oratakashi.resto.core.App
import com.oratakashi.resto.di.builder.AppBuilder
import com.oratakashi.resto.di.module.NetworkModule
import dagger.Component
import dagger.android.AndroidInjector
import dagger.android.support.AndroidSupportInjectionModule
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        AndroidSupportInjectionModule::class,
        NetworkModule::class,
        AppBuilder::class
    ]
)
interface AppComponent : AndroidInjector<App>