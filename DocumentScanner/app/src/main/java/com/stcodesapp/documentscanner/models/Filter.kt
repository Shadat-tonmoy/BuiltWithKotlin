package com.stcodesapp.documentscanner.models

import com.stcodesapp.documentscanner.constants.enums.FilterType

data class Filter (var title : String, var imagepath : String, var type : FilterType)