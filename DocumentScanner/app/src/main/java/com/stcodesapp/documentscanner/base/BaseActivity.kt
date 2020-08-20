package com.stcodesapp.documentscanner.base

import androidx.appcompat.app.AppCompatActivity
import com.stcodesapp.documentscanner.DocumentScannerApp
import com.stcodesapp.documentscanner.di.activity.ActivityComponent
import com.stcodesapp.documentscanner.di.activity.modules.ActivityModule
import com.tigerit.pothghat.di.application.ApplicationComponent

open class BaseActivity : AppCompatActivity()
{
    val appComponent:ApplicationComponent by lazy {
        (application as DocumentScannerApp).appComponent
    }

    val activityComponent: ActivityComponent by lazy {
        appComponent.getActivityComponent(ActivityModule(this))
    }

}