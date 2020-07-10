package com.oratakashi.resto.di.module

import androidx.lifecycle.ViewModel
import com.oratakashi.resto.data.network.ApiEndPoint
import com.oratakashi.resto.ui.collection.CollectionViewModel
import com.oratakashi.silahturahmikita.di.scope.ViewModelKey
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap
import retrofit2.Retrofit

@Module
abstract class CollectionModule {
    @Module
    companion object {
        @JvmStatic
        @Provides
        fun providesApiEndPoint(retrofit: Retrofit): ApiEndPoint =
            retrofit.create(ApiEndPoint::class.java)
    }

    @Binds
    @IntoMap
    @ViewModelKey(CollectionViewModel::class)
    abstract fun bindViewModel(viewModel: CollectionViewModel): ViewModel
}