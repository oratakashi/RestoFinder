package com.oratakashi.resto.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.oratakashi.resto.core.App
import com.oratakashi.resto.data.network.ApiEndPoint
import javax.inject.Inject

/**
 * Main Logic Main Activity
 *
 * Di ViewModel ini akan mengambil data dari API berupa :
 * - Data Collection
 * - Data Nearby
 *
 * @property endpoint ApiEndPoint : Berisi Retrofit
 * @property observer MutableLiveData<MainState> : Berisi Main State
 * @property collectionObs MutableLiveData<CollectionState> : Berisi Collection State
 * @property state LiveData<MainState>
 * @property collection LiveData<CollectionState>
 * @constructor
 */

class MainViewModel @Inject constructor(
    private val endpoint: ApiEndPoint
) : ViewModel(), MainContract {

    private val observer: MutableLiveData<MainState> by lazy {
        MutableLiveData<MainState>()
    }

    private val collectionObs: MutableLiveData<CollectionState> by lazy {
        MutableLiveData<CollectionState>()
    }

    override val state: LiveData<MainState>
        get() = observer

    override val collection: LiveData<CollectionState>
        get() = collectionObs

    override suspend fun getCollection(lat: String, lang: String) {
        endpoint.getCollection(lat, lang)
            .map<CollectionState>(CollectionState::Result)
            .onErrorReturn(CollectionState::Error)
            .toFlowable()
            .startWith(CollectionState.Loading)
            .subscribe(collectionObs::postValue)
            .let { return@let App.disposable::add }
    }

    override suspend fun getNearby(lat: String, lang: String) {
        endpoint.getNearby(lat, lang)
            .map<MainState>(MainState::Result)
            .onErrorReturn(MainState::Error)
            .toFlowable()
            .startWith(MainState.Loading)
            .subscribe(observer::postValue)
            .let { return@let App.disposable::add }
    }

    override fun onCleared() {
        super.onCleared()
        App.disposable.clear()
    }
}