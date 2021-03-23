package com.stcodesapp.documentscanner.tasks

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.graphics.pdf.PdfDocument.PageInfo
import android.net.Uri
import com.stcodesapp.documentscanner.constants.ConstValues.Companion.A4_PAPER_HEIGHT
import com.stcodesapp.documentscanner.constants.ConstValues.Companion.A4_PAPER_WIDTH
import com.stcodesapp.documentscanner.constants.ConstValues.Companion.MIN_IMAGE_DIMEN
import com.stcodesapp.documentscanner.database.entities.Image
import com.stcodesapp.documentscanner.helpers.FilterHelper
import com.stcodesapp.documentscanner.helpers.getPolygonFromCropAreaJson
import com.stcodesapp.documentscanner.models.ImageToPDFProgress
import com.stcodesapp.documentscanner.scanner.getWarpedImage
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
        init { System.loadLibrary("native-lib") }
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
                val polygon = getPolygonFromCropAreaJson(cropAreaJson)
                val srcBitmap = bitmap
                val dstBitmap = Bitmap.createBitmap(A4_PAPER_WIDTH,A4_PAPER_HEIGHT, Bitmap.Config.ARGB_8888)
                polygon.multiplyWithRatio()
                getWarpedImage(srcBitmap, dstBitmap, polygon)
                bitmap = dstBitmap
            }

            if (image.rotationAngle > 0f)
            {
                bitmap = bitmap?.let { bitmapUtil.rotateBitmap(it, image.rotationAngle.toFloat()) }
            }

            bitmap = bitmap?.let { FilterHelper(context).applyFilter(it,image) }

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
            //delay(5000)
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