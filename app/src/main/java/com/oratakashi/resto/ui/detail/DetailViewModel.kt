package com.oratakashi.resto.ui.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.oratakashi.resto.core.App
import com.oratakashi.resto.data.network.ApiEndPoint
import javax.inject.Inject

class DetailViewModel @Inject constructor(
    private val endpoint: ApiEndPoint
) : ViewModel(), DetailContract {

    private val observer: MutableLiveData<DetailState> by lazy {
        MutableLiveData<DetailState>()
    }

    override val state: LiveData<DetailState>
        get() = observer

    override suspend fun getData(res_id: String) {
        endpoint.getReviews(res_id)
            .map<DetailState>(DetailState::Result)
            .onErrorReturn(DetailState::Error)
            .toFlowable()
            .startWith(DetailState.Loading)
            .subscribe(observer::postValue)
            .let { return@let App.disposable::add }
    }

    override fun onCleared() {
        super.onCleared()
        App.disposable.clear()
    }
}