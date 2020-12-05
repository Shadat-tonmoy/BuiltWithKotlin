package com.stcodesapp.documentscanner.helpers

import android.content.Context
import android.os.Environment
import com.stcodesapp.documentscanner.R
import java.io.File

class FileHelper(private val context: Context)
{
    fun getSavedFilePath(): String {
        return Environment.getExternalStorageDirectory().path + "/" + context.packageName
    }

    fun getPDFFullPathToSave(fileTitle: String): String
    {
        val outputFolder = File(getSavedFilePath())
        if (!outputFolder.exists()) outputFolder.mkdirs()
        return "$outputFolder/$fileTitle"
    }

}