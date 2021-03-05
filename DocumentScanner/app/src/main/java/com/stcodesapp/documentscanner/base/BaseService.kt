package com.stcodesapp.documentscanner.base

import android.app.Service
import com.stcodesapp.documentscanner.DocumentScannerApp
import com.stcodesapp.documentscanner.di.application.ApplicationComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

abstract class BaseService : Service()
{
    val ioCoroutine = CoroutineScope(Dispatchers.IO)
    val uiCoroutine = CoroutineScope(Dispatchers.Main)
    val appComponent: ApplicationComponent by lazy {
        (application as DocumentScannerApp).appComponent
    }





}