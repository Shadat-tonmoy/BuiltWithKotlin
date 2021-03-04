package com.stcodesapp.documentscanner.tasks

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.graphics.pdf.PdfDocument.PageInfo
import android.net.Uri
import com.google.gson.Gson
import com.stcodesapp.documentscanner.constants.ConstValues
import com.stcodesapp.documentscanner.constants.ConstValues.Companion.MIN_IMAGE_DIMEN
import com.stcodesapp.documentscanner.database.entities.Image
import com.stcodesapp.documentscanner.helpers.FilterHelper
import com.stcodesapp.documentscanner.models.CropArea
import com.stcodesapp.documentscanner.models.ImageToPDFProgress
import com.stcodesapp.documentscanner.utils.BitmapUtil
import java.io.IOException

class ImageToPdfTask(private val context : Context)
{

    interface Listener{
        fun onImageToPDFProgressUpdate(progress : ImageToPDFProgress)
    }
    companion object{
        const val PADDING = 40
        const val MARGIN = 20
        private const val TAG = "ImageToPdfTask"
    }

    suspend fun createPdf(imageList : List<Image>, pdfFileUri : Uri, callBack : Listener): Boolean
    {
        val bitmapUtil = BitmapUtil(context)
        var pdfPageNumber = 1
        val document = PdfDocument()
        for (image in imageList)
        {
            val imageUri: String = image.path
            var bitmap: Bitmap? = bitmapUtil.getBitmapFromPath(imageUri, MIN_IMAGE_DIMEN, MIN_IMAGE_DIMEN)
            if (bitmap == null) {
                pdfPageNumber++
                continue
            }
            bitmap = Bitmap.createScaledBitmap(bitmap, bitmap.width, bitmap.height, true)

            val cropAreaJson = image.cropArea

            if (cropAreaJson!=null && cropAreaJson.isNotEmpty())
            {
                val cropArea = Gson().fromJson(cropAreaJson,CropArea::class.java)
                bitmap = bitmapUtil.getCroppedBitmap(bitmap, cropArea)
            }

            if (image.rotationAngle > 0f)
            {
                bitmap = bitmap?.let { bitmapUtil.rotateBitmap(it, image.rotationAngle.toFloat()) }
            }

            if(image.filterName.isNotEmpty() && image.filterName != ConstValues.DEFAULT_FILTER)
            {
                bitmap = bitmap?.let { FilterHelper(context).applyFilterByName(it,image.filterName) }
            }

            val pageInfo = PageInfo.Builder(bitmap!!.width + PADDING, bitmap.height + PADDING, pdfPageNumber).create()
            val page = document.startPage(pageInfo)
            val canvas = page.canvas
            val paint = Paint()
            paint.color = Color.parseColor("#ffffff")
            canvas.drawPaint(paint)
            canvas.drawBitmap(bitmap, MARGIN.toFloat(), MARGIN.toFloat(), null)
            document.finishPage(page)
            callBack.onImageToPDFProgressUpdate(ImageToPDFProgress(pdfPageNumber,imageList.size))
            pdfPageNumber++
        }
        try
        {
            val outputStream = context.contentResolver.openOutputStream(pdfFileUri)
            if(outputStream != null)
            {
                document.writeTo(outputStream)
            }
        } catch (e: IOException) {
            e.printStackTrace()
            return false
        }
        // close the document
        document.close()
        return true
    }

}