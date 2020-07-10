package com.oratakashi.resto.ui.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.oratakashi.resto.core.App
import com.oratakashi.resto.data.network.ApiEndPoint
import javax.inject.Inject

class SearchViewModel @Inject constructor(
    private val endpoint: ApiEndPoint
) : ViewModel(), SearchContract {

    private val observer: MutableLiveData<SearchState> by lazy {
        MutableLiveData<SearchState>()
    }

    private val collectionObs: MutableLiveData<CollectionState> by lazy {
        MutableLiveData<CollectionState>()
    }

    override val state: LiveData<SearchState>
        get() = observer
    override val collection: LiveData<CollectionState>
        get() = collectionObs

    override suspend fun getData(keyword: String, lat: String, lang: String) {
        endpoint.getSearch(keyword, lat, lang)
            .map<SearchState>(SearchState::Result)
            .onErrorReturn(SearchState::Error)
            .toFlowable()
            .startWith(SearchState.Loading)
            .subscribe(observer::postValue)
            .let { return@let App.disposable::add }
    }

    override suspend fun getCollection(lat: String, lang: String) {
        endpoint.getCollection(lat, lang)
            .map<CollectionState>(CollectionState::Result)
            .onErrorReturn(CollectionState::Error)
            .toFlowable()
            .startWith(CollectionState.Loading)
            .subscribe(collectionObs::postValue)
            .let { return@let App.disposable::add }
    }

    override fun onCleared() {
        super.onCleared()
        App.disposable.clear()
    }
}