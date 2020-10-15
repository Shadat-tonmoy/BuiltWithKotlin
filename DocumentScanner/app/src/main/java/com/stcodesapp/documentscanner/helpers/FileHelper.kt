package com.stcodesapp.documentscanner.helpers

import android.content.Context
import android.os.Environment
import com.stcodesapp.documentscanner.R

class FileHelper(private val context: Context)
{
    fun getSavedFilePath(): String {
        return Environment.getExternalStorageDirectory().path + "/" + context.packageName
    }

}