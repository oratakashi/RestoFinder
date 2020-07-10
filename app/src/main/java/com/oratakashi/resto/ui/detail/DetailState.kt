package com.oratakashi.resto.ui.detail

import com.oratakashi.resto.data.model.review.ResponseReview

/**
 * Class for State Management
 *
 * Class ini berfungsi untuk state management, digunakan agar membuat code menjadi bersih dan enak
 * untuk di baca
 */

sealed class DetailState {
    object Loading : DetailState()

    data class Result(val data: ResponseReview) : DetailState()
    data class Error(val error: Throwable) : DetailState()
}