package com.oratakashi.resto.data.model.collection

import com.google.gson.annotations.SerializedName

data class ResponseCollection(
    @SerializedName("collections") val data: List<DataCollections>?
)