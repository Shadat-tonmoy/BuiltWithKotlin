package com.stcodesapp.documentscanner.tasks

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.graphics.pdf.PdfDocument.PageInfo
import com.stcodesapp.documentscanner.constants.ConstValues.Companion.MIN_IMAGE_DIMEN
import com.stcodesapp.documentscanner.database.entities.Image
import com.stcodesapp.documentscanner.helpers.FileHelper
import com.stcodesapp.documentscanner.utils.BitmapUtil
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.*

class ImageToPdfTask(private val context : Context, private val imageList : List<Image>, private val fileTitle : String)
{
    companion object{
        const val PADDING = 40
        const val MARGIN = 20
    }

    private fun createPdf(): Boolean
    {
        val bitmapUtil = BitmapUtil(context)
        var pdfPageNumber = 1
        val document = PdfDocument()
        for (image in imageList) {
            val imagePath: String = image.path
            var bitmap: Bitmap? = bitmapUtil.getBitmapFromPath(imagePath, MIN_IMAGE_DIMEN, MIN_IMAGE_DIMEN)
            if (bitmap == null) {
                pdfPageNumber++
                continue
            }
            bitmap = Bitmap.createScaledBitmap(bitmap, bitmap.width, bitmap.height, true)
            if (image.rotationAngle > 0f) {
                bitmap = bitmapUtil.rotateBitmap(bitmap, image.rotationAngle.toFloat())
            }
            if (!image.cropArea!!.isEmpty()) {
                bitmap = bitmapUtil.getCroppedBitmap(bitmap, image.cropArea)
            }
            val pageInfo = PageInfo.Builder(bitmap!!.width + PADDING, bitmap.height + PADDING, pdfPageNumber).create()
            val page = document.startPage(pageInfo)
            val canvas = page.canvas
            val paint = Paint()
            paint.color = Color.parseColor("#ffffff")
            canvas.drawPaint(paint)
            canvas.drawBitmap(bitmap, MARGIN.toFloat(), MARGIN.toFloat(), null)
            document.finishPage(page)
            pdfPageNumber++
        }
        val targetPdf: String = getPDFFullPathToSave()
        val outputPDFFile = File(targetPdf)
        try {
            if (!outputPDFFile.exists()) outputPDFFile.createNewFile()
            document.writeTo(FileOutputStream(outputPDFFile))
        } catch (e: IOException) {
            e.printStackTrace()
            return false
        }
        // close the document
        document.close()
        return true
    }

    private fun getPDFFullPathToSave(): String
    {
        val fileHelper = FileHelper(context)
        val outputFolder = File(fileHelper.getSavedFilePath())
        if (!outputFolder.exists()) outputFolder.mkdirs()
        return "$outputFolder/$fileTitle"
    }

}