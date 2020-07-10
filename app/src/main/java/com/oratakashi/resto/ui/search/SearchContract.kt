package com.oratakashi.resto.ui.search

import androidx.lifecycle.LiveData

interface SearchContract {
    val state: LiveData<SearchState>
    val collection: LiveData<CollectionState>

    suspend fun getData(keyword: String, lat: String, lang: String)
    suspend fun getCollection(lat: String, lang: String)
}