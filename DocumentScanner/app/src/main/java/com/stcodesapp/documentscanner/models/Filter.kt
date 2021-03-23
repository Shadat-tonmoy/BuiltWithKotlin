package com.stcodesapp.documentscanner.models

data class Filter (var title : String, var imagepath : String, var type : FilterType, var isPaid : Boolean = false)

data class RGBAValue(var red : Double, var green : Double, var blue : Double, var alpha : Double = 1.0)

data class BrightenFilter(var brightnessValue : Int)
data class LightenFilter(var contrastValue : Float)
data class CustomFilter(var brightnessValue : Int, var contrastValue : Float)
data class PaperEffectFilter(var blockSize : Int, var c : Double)

enum class FilterType{
    CUSTOM_FILTER,
    DEFAULT,
    GRAY_SCALE,
    BLACK_AND_WHITE,
    PAPER,
    POLISH,
    BRIGHTEN,
    LIGHTEN
}

fun getFilterTypeFromName(filterName : String) : FilterType
{
    when(filterName)
    {
        "CUSTOM_FILTER" -> return FilterType.CUSTOM_FILTER
        "DEFAULT" -> return FilterType.DEFAULT
        "GRAY_SCALE" -> return FilterType.GRAY_SCALE
        "BLACK_AND_WHITE" -> return FilterType.BLACK_AND_WHITE
        "PAPER" -> return FilterType.PAPER
        "POLISH" -> return FilterType.POLISH
        "BRIGHTEN" -> return FilterType.BRIGHTEN
        "LIGHTEN" -> return FilterType.LIGHTEN
    }
    return FilterType.DEFAULT
}