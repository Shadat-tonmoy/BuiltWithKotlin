package com.test.ffmpegdemo.helper


import com.tigerit.signalmessenger.constants.Values
import java.util.*


private const val TAG = "DataHelper"


fun getHumanReadableTime(timeInMillis: Long, showFullLength: Boolean = true): String {
    if(timeInMillis == 0L) return "N/A"
    val calendar = Calendar.getInstance()
    calendar.timeInMillis = timeInMillis
    val date = calendar[Calendar.DAY_OF_MONTH]
    val month = calendar[Calendar.MONTH]
    val year = calendar[Calendar.YEAR]
    val day = calendar[Calendar.DAY_OF_WEEK]
    var hours = calendar[Calendar.HOUR]
    val minutes = calendar[Calendar.MINUTE]
    val seconds = calendar[Calendar.SECOND]
    val ampm = calendar[Calendar.AM_PM]
    hours = if (hours == 0) 12 else hours

    return if (showFullLength) Values.DAYS_OF_WEEK[day - 1] + "-" + addLeadingZero(date) + "-" + Values.FULL_MONTHS[month] + "-" + year + "-" + addLeadingZero(hours) + ":" + addLeadingZero(minutes) + ":" + addLeadingZero(seconds) + "-" + Values.AM_PM[ampm] else Values.TRIMMED_DAYS_OF_WEEK[day - 1]+"-" + addLeadingZero(date) + "-" + Values.TRIMMED_MONTHS[month] + "-" + year + "-" + addLeadingZero(hours) + ":" + addLeadingZero(minutes) + ":" + addLeadingZero(seconds) + "-" + Values.AM_PM[ampm]
}

fun addLeadingZero(n: Int): String? {
    return if (n < 10) "0$n" else n.toString() + ""
}

