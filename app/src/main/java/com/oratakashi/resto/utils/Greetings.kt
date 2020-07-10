package com.oratakashi.resto.utils

import android.content.Context
import com.oratakashi.resto.R
import java.util.*

object Greetings {
    fun getTimes(context: Context): String {
        val date = Date()
        val cal = Calendar.getInstance()
        cal.time = date

        return when (cal[Calendar.HOUR_OF_DAY]) {
            in 12..17 -> context.getString(R.string.title_afternoon)
            in 18..23 -> context.getString(R.string.title_evening)
            in 1..3 -> context.getString(R.string.title_evening)
            else -> context.getString(R.string.title_morning)
        }
    }

    fun getGreetings(context: Context): String {
        val date = Date()
        val cal = Calendar.getInstance()
        cal.time = date

        return when (cal[Calendar.HOUR_OF_DAY]) {
            in 12..17 -> context.getString(R.string.title_good_afternoon)
            in 18..23 -> context.getString(R.string.title_good_evening)
            in 1..3 -> context.getString(R.string.title_good_evening)
            else -> context.getString(R.string.title_good_morning)
        }
    }
}