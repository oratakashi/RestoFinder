package com.oratakashi.resto.utils

import java.text.DecimalFormat

object Converter {
    fun numberFormat(number: Int): String {
        return DecimalFormat("###,###,###,###").format(number)
    }
}