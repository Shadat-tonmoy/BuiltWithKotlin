package com.stcodesapp.documentscanner.helpers

import com.google.gson.Gson
import com.stcodesapp.documentscanner.models.CropArea
import com.theartofdev.edmodo.cropper.Polygon
import java.io.File

fun getFileNameFromPath(path:String) : String
{
    return File(path).name
}

fun getCropAreaFromPolygon(polygon: Polygon) : CropArea
{
    return CropArea(polygon.topLeftX,polygon.topLeftY,polygon.topRightX,polygon.topRightY,polygon.bottomLeftX,polygon.bottomLeftY,polygon.bottomRightX,polygon.bottomRightY)
}

fun getCropAreaJsonFromPolygon(polygon: Polygon) : String
{
    return Gson().toJson(getCropAreaFromPolygon(polygon),CropArea::class.java)
}