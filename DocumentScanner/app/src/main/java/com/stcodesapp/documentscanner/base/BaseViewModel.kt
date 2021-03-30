package com.stcodesapp.documentscanner.base

import androidx.lifecycle.AndroidViewModel
import com.stcodesapp.documentscanner.DocumentScannerApp
import com.stcodesapp.documentscanner.database.core.AppDatabase
import com.stcodesapp.documentscanner.database.managers.DocumentManager
import com.stcodesapp.documentscanner.database.managers.ImageManager
import com.tigerit.pothghat.ui.helpers.ResourceProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import javax.inject.Inject

open class BaseViewModel(app : DocumentScannerApp) : AndroidViewModel(app) {

    @Inject lateinit var resourceProvider: ResourceProvider
    @Inject lateinit var appDB : AppDatabase
    @Inject lateinit var documentManager: DocumentManager
    @Inject lateinit var imageManager: ImageManager
    val ioCoroutine = CoroutineScope(IO)
    val uiCoroutine = CoroutineScope(Main)
    val context = app.applicationContext

}