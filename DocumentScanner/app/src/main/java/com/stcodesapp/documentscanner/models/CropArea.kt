package com.stcodesapp.documentscanner.models

data class CropArea(
    var topLeftX : Float = 0f,
    var topLeftY : Float = 0f,
    var topRightX : Float = 0f,
    var topRightY : Float = 0f,
    var bottomLeftX : Float = 0f,
    var bottomLeftY : Float = 0f,
    var bottomRightX : Float = 0f,
    var bottomRightY : Float = 0f,
    var xRatio : Float = 1f,
    var yRatio : Float = 1f){
    fun isEmpty() : Boolean
    {
        return topLeftX == 0f && topLeftY == 0f && topRightX == 0f && topRightY == 0f && bottomLeftX == 0f && bottomLeftY == 0f && bottomRightX == 0f && bottomRightY == 0f
    }

    fun empty()
    {
        topLeftX = 0f
        topLeftY = 0f
        topRightX = 0f
        topRightY = 0f
        bottomLeftX = 0f
        bottomLeftY = 0f
        bottomRightX = 0f
        bottomRightY = 0f

  }

    override fun toString(): String {
        return "CropArea(topLeftX=$topLeftX, topLeftY=$topLeftY, topRightX=$topRightX, topRightY=$topRightY, bottomLeftX=$bottomLeftX, bottomLeftY=$bottomLeftY, bottomRightX=$bottomRightX, bottomRightY=$bottomRightY, xRatio=$xRatio, yRatio=$yRatio)"
    }


}