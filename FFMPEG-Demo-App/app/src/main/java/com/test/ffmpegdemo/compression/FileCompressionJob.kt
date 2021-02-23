package com.test.ffmpegdemo.compression

import android.content.Context
import android.media.MediaDataSource
import android.net.Uri
import android.util.Log
import androidx.annotation.RequiresApi
import com.test.ffmpegdemo.helper.getHumanReadableTime
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

class FileCompressionJob(val context: Context, val listener : Listener)
{
    companion object{
        private const val TAG = "FileCompressionJob"
    }
    private val MB = 1024 * 1024L

    interface Listener{
        fun onCompressionUpdate(msg : String)
        fun onCompressionCompleted(msg : String?)
        fun onCompressedFileSaved(msg : String)
        fun onCompressionError(msg : String)
    }

    @RequiresApi(26)
    fun compressFile(fileUri: Uri)
    {
        CoroutineScope(Dispatchers.IO).launch {
            compress(fileUri)
        }

    }

    fun getVideoMaxSize(): Long {
        return 1000 * MB
    }

    @RequiresApi(26)
    private fun compress(fileUri: Uri): MediaDataSource?
    {
        var options: InMemoryTranscoder.Options? = null
        //val transcoder = InMemoryTranscoder(context, File(filePath),options, getVideoMaxSize())


        InMemoryTranscoder(context, fileUri,options, getVideoMaxSize()).use{ transcoder ->
            if (transcoder.isTranscodeRequired) {
                val mediaStream = transcoder.transcode(object  : InMemoryTranscoder.Progress{
                    override fun onSuccess(successMessage: String?) {
                        listener.onCompressionCompleted(successMessage)
                    }

                    override fun onProgress(percent: Int) {
                        Log.e(TAG, "compress: $percent" )
                        listener.onCompressionUpdate("Compression in progress : $percent%")
                    }

                }, null)

                Log.e(TAG, "mediaStream: $mediaStream size : ${mediaStream.stream.available()}" )
                val outputFilePath = writeStreamToFile(mediaStream.stream)
                listener.onCompressedFileSaved("Compression in completed. File is saved at $outputFilePath")


            }
            else {
                listener.onCompressionError(transcoder.compressionErrorMsg)
            }
        }
        return null
    }

    private fun writeStreamToFile(input : InputStream) : String?
    {
        try {
            val backupDirectory = File(getExternalStorageDir(), "test_backup")
            if (!backupDirectory.exists()) backupDirectory.mkdirs()
            val testFile = File("${backupDirectory.absolutePath}${File.separator}${getFormattedTime(System.currentTimeMillis())}.mp4")

            FileOutputStream(testFile).use { output ->
                val buffer = ByteArray(4 * 1024) // or other buffer size
                var read: Int = 0
                while (input.read(buffer).also({ read = it }) != -1) {
                    output.write(buffer, 0, read)
                }
                output.flush()
                return testFile.absolutePath
            }
        }catch (e : Exception){
            e.printStackTrace()
        } finally {
            input.close()
        }
        return null
    }

    private fun getFormattedTime(timeStamp : Long) : String
    {
        return getHumanReadableTime(timeStamp)
    }

    private class DataInfo private constructor(private val file: File, private val length: Long, private val random: ByteArray, private val hash: String)

    private fun getExternalStorageDir() : String
    {
        return context.getExternalFilesDir(null)!!.absolutePath
    }

}