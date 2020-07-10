package com.oratakashi.resto.data.model.review

import com.google.gson.annotations.SerializedName

data class DataReviews(
    @SerializedName("review") val detail: DataReview?
)