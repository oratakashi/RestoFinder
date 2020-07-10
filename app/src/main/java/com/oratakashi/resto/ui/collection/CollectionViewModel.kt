package com.oratakashi.resto.ui.collection

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.oratakashi.resto.core.App
import com.oratakashi.resto.data.network.ApiEndPoint
import javax.inject.Inject

class CollectionViewModel @Inject constructor(
    private val endpoint: ApiEndPoint
) : ViewModel(), CollectionContract {

    private val observer: MutableLiveData<CollectionState> by lazy {
        MutableLiveData<CollectionState>()
    }

    override val state: LiveData<CollectionState>
        get() = observer

    override fun getData(collection_id: String) {
        endpoint.getByCollection(collection_id)
            .map<CollectionState>(CollectionState::Result)
            .onErrorReturn(CollectionState::Error)
            .toFlowable()
            .startWith(CollectionState.Loading)
            .subscribe(observer::postValue)
            .let { return@let App.disposable::addAll }
    }

    override fun onCleared() {
        super.onCleared()
        App.disposable.clear()
    }
}