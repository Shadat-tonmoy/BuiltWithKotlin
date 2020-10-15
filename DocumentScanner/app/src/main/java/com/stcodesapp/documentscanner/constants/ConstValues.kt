package com.stcodesapp.documentscanner.constants

class ConstValues
{
    companion object
    {
        const val PNG_EXTENSION = ".png"

        var FULL_MONTHS = arrayOf(
            "January",
            "February",
            "March",
            "April",
            "May",
            "June",
            "July",
            "August",
            "September",
            "October",
            "November",
            "December"
        )
        var TRIMMED_MONTHS = arrayOf(
            "Jan",
            "Feb",
            "Mar",
            "Apr",
            "May",
            "Jun",
            "Jul",
            "Aug",
            "Sep",
            "Oct",
            "Nov",
            "Dec"
        )
        var DAYS_OF_WEEK = arrayOf(
            "Sunday",
            "Monday",
            "Tuesday",
            "Wednesday",
            "Thursday",
            "Friday",
            "Saturday"
        )
        var TRIMMED_DAYS_OF_WEEK =
            arrayOf("Sun", "Mon", "Tue", "Wed", "Thr", "Fri", "Sat")
        var AM_PM = arrayOf("AM", "PM")

        const val MIN_IMAGE_DIMEN = 512

    }
}