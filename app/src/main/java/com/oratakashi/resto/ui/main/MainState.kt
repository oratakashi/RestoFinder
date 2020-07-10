package com.oratakashi.resto.ui.main

import com.oratakashi.resto.data.model.main.ResponseMain

/**
 * Class for State Management
 *
 * Class ini berfungsi untuk state management, digunakan agar membuat code menjadi bersih dan enak
 * untuk di baca
 */

sealed class MainState {
    object Loading : MainState()

    data class Result(val data: ResponseMain) : MainState()
    data class Error(val error: Throwable) : MainState()
}