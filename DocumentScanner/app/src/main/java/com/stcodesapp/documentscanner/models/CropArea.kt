package com.stcodesapp.documentscanner.models

data class CropArea(
    val left : Double = 0.0,
    val right : Double = 0.0,
    val top : Double = 0.0,
    val bottom : Double = 0.0){
    fun isEmpty(): Boolean { return left == 0.0
            && right == 0.0
            && top == 0.0
            && bottom == 0.0 }
}