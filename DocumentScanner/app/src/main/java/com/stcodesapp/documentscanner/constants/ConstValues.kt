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
        const val DEFAULT_FILTER = "Default"
        const val OPEN_FILE = 1
        const val SHARE_FILE = 2
        const val DELETE_FILE = 3
        const val SAVE_DIRECTORY_NAME = "DocumentScanner~STCodesApp"
        const val OUTPUT_DIRECTORY_NAME = "Output"
        const val THUMB_SIZE = 256
        const val A4_PAPER_WIDTH = 420
        const val A4_PAPER_HEIGHT = 596

    }
}