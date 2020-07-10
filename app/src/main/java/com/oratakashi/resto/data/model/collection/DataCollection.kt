package com.oratakashi.resto.data.model.collection

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class DataCollection(
    @SerializedName("collection_id") val id: Int?,
    @SerializedName("res_count") val res_count: Int?,
    @SerializedName("image_url") val image_url: String?,
    @SerializedName("title") val title: String?
) : Parcelable