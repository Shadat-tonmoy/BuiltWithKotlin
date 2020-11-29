package com.stcodesapp.documentscanner.models

data class CropArea(
    val x1 : Float = 0f,
    val y1 : Float = 0f,
    val x2 : Float = 0f,
    val y2 : Float = 0f,
    val x3 : Float = 0f,
    val y3 : Float = 0f,
    val x4 : Float = 0f,
    val y4 : Float = 0f){
    fun isEmpty() : Boolean
    {
        return x1 == 0f && y1 == 0f && x2 == 0f && y2 == 0f && x3 == 0f && y3 == 0f && x4 == 0f && y4 == 0f
    }
}