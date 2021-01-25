package com.test.backuprestoredemo.helper

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import android.text.TextUtils
import android.util.Log
import com.tigerit.filelogger.FileLogger
import java.io.*
import kotlin.math.min

@SuppressLint("LogNotTimber")
class FileHelper(private val context: Context)
{
    companion object{
        private const val TAG = "FileHelper"
    }

    fun copyFile(contentDescriber: Uri) : String?
    {
        val fileType = context.contentResolver.getType(contentDescriber)
        //val base = getBaseDirectory(context, fileType, sent)
        val base = File(getExternalStorageDir(), "test_backup")
        val outputFile = File(base, "" + System.currentTimeMillis() + getExtensionFromURI(contentDescriber))
        outputFile.deleteOnExit()
        Log.e(TAG, "output file path: ${outputFile.absolutePath}")

        var inputStream: InputStream? = null
        var out: OutputStream? = null
        val maxBufferSize = 8 * 1024 * 1024
        try
        {
            // open the user-picked file for reading:
            inputStream = context.contentResolver.openInputStream(contentDescriber)
            val bytesAvailable = inputStream!!.available()
            val bufferSize = min(bytesAvailable, maxBufferSize)
            // open the output-file:
//            out = FileOutputStream(File("some/path/to/a/writable/file"))
            out = FileOutputStream(outputFile)
            // copy the content:
            val buffer = ByteArray(bufferSize)
            var len = 0
            while (inputStream.read(buffer).also {
                    len = it
                } != -1) {
                out.write(buffer, 0, len)
            }
            // Contents are copied!
            return outputFile.absolutePath
        }
        catch (e : Exception)
        {
            FileLogger.getInstance()?.printStackTrace(e)
            e.printStackTrace();
        }
        finally
        {
            inputStream?.close()
            out?.close()
        }
        return null
    }

    fun copyFile(sourcePath : String, destinationPath : String) : String?
    {
        val outputFile = File(destinationPath)
        outputFile.deleteOnExit()
        val inputFile = File(sourcePath)
        Log.e(TAG, "output file path: ${outputFile.absolutePath}")
        var inputStream: InputStream? = null
        var out: OutputStream? = null
        val maxBufferSize = 8 * 1024 * 1024
        try
        {
            // open the user-picked file for reading:
            inputStream = FileInputStream(inputFile)
            val bytesAvailable = inputStream!!.available()
            val bufferSize = min(bytesAvailable, maxBufferSize)
            // open the output-file:
//            out = FileOutputStream(File("some/path/to/a/writable/file"))
            out = FileOutputStream(outputFile)
            // copy the content:
            val buffer = ByteArray(bufferSize)
            var len = 0
            while (inputStream.read(buffer).also {
                    len = it
                } != -1) {
                out.write(buffer, 0, len)
            }
            // Contents are copied!
            return outputFile.absolutePath
        }
        catch (e : Exception)
        {
            FileLogger.getInstance()?.printStackTrace(e)
            e.printStackTrace();
        }
        finally
        {
            inputStream?.close()
            out?.close()
        }
        return null
    }

    private fun getExtensionFromURI(uri: Uri) : String?
    {
        val extensionFromUri = getExtensionFromPath(uri.toString())
        if(extensionFromUri!=null && (extensionFromUri.length in 3..4) )  return extensionFromUri

        val cursor = context.contentResolver.query(uri, null, null, null, null)
        cursor?.let {
            val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            cursor.moveToFirst()
            val name = cursor.getString(nameIndex)
            return getExtensionFromPath(name)
        }
        return null
    }

    fun getFileNameFromURI(uri: Uri?) : String?
        {   /*
         * Get the file's content URI from the incoming Intent,
         * then query the server app to get the file's display name
         * and size.
         */
        uri?.let { returnUri ->
            context.contentResolver.query(returnUri, null, null, null, null)
        }?.use { cursor ->
            /*
             * Get the column indexes of the data in the Cursor,
             * move to the first row in the Cursor, get the data,
             * and display it.
             */
            val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
//            val sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE)
            cursor.moveToFirst()
            return cursor.getString(nameIndex)
        }
        return ""
    }

    fun getFileNameFromPath(name: String) : String?
    {
        Log.e("file-preview", "getFileNameFromPath: $name")
        if (!TextUtils.isEmpty(name)) {
            val index = name.lastIndexOf("/")
            index.let {
                if (index >= 0) {
                    return name.substring(index+1)
                }
            }
        }
        return ""
    }

    private fun getExtension(name: String?): String? {
        if (!TextUtils.isEmpty(name)) {
            val index = name?.lastIndexOf("/")
            if (index != null) {
                if (index >= 0) {
                    return "." + name.substring(index+1)
                }
            }
        }
        return ""
    }

    private fun getExtensionFromPath(path: String?): String? {
        path?.let {
            if(it.contains(".")) return it.substring(it.lastIndexOf('.'))
        }
        return null
    }

    fun create(filePath: String, fileContent: String): Boolean
    {
        val file = File(filePath)
        if(file.exists()) file.delete()
        return try {
            val fileOutputStream = FileOutputStream(File(filePath))
            fileOutputStream.write(fileContent.toByteArray())
            fileOutputStream.close()
            true
        } catch (fileNotFound: FileNotFoundException) {
            false
        } catch (ioException: IOException) {
            false
        }
    }

    private fun getExternalStorageDir() : String
    {
        return context.getExternalFilesDir(null)!!.absolutePath
    }
}