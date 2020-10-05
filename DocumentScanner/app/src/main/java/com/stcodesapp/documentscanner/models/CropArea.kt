package com.stcodesapp.documentscanner.models

import java.io.Serializable

data class CropArea(
    val left : Double = 0.0,
    val right : Double = 0.0,
    val top : Double = 0.0,
    val bottom : Double = 0.0)