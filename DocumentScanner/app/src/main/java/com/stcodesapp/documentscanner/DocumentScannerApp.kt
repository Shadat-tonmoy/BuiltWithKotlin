package com.stcodesapp.documentscanner

import android.app.Application
import android.util.Log
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkRequest
import com.stcodesapp.documentscanner.backup.BackupWorker
import com.tigerit.pothghat.di.application.ApplicationComponent
import com.tigerit.pothghat.di.application.ApplicationModule
import com.tigerit.pothghat.di.application.DaggerApplicationComponent
import java.util.concurrent.TimeUnit

class DocumentScannerApp : Application()
{

    val appComponent:ApplicationComponent by lazy {
        DaggerApplicationComponent.builder()
            .applicationModule(ApplicationModule(this))
            .build()
    }

    override fun onCreate() {
        super.onCreate()

        /*val backupWorkRequest : WorkRequest =
            PeriodicWorkRequestBuilder<BackupWorker>(15,TimeUnit.MINUTES)
                .build()

        val myWorkRequest = WorkManager
            .getInstance(this)
            .enqueue(backupWorkRequest)
        Log.e("TAG", "onCreate: RegisteringBackupWorker")*/
    }



}