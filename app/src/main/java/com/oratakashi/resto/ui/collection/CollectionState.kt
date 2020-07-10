package com.oratakashi.resto.ui.collection

import com.oratakashi.resto.data.model.main.ResponseMain

sealed class CollectionState {
    object Loading : CollectionState()

    data class Result(val data: ResponseMain) : CollectionState()
    data class Error(val error: Throwable) : CollectionState()
}