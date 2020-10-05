package com.stcodesapp.documentscanner.ui.documentPages

import android.util.Log
import com.stcodesapp.documentscanner.DocumentScannerApp
import com.stcodesapp.documentscanner.base.BaseViewModel
import com.stcodesapp.documentscanner.database.AppDatabase
import com.stcodesapp.documentscanner.ui.main.MainViewModel
import javax.inject.Inject

class DocumentPagesViewModel @Inject constructor(private val app: DocumentScannerApp) : BaseViewModel(app)
{

    companion object
    {
        private const val TAG = "DocumentPagesViewModel"
    }

    @Inject lateinit var appDB : AppDatabase

    fun showInfo()
    {
        Log.e(TAG, "DB: $appDB" )

    }

}