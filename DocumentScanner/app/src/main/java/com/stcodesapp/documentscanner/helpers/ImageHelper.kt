package com.stcodesapp.documentscanner.helpers

import android.content.Context
import android.net.Uri
import java.io.*
import kotlin.math.min

class ImageHelper(private val context: Context)
{
    fun copyImage(source : Uri, destination : String) : Boolean
    {
        val outputFile = File(destination)
        outputFile.deleteOnExit()
        var inputStream: InputStream? = null
        var out: OutputStream? = null
        val maxBufferSize = 8 * 1024 * 1024
        try
        {
            inputStream = context.contentResolver.openInputStream(source)
            //inputStream = FileInputStream(source)
            if(inputStream != null)
            {
                val bytesAvailable = inputStream.available()
                val bufferSize = min(bytesAvailable, maxBufferSize)
                out = FileOutputStream(outputFile)
                val buffer = ByteArray(bufferSize)
                var len = 0
                while (inputStream.read(buffer).also {
                        len = it
                    } != -1) {
                    out.write(buffer, 0, len)
                }
                return true
            }


        }
        catch (e : Exception)
        {
            e.printStackTrace()
        }
        finally
        {
            inputStream?.close()
            out?.close()
        }
        return false
    }

}