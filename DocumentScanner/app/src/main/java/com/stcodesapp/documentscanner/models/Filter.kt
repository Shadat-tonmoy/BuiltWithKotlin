package com.stcodesapp.documentscanner.models

data class Filter (var title : String, var imagepath : String, var type : FilterType, var isPaid : Boolean = false)

data class RGBAValue(var red : Double, var green : Double, var blue : Double, var alpha : Double = 1.0)

data class BrightenFilter(var brightnessValue : Int)
data class LightenFilter(var contrastValue : Float)
data class CustomFilter(var brightnessValue : Int, var contrastValue : Int)

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