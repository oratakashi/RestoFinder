package com.oratakashi.resto.data.model.review

import com.google.gson.annotations.SerializedName

data class DataUsers(
    @SerializedName("name") val name: String?,
    @SerializedName("profile_image") val profile_image: String?
)