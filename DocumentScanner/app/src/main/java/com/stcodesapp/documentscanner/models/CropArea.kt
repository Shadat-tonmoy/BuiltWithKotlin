package com.stcodesapp.documentscanner.models

data class CropArea(
    var x1 : Float = 0f,
    var y1 : Float = 0f,
    var x2 : Float = 0f,
    var y2 : Float = 0f,
    var x3 : Float = 0f,
    var y3 : Float = 0f,
    var x4 : Float = 0f,
    var y4 : Float = 0f){
    fun isEmpty() : Boolean
    {
        return x1 == 0f && y1 == 0f && x2 == 0f && y2 == 0f && x3 == 0f && y3 == 0f && x4 == 0f && y4 == 0f
    }

    fun empty()
    {
        x1 = 0f
        y1 = 0f
        x2 = 0f
        y2 = 0f
        x3 = 0f
        y3 = 0f
        x4 = 0f
        y4 = 0f

  }
}