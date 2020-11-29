package com.stcodesapp.documentscanner.helpers

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.util.Log
import com.stcodesapp.documentscanner.constants.enums.FilterType
import com.stcodesapp.documentscanner.models.RGBAValue
import com.zomato.photofilters.FilterPack
import com.zomato.photofilters.imageprocessors.Filter

class FilterHelper(private val context: Context)
{
    suspend fun applyFilter(oldBitmap : Bitmap, filter: Filter) : Bitmap
    {
        // copying to newBitmap for manipulation
        var newBitmap: Bitmap = oldBitmap.copy(Bitmap.Config.ARGB_8888, true)

        newBitmap = filter.processFilter(newBitmap)

        return newBitmap
    }

    suspend fun applyFilterByName(oldBitmap : Bitmap, filterName: String) : Bitmap
    {
        // copying to newBitmap for manipulation
        for(filter in FilterPack.getFilterPack(context))
        {
            if(filter.name == filterName)
            {
                var newBitmap: Bitmap = oldBitmap.copy(Bitmap.Config.ARGB_8888, true)

                newBitmap = filter.processFilter(newBitmap)

                return newBitmap
            }
        }
        return oldBitmap
    }
}