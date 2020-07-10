package com.oratakashi.resto.data.model.main

import com.google.gson.annotations.SerializedName

data class ResponseMain(
    @SerializedName("results_shown") val results_shown: Int?,
    @SerializedName("restaurants") val data: List<DataMain>?
)