package com.stcodesapp.documentscanner.helpers

import java.io.*
import kotlin.math.min

class ImageHelper
{
    private fun copyImage(source : String, destination : String) : Boolean
    {
        val outputFile = File(destination)
        outputFile.deleteOnExit()
        var inputStream: InputStream? = null
        var out: OutputStream? = null
        val maxBufferSize = 8 * 1024 * 1024
        try
        {
            inputStream = FileInputStream(source)

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