package com.oratakashi.resto.data.model.main

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class DataLocation(
    @SerializedName("address") val address: String?,
    @SerializedName("locality") val locality: String?,
    @SerializedName("city") val city: String?,
    @SerializedName("latitude") val lat: String?,
    @SerializedName("longitude") val long: String?
) : Parcelable