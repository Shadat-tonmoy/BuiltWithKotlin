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
//        filter.clearSubFilters()
        // copying to newBitmap for manipulation
        var newBitmap: Bitmap = oldBitmap.copy(Bitmap.Config.ARGB_8888, true)

        newBitmap = filter.processFilter(newBitmap)

        return newBitmap
    }

    private fun sepiaFilter (rgbAValues: RGBAValue) : RGBAValue
    {
        val oldRed = rgbAValues.red
        val oldGreen = rgbAValues.green
        val oldBlue = rgbAValues.blue

        val newRed = (0.393 * oldRed + 0.769 * oldGreen + 0.189 * oldBlue)
        val newGreen = (0.349 * oldRed + 0.686 * oldGreen + 0.168 * oldBlue)
        val newBlue = (0.272 * oldRed + 0.534 * oldGreen + 0.131 * oldBlue)

        return RGBAValue(newRed, newGreen, newBlue)
    }

    private fun greyFilter (rgbAValues: RGBAValue) : RGBAValue
    {
        val oldRed = rgbAValues.red
        val oldGreen = rgbAValues.green
        val oldBlue = rgbAValues.blue

        val intensity = ((oldRed + oldBlue + oldGreen) / 3).toInt().toDouble()

        return RGBAValue(intensity, intensity, intensity)
    }
}