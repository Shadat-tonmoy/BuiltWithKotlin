package com.stcodesapp.documentscanner.helpers

import android.net.Uri
import android.os.Build
import androidx.core.content.FileProvider
import androidx.documentfile.provider.DocumentFile
import com.google.gson.Gson
import com.stcodesapp.documentscanner.BuildConfig
import com.stcodesapp.documentscanner.models.CropArea
import com.stcodesapp.documentscanner.models.SavedFile
import com.theartofdev.edmodo.cropper.Polygon
import java.io.File

fun getFileNameFromPath(path:String) : String
{
    return File(path).name
}

fun getCropAreaFromPolygon(polygon: Polygon) : CropArea
{
    return CropArea(polygon.topLeftX,polygon.topLeftY,polygon.topRightX,polygon.topRightY,polygon.bottomLeftX,polygon.bottomLeftY,polygon.bottomRightX,polygon.bottomRightY,polygon.xRatio,polygon.yRatio)
}

fun getCropAreaJsonFromPolygon(polygon: Polygon) : String
{
    return Gson().toJson(getCropAreaFromPolygon(polygon),CropArea::class.java)
}

fun getPolygonFromCropArea(cropArea: CropArea) : Polygon
{
    return Polygon(cropArea.topLeftX,cropArea.topLeftY,cropArea.topRightX,cropArea.topRightY,cropArea.bottomLeftX,cropArea.bottomLeftY,cropArea.bottomRightX,cropArea.bottomRightY,cropArea.xRatio,cropArea.yRatio)
}

fun getPolygonFromCropAreaJson(cropAreaJSON: String) : Polygon
{
    val cropArea =  Gson().fromJson(cropAreaJSON,CropArea::class.java)
    return getPolygonFromCropArea(cropArea)
}

fun isValidPolygon(polygon: Polygon) : Boolean
{
    return polygon.topLeftX > 0 && polygon.topLeftY > 0 && polygon.topRightX > 0 && polygon.topRightY > 0 &&polygon.bottomLeftX > 0 && polygon.bottomLeftY > 0 &&polygon.bottomRightX > 0 && polygon.bottomRightY > 0
}

fun isAndroidX() : Boolean
{
    return Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q
}

fun getSavedFileListFromDocumentFile(documentFileList : List<DocumentFile>) : MutableList<SavedFile>
{
    val savedFileList = mutableListOf<SavedFile>()
    for(documentFile in documentFileList)
    {
        val name = documentFile.name ?: ""
        val pathString = documentFile.uri.toString()
        val fileSize = documentFile.length()
        val lastModified = documentFile.lastModified()
        val fileUri = documentFile.uri
        val relativePath = documentFile.parentFile?.name ?: ""
        savedFileList.add(SavedFile(name,pathString,relativePath,lastModified,fileSize,fileUri))
    }
    return savedFileList
}

fun getSavedFileListFromFile(fileList : List<File>) : MutableList<SavedFile>
{
    val savedFileList = mutableListOf<SavedFile>()
    for(file in fileList)
    {
        val name = file.name ?: ""
        val pathString = file.absolutePath
        val fileSize = file.length()
        val lastModified = file.lastModified()
        val fileUri = Uri.fromFile(file)
        val relativePath = file.parentFile?.name ?: ""
        savedFileList.add(SavedFile(name,pathString,relativePath,lastModified,fileSize,fileUri))
    }
    return savedFileList
}