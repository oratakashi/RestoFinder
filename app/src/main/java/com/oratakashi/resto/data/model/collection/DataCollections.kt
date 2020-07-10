package com.oratakashi.resto.data.model.collection

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class DataCollections(
    @SerializedName("collection") val detail: DataCollection?
) : Parcelable