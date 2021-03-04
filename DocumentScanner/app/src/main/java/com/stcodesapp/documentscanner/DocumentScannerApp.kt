package com.stcodesapp.documentscanner

import android.app.Application
import com.stcodesapp.documentscanner.di.application.ApplicationComponent
import com.stcodesapp.documentscanner.di.application.ApplicationModule
import com.stcodesapp.documentscanner.di.application.DaggerApplicationComponent

class DocumentScannerApp : Application()
{

    val appComponent: ApplicationComponent by lazy {
        DaggerApplicationComponent.builder()
            .applicationModule(ApplicationModule(this))
            .build()
    }



}