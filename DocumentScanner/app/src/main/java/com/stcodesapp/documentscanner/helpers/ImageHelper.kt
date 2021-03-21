package com.stcodesapp.documentscanner.helpers

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import androidx.exifinterface.media.ExifInterface
import java.io.*
import kotlin.math.floor
import kotlin.math.min

class ImageHelper(private val context: Context)
{
    companion object{
        const val IMAGE_WIDTH_THRESHOLD = 720
        const val IMAGE_COMPRESSION_QUALITY = 70
        const val REQ_IMAGE_DIMEN = 2500
        private const val TAG = "ImageHelper"
    }

    fun copyImage(source : Uri, destination : String) : Boolean
    {
        val resizedBitmap = getResizedImageByThreshold(source, IMAGE_WIDTH_THRESHOLD)
        if(resizedBitmap != null)
        {
            saveBitmapInFile(resizedBitmap,destination);
            return true
        }
        return false
    }

    fun getResizedImageByThreshold(imageUri: Uri, dimensionThreshold: Int): Bitmap?
    {
        //val bitmap = getBitmapFromURI(imageUri)
        var bitmap = decodeBitmapFromUri(imageUri, REQ_IMAGE_DIMEN, REQ_IMAGE_DIMEN)
        if(bitmap != null){bitmap = adjustRotatedBitmap(bitmap,imageUri)}
        return getResizedBitmapByThreshold(bitmap,dimensionThreshold)
    }

    fun getResizedBitmapByThreshold(bitmap: Bitmap?, dimensionThreshold: Int): Bitmap?
    {
        if(bitmap != null)
        {
            val originalWidth = bitmap.width
            val originalHeight = bitmap.height
            var scaledWidth = originalWidth
            var scaledHeight = originalHeight

            if(originalWidth > originalHeight)
            {
                //for landscape image
                if (originalHeight > dimensionThreshold)
                {
                    val factor = dimensionThreshold.toDouble() / originalHeight
                    scaledHeight = dimensionThreshold
                    scaledWidth = (floor(originalWidth.toDouble() * factor).toInt())
                }
                else
                {
                    // no need to scale. already less then the threshold value
                    return bitmap
                }

            }
            else
            {
                //for portrait image
                if (originalWidth > dimensionThreshold)
                {
                    val factor = dimensionThreshold.toDouble() / originalWidth
                    scaledWidth = dimensionThreshold
                    scaledHeight = (floor(originalHeight.toDouble() * factor).toInt())
                }
                else
                {
                    // no need to scale. already less then the threshold value
                    return bitmap
                }

            }
            try
            {
                return bitmap?.let { Bitmap.createScaledBitmap(it, scaledWidth, scaledHeight,false) }

            }catch (e : java.lang.Exception)
            {
                e.printStackTrace()
            }

        }
        return null

    }



    fun decodeBitmapFromUri(uri: Uri, reqWidth: Int, reqHeight: Int): Bitmap?
    {
        return try
        {
            // First decode with inJustDecodeBounds=true to check dimensions
            val options =
                BitmapFactory.Options()
            options.inJustDecodeBounds = true
            val is1 = context.contentResolver.openInputStream(uri)
            BitmapFactory.decodeStream(is1, null, options)
            is1?.close()
            // Calculate inSampleSize
            val sampleSize =calculateInSampleSize(options, reqWidth, reqHeight)
            options.inSampleSize =sampleSize
            options.outHeight = reqHeight
            options.outWidth = reqWidth

            // Decode bitmap with inSampleSize set
            options.inJustDecodeBounds = false
            options.inPreferredConfig = Bitmap.Config.ARGB_8888
            options.inMutable = true
            val inputStream = context.contentResolver.openInputStream(uri)
            val bitmap =
                BitmapFactory.decodeStream(inputStream, null, options)
            inputStream?.close()
            bitmap
        } catch (ex: java.lang.Exception) {
            ex.printStackTrace()
            null
        } catch (error: OutOfMemoryError) {
            null
        }
    }

    private fun calculateInSampleSize(options: BitmapFactory.Options, reqWidth: Int, reqHeight: Int): Int
    {
        // Raw height and width of image
        val height = options.outHeight
        val width = options.outWidth
        var inSampleSize = 1
        if (height > reqHeight || width > reqWidth) {
            val halfHeight = height / 2
            val halfWidth = width / 2

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while (halfHeight / inSampleSize >= reqHeight
                && halfWidth / inSampleSize >= reqWidth
            ) {
                inSampleSize *= 2
            }
        }
        return inSampleSize
    }

    private fun adjustRotatedBitmap(bitmap: Bitmap, photoUri: Uri): Bitmap?
    {
        return try
        {
            val inputStream = context.contentResolver.openInputStream(photoUri)
            if(inputStream != null)
            {
                val exif = ExifInterface(inputStream)
                val orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)
                var rotatedBitmap: Bitmap? = null
                rotatedBitmap = when (orientation)
                {
                    ExifInterface.ORIENTATION_ROTATE_90 -> {
                        rotateBitmap(bitmap, 90f)
                    }
                    ExifInterface.ORIENTATION_ROTATE_180 -> {
                        rotateBitmap(bitmap, 180f)
                    }
                    ExifInterface.ORIENTATION_ROTATE_270 -> {
                        rotateBitmap(bitmap, 270f)
                    }
                    ExifInterface.ORIENTATION_NORMAL -> {

                        bitmap
                    }
                    else -> {
                        bitmap
                    }
                }
                rotatedBitmap
            }
            else
            {
                bitmap
            }

        } catch (ex: java.lang.Exception)
        {
            ex.printStackTrace()
            bitmap
        } catch (error: OutOfMemoryError)
        {
            bitmap
        }
    }

    private fun rotateBitmap(source: Bitmap, angle: Float): Bitmap? {
        return try
        {
            val matrix = Matrix()
            matrix.postRotate(angle)
            Bitmap.createBitmap(source, 0, 0, source.width, source.height, matrix, true)
        } catch (e: Exception)
        {
            e.printStackTrace()
            source
        }
        catch (error: OutOfMemoryError)
        {
            error.printStackTrace()
            source
        }
    }

    fun saveBitmapInFile(bitmap: Bitmap, outputFilePath : String, format : Bitmap.CompressFormat = Bitmap.CompressFormat.JPEG, quality : Int = 100) : String
    {
        val out = FileOutputStream(outputFilePath)
        bitmap.compress(format, quality, out)
        return outputFilePath
    }
}