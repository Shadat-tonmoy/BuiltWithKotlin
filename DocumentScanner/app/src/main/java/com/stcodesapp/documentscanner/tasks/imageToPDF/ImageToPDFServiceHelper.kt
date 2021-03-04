package com.stcodesapp.documentscanner.tasks.imageToPDF

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import javax.inject.Inject

class ImageToPDFServiceHelper(private val context: Context)
{
    var imageToPDFService : ImageToPDFService? = null

    private val imageToPDfServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            imageToPDFService = (service as ImageToPDFService.ServiceBinder).getService()
        }

        override fun onServiceDisconnected(name: ComponentName) { imageToPDFService = null }
    }

    fun initService()
    {
        val serviceIntent = Intent(context, ImageToPDFService::class.java)
        context.startService(serviceIntent)
        context.bindService(serviceIntent, imageToPDfServiceConnection, Context.BIND_AUTO_CREATE)
    }

    fun destroyService()
    {
        val serviceIntent = Intent(context, ImageToPDFService::class.java)
        context.stopService(serviceIntent)
        imageToPDFService = null
    }
}