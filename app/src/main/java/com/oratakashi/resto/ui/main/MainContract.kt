package com.oratakashi.resto.ui.main

import androidx.lifecycle.LiveData

/**
 * This is for Implement on ViewModel
 *
 * Fungsi Interface Contract yaitu hal2 atau kegiatan apa saja yang akan di lakukan oleh ViewModel
 * Semua sudah di Definisikan di sini, bertujuan untuk mempermudah team
 * tiap membaca viewmodel permodul
 */

interface MainContract {
    val state: LiveData<MainState>
    val collection: LiveData<CollectionState>

    suspend fun getNearby(lat: String, lang: String)
    suspend fun getCollection(lat: String, lang: String)
}