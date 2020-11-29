package com.stcodesapp.documentscanner.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import com.labters.documentscanner.libraries.NativeClass
import com.stcodesapp.documentscanner.models.CropArea
import java.io.ByteArrayOutputStream
import java.io.File

class BitmapUtil(private val context: Context)
{
    companion object {
        private const val TAG = "BitmapUtil"
    }
    fun getBitmapFromPath(path: String, reqWidth: Int, reqHeight: Int) : Bitmap?
    {
        return try
        {
            val bitmap: Bitmap? = getBitmapFromFile(File(path), reqWidth, reqHeight)
            bitmap
        } catch (e: OutOfMemoryError) {
            throw e
        } catch (e: Exception) {
            throw e
        }
    }

    private fun getBitmapFromFile(file: File, reqWidth: Int, reqHeight: Int): Bitmap?
    {
        return try {
            getBitmapFromUri(Uri.fromFile(file), reqWidth, reqHeight)
        }catch (e : Exception) {
            throw e
        }
    }


    private fun getBitmapFromUri(uri: Uri, reqWidth: Int, reqHeight: Int): Bitmap? {
        return try
        {
            val options = BitmapFactory.Options()
            options.inJustDecodeBounds = true
            val is1 = context.contentResolver.openInputStream(uri)
            BitmapFactory.decodeStream(is1, null, options)
            is1?.close()

            val sampleSize = calculateInSampleSize(options, reqWidth, reqHeight)
            options.inSampleSize = sampleSize
            options.outHeight = reqHeight
            options.outWidth = reqWidth

            options.inJustDecodeBounds = false
            options.inPreferredConfig = Bitmap.Config.ARGB_8888
            options.inMutable = true
            val inputStream = context.contentResolver.openInputStream(uri)
            val bitmap = BitmapFactory.decodeStream(inputStream, null, options)
            inputStream?.close()
            bitmap
        } catch (ex: java.lang.Exception) {
            ex.printStackTrace();
            throw ex
        } catch (error: OutOfMemoryError) {
            error.printStackTrace()
            throw error
        }
    }

    private fun calculateInSampleSize(options: BitmapFactory.Options, reqWidth: Int, reqHeight: Int): Int
    {
        val height = options.outHeight
        val width = options.outWidth
        var inSampleSize = 1
        if (height > reqHeight || width > reqWidth) {
            val halfHeight = height / 2
            val halfWidth = width / 2

            while (halfHeight / inSampleSize >= reqHeight
                && halfWidth / inSampleSize >= reqWidth
            ) {
                inSampleSize *= 2
            }
        }
        return inSampleSize
    }

    private fun compressBitmap(bitmap: Bitmap, quality: Int)
    {
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, stream)
    }

    fun rotateBitmap(source: Bitmap, angle: Float): Bitmap? {
        return try {
            val matrix = Matrix()
            matrix.postRotate(angle)
            Bitmap.createBitmap(
                source, 0, 0, source.width, source.height,
                matrix, true
            )
        } catch (e: java.lang.Exception) {
            source
        } catch (error: OutOfMemoryError) {
            source
        }
    }

    fun getCroppedBitmap(src: Bitmap, ca: CropArea): Bitmap {
        return NativeClass().getScannedBitmap(src, ca.x1, ca.y1, ca.x2, ca.y2, ca.x3, ca.y3, ca.x4, ca.y4);

    }

}