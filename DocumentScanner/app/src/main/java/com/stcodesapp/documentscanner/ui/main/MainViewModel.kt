package com.stcodesapp.documentscanner.ui.main

import android.R.attr.data
import android.os.Environment
import android.util.Log
import com.stcodesapp.documentscanner.DocumentScannerApp
import com.stcodesapp.documentscanner.base.BaseViewModel
import com.stcodesapp.documentscanner.database.AppDatabase
import java.io.*
import javax.inject.Inject
import kotlin.math.min


class MainViewModel @Inject constructor(val app: DocumentScannerApp) : BaseViewModel(app)
{
    @Inject lateinit var appDB : AppDatabase

    companion object{
        private const val TAG = "MainViewModel"
    }

    fun copySelectedImages(selectedImages: MutableList<String>?)
    {
        if(selectedImages!=null)
        {
            val appPrivateStorage = app.filesDir
            val documentTitle = System.currentTimeMillis()
            val outputDirPath = appPrivateStorage.absolutePath + File.separator + documentTitle
            for(imagePath in selectedImages)
            {

            }

        }
    }


}