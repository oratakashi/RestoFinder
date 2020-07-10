package com.oratakashi.resto.data.model.review

import com.google.gson.annotations.SerializedName

data class ResponseReview(
    @SerializedName("user_reviews") val reviews: List<DataReviews>?
)