package com.oratakashi.resto.data.model.review

import com.google.gson.annotations.SerializedName

data class DataReview(
    @SerializedName("rating") val rating: Float?,
    @SerializedName("id") val id: Int?,
    @SerializedName("review_time_friendly") val time: String?,
    @SerializedName("review_text") val review_text: String?,
    @SerializedName("user") val user: DataUsers?
)