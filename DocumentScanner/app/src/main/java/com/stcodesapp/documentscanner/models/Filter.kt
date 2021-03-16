package com.stcodesapp.documentscanner.models

data class Filter (var title : String, var imagepath : String, var type : FilterType)

data class RGBAValue(var red : Double, var green : Double, var blue : Double, var alpha : Double = 1.0)

enum class FilterType{
    CUSTOM_FILTER,
    DEFAULT,
    GRAY_SCALE,
    BLACK_AND_WHITE,
    BRIGHTEN,
    LIGHTEN
}