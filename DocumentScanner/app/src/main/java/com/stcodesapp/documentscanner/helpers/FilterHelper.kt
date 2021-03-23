package com.stcodesapp.documentscanner.helpers

import android.content.Context
import android.graphics.Bitmap
import android.webkit.MimeTypeMap
import com.google.gson.Gson
import com.stcodesapp.documentscanner.database.entities.Image
import com.stcodesapp.documentscanner.models.*
import com.stcodesapp.documentscanner.scanner.*
import com.stcodesapp.documentscanner.ui.imageCrop.CropImageSingleItemViewModel

class FilterHelper(private val context: Context)
{
    /*suspend fun applyFilter(oldBitmap : Bitmap, filter: Filter) : Bitmap
    {
        // copying to newBitmap for manipulation
        var newBitmap: Bitmap = oldBitmap.copy(Bitmap.Config.ARGB_8888, true)

        newBitmap = filter.processFilter(newBitmap)

        return newBitmap
    }*/

    suspend fun applyFilter(oldBitmap : Bitmap, filter: Filter) : Bitmap
    {
        // copying to newBitmap for manipulation
        var newBitmap: Bitmap = oldBitmap.copy(Bitmap.Config.ARGB_8888, true)

        when(filter.type)
        {
            FilterType.GRAY_SCALE -> getGrayscaleImage(oldBitmap, newBitmap)

        }
        return newBitmap
    }

    fun applyFilter(oldBitmap : Bitmap, image : Image) : Bitmap
    {
        val filterName = image.filterName
        val filterJson = image.filterJson
        val paperEffectJson = image.paperEffectJson
        val customFilterJson = image.customFilterJson
        val filterType = getFilterTypeFromName(filterName)
        when(filterType)
        {
            FilterType.GRAY_SCALE -> getGrayscaleImage(oldBitmap,oldBitmap)
            FilterType.LIGHTEN ->
            {
                val filter = Gson().fromJson(filterJson,LightenFilter::class.java)
                getLightenImage(oldBitmap,oldBitmap,filter.contrastValue)
            }
            FilterType.BRIGHTEN ->
            {
                val filter = Gson().fromJson(filterJson,BrightenFilter::class.java)
                getBrightenImage(oldBitmap,oldBitmap,filter.brightnessValue)
            }
        }
        return oldBitmap
    }

    fun applyFilterByName(oldBitmap : Bitmap, image : Image) : Bitmap
    {
        val filterName = image.filterName
        val filterJson = image.filterJson
        val filterType = getFilterTypeFromName(filterName)
        when(filterType)
        {
            FilterType.GRAY_SCALE -> getGrayscaleImage(oldBitmap,oldBitmap)
            FilterType.LIGHTEN ->
            {
                val filter = Gson().fromJson(filterJson,LightenFilter::class.java)
                getLightenImage(oldBitmap,oldBitmap,filter.contrastValue)
            }
            FilterType.BRIGHTEN ->
            {
                val filter = Gson().fromJson(filterJson,BrightenFilter::class.java)
                getBrightenImage(oldBitmap,oldBitmap,filter.brightnessValue)
            }
        }
        return oldBitmap
    }



    fun getFilterList(imagePath : String) : List<Filter>
    {
        val filterList = mutableListOf<Filter>()
        filterList.add(Filter("Default",imagePath,FilterType.DEFAULT))
        filterList.add(Filter("Gray Scale",imagePath,FilterType.GRAY_SCALE))
        filterList.add(Filter("B&W",imagePath,FilterType.BLACK_AND_WHITE))
        filterList.add(Filter("Paper",imagePath,FilterType.PAPER,true))
        filterList.add(Filter("Polish",imagePath,FilterType.POLISH,true))
        filterList.add(Filter("Brighten",imagePath,FilterType.BRIGHTEN))
        filterList.add(Filter("Lighten",imagePath,FilterType.LIGHTEN))
        return filterList
    }

    fun getFilteredThumbFileName(imagePath: String, filterType: FilterType) : String
    {
        val extension = MimeTypeMap.getFileExtensionFromUrl(imagePath)
        val thumbFileName = "${getFileNameFromPath(imagePath).replace(".$extension","")}_$filterType.$extension"
        return thumbFileName
    }

    fun getFilteredThumbFilePath(imagePath: String, filterType: FilterType) : String
    {
        val extension = MimeTypeMap.getFileExtensionFromUrl(imagePath)
        val fileName = getFileNameFromPath(imagePath)
        val fileNameWithoutExtension = fileName.replace(extension,"")
        val thumbFilePath = "${imagePath.replace(fileName,fileNameWithoutExtension)}_$filterType.$extension"
        return thumbFilePath
    }

    fun getFilteredBitmap(filterType: FilterType, srcBitmap : Bitmap) : Bitmap
    {
        val dstBitmap = Bitmap.createBitmap(srcBitmap.width,srcBitmap.height, Bitmap.Config.ARGB_8888)
        when(filterType)
        {
            FilterType.GRAY_SCALE -> getGrayscaleImage(srcBitmap,dstBitmap)
            FilterType.BRIGHTEN -> getBrightenImage(srcBitmap,dstBitmap, CropImageSingleItemViewModel.BRIGHTEN_FILTER_VALUE)
            FilterType.LIGHTEN -> getLightenImage(srcBitmap,dstBitmap, CropImageSingleItemViewModel.LIGHTEN_FILTER_VALUE)
            else -> return srcBitmap
        }
        return dstBitmap
    }
}