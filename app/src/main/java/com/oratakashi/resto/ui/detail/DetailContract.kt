package com.oratakashi.resto.ui.detail

import androidx.lifecycle.LiveData

/**
 * This is for Implement on ViewModel
 *
 * Fungsi Interface Contract yaitu hal2 atau kegiatan apa saja yang akan di lakukan oleh ViewModel
 * Semua sudah di Definisikan di sini, bertujuan untuk mempermudah team
 * tiap membaca viewmodel permodul
 */

interface DetailContract {
    val state: LiveData<DetailState>

    /**
     * Get Data Mean method for get Reviews on Detail Activity
     *
     * Fungsi Method getData digunakan untuk mendapatkan review yang berada di Activity
     * @param res_id String
     */

    suspend fun getData(res_id: String)
}