package com.oratakashi.resto.ui.search

import com.oratakashi.resto.data.model.collection.ResponseCollection

/**
 * Class for State Management
 *
 * Class ini berfungsi untuk state management, digunakan agar membuat code menjadi bersih dan enak
 * untuk di baca
 */

sealed class CollectionState {
    object Loading : CollectionState()

    data class Result(val data: ResponseCollection) : CollectionState()
    data class Error(val error: Throwable) : CollectionState()
}