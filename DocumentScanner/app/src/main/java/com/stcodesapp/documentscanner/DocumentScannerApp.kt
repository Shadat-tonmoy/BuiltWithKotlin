package com.stcodesapp.documentscanner

import android.app.Application
import com.tigerit.pothghat.di.application.ApplicationComponent
import com.tigerit.pothghat.di.application.ApplicationModule
import com.tigerit.pothghat.di.application.DaggerApplicationComponent

class DocumentScannerApp : Application()
{

    val appComponent:ApplicationComponent by lazy {
        DaggerApplicationComponent.builder()
            .applicationModule(ApplicationModule(this))
            .build()
    }



}