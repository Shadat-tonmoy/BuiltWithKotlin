package com.stcodesapp.documentscanner.helpers

import android.content.Context
import android.graphics.Bitmap
import android.webkit.MimeTypeMap
import com.stcodesapp.documentscanner.models.Filter
import com.stcodesapp.documentscanner.models.FilterType
import com.stcodesapp.documentscanner.scanner.getGrayscaleImage

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

    suspend fun applyFilterByName(oldBitmap : Bitmap, filterName: String) : Bitmap
    {
        // copying to newBitmap for manipulation
        /*for(filter in FilterPack.getFilterPack(context))
        {
            if(filter.name == filterName)
            {
                var newBitmap: Bitmap = oldBitmap.copy(Bitmap.Config.ARGB_8888, true)

                newBitmap = filter.processFilter(newBitmap)

                return newBitmap
            }
        }*/
        return oldBitmap
    }

    fun getFilterList(imagePath : String) : List<Filter>
    {
        val filterList = mutableListOf<Filter>()
        filterList.add(Filter("Custom Filter",imagePath,FilterType.CUSTOM_FILTER))
        filterList.add(Filter("Default",imagePath,FilterType.DEFAULT))
        filterList.add(Filter("Gray Scale",imagePath,FilterType.GRAY_SCALE))
        filterList.add(Filter("B&W",imagePath,FilterType.BLACK_AND_WHITE))
        filterList.add(Filter("Brighten",imagePath,FilterType.BRIGHTEN))
        filterList.add(Filter("Lighten",imagePath,FilterType.LIGHTEN))
        return filterList
    }

    fun getFilteredThumbList(imagePath : String) : List<Filter>
    {
        val filterList = mutableListOf<Filter>()
        filterList.add(Filter("Custom Filter",getFilteredThumbFilePath(imagePath,FilterType.CUSTOM_FILTER),FilterType.CUSTOM_FILTER))
        filterList.add(Filter("Default",getFilteredThumbFilePath(imagePath,FilterType.DEFAULT),FilterType.DEFAULT))
        filterList.add(Filter("Gray Scale",getFilteredThumbFilePath(imagePath,FilterType.GRAY_SCALE),FilterType.GRAY_SCALE))
        filterList.add(Filter("B&W",getFilteredThumbFilePath(imagePath,FilterType.BLACK_AND_WHITE),FilterType.BLACK_AND_WHITE))
        filterList.add(Filter("Brighten",getFilteredThumbFilePath(imagePath,FilterType.BRIGHTEN),FilterType.BRIGHTEN))
        filterList.add(Filter("Lighten",getFilteredThumbFilePath(imagePath,FilterType.LIGHTEN),FilterType.LIGHTEN))
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
}