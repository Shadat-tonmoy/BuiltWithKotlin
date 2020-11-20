package com.stcodesapp.documentscanner.models

import com.stcodesapp.documentscanner.constants.enums.FilterType
import com.zomato.photofilters.imageprocessors.Filter

data class Filter (var title : String, var imagepath : String, var type : Filter)

data class RGBAValue(var red : Double, var green : Double, var blue : Double, var alpha : Double = 1.0)