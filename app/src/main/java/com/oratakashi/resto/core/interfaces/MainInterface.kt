package com.oratakashi.resto.core.interfaces

import com.oratakashi.resto.data.model.collection.DataCollection
import com.oratakashi.resto.data.model.main.DataRestaurant

interface MainInterface {
    fun openDetail(data: DataRestaurant?)
    fun openCollection(data: DataCollection?)
}