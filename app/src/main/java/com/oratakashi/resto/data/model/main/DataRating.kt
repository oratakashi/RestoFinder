package com.oratakashi.resto.data.model.main

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class DataRating(
    @SerializedName("aggregate_rating") val rating: String?,
    @SerializedName("votes") val votes: Int?
) : Parcelable