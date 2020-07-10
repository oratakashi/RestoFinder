package com.oratakashi.resto.data.model.main

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class DataMain(
    @SerializedName("restaurant") val detail: DataRestaurant?
) : Parcelable