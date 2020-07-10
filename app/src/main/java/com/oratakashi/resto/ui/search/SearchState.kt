package com.oratakashi.resto.ui.search

import com.oratakashi.resto.data.model.main.ResponseMain

sealed class SearchState {
    object Loading : SearchState()

    data class Result(val data: ResponseMain) : SearchState()
    data class Error(val error: Throwable) : SearchState()
}