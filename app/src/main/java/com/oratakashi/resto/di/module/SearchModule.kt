package com.oratakashi.resto.di.module

import androidx.lifecycle.ViewModel
import com.oratakashi.resto.data.network.ApiEndPoint
import com.oratakashi.resto.ui.search.SearchViewModel
import com.oratakashi.silahturahmikita.di.scope.ViewModelKey
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap
import retrofit2.Retrofit

@Module
abstract class SearchModule {
    @Module
    companion object {
        @JvmStatic
        @Provides
        fun providesApiEndPoint(retrofit: Retrofit): ApiEndPoint =
            retrofit.create(ApiEndPoint::class.java)
    }

    @Binds
    @IntoMap
    @ViewModelKey(SearchViewModel::class)
    abstract fun bindViewModel(viewModel: SearchViewModel): ViewModel
}