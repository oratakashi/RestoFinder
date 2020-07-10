package com.oratakashi.resto.ui.collection

import androidx.lifecycle.LiveData

interface CollectionContract {
    val state: LiveData<CollectionState>

    fun getData(collection_id: String)
}