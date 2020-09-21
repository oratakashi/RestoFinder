package com.oratakashi.resto.di.module

import androidx.lifecycle.ViewModel
import com.oratakashi.resto.data.network.ApiEndPoint
import com.oratakashi.resto.ui.detail.DetailViewModel
import com.oratakashi.resto.di.scope.ViewModelKey
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap
import retrofit2.Retrofit

@Module
abstract class DetailModule {
    @Module
    companion object {
        @JvmStatic
        @Provides
        fun providesApiEndPoint(retrofit: Retrofit): ApiEndPoint =
            retrofit.create(ApiEndPoint::class.java)
    }

    @Binds
    @IntoMap
    @ViewModelKey(DetailViewModel::class)
    abstract fun bindViewModel(viewModel: DetailViewModel): ViewModel
}