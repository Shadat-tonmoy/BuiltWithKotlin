package com.stcodesapp.documentscanner.ui.main

import android.util.Log
import com.stcodesapp.documentscanner.DocumentScannerApp
import com.stcodesapp.documentscanner.base.BaseViewModel
import com.stcodesapp.documentscanner.database.AppDatabase
import javax.inject.Inject

class MainViewModel @Inject constructor(val app: DocumentScannerApp) : BaseViewModel(app)
{
    @Inject lateinit var appDB : AppDatabase

    companion object{
        private const val TAG = "MainViewModel"
    }

    fun copySelectedImages(selectedImages: MutableList<String>?)
    {
        val appPrivateStorage = app.filesDir
        Log.e(TAG, "copySelectedImages: PrivateFilesDir ${appPrivateStorage.absolutePath} DB : $appDB" )

    }
}