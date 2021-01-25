package com.test.ffmpegdemo.ui.helpers

import com.test.ffmpegdemo.constants.ConstValues
import java.io.File
import java.util.*

fun getFileNameFromPath(path:String) : String
{
    return File(path).name
}

fun getFormattedTime(timeInMillis : Long, showFullLength: Boolean = true) : String
{
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

    return if (showFullLength) ConstValues.DAYS_OF_WEEK[day - 1] + " , " + addLeadingZero(date) + " " + ConstValues.FULL_MONTHS[month] + " " + year + " | " + addLeadingZero(hours) + ":" + addLeadingZero(minutes) + ":" + addLeadingZero(seconds) + " " + ConstValues.AM_PM[ampm] else ConstValues.TRIMMED_DAYS_OF_WEEK[day - 1]+" , " + addLeadingZero(date) + " " + ConstValues.TRIMMED_MONTHS[month] + " " + year + " | " + addLeadingZero(hours) + ":" + addLeadingZero(minutes) + ":" + addLeadingZero(seconds) + " " + ConstValues.AM_PM[ampm]
}

fun addLeadingZero(n: Int): String? {
    return if (n < 10) "0$n" else n.toString() + ""
}

fun getFileSizeString(fileSize: Long): String?
{
    val finalSize = ""
    return if (fileSize < 1024)
    {
        String.format("%.2f", fileSize.toDouble()) + " B"
    }
    else
    {
        val fileSizeInKB = fileSize.toDouble() / 1024
        if (fileSizeInKB < 1024)
        {
            String.format("%.2f", fileSizeInKB.toDouble()) + " KB"
        }
        else
        {
            val fileSizeInMB = fileSizeInKB.toDouble() / 1024
            String.format("%.2f", fileSizeInMB.toDouble()) + " MB"
        }
    }
}

fun getTimeDurationString(duration: Long) : String
{
    val seconds = (duration.toInt() / 1000) % 60
    val minutes = (duration.toInt() / (1000 * 60) % 60)
    val hours = (duration.toInt() / (1000 * 60 * 60) % 24)
    var timeStr= ""
    if(hours > 0)
        timeStr += "$hours hr"
    if(minutes > 0){
        if(timeStr.isNotEmpty())
            timeStr += " "
        timeStr += "$minutes min"
    }
    if(timeStr.isNotEmpty())
        timeStr += " "
    timeStr += "$seconds secs"
    return timeStr
}