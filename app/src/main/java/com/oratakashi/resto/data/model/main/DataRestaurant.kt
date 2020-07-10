package com.oratakashi.resto.data.model.main

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class DataRestaurant(
    @SerializedName("id") val id: String?,
    @SerializedName("name") val name: String?,
    @SerializedName("location") val location: DataLocation?,
    @SerializedName("cuisines") val cuisines: String?,
    @SerializedName("timings") val timings: String?,
    @SerializedName("average_cost_for_two") val price: Int?,
    @SerializedName("price_range") val price_range: Int?,
    @SerializedName("currency") val currency: String?,
    @SerializedName("thumb") val photos_url: String?,
    @SerializedName("highlights") val highlights: List<String>?,
    @SerializedName("user_rating") val rating: DataRating?
) : Parcelable