package com.stcodesapp.documentscanner.tasks.imageToPDF

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import javax.inject.Inject

class ImageToPDFServiceHelper(private val context: Context)
{

    interface Listener { fun onServiceConnected() }

    var imageToPDFService : ImageToPDFService? = null
    var listener : Listener? = null

    private val imageToPDfServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            imageToPDFService = (service as ImageToPDFService.ServiceBinder).getService()
            listener?.onServiceConnected()
        }

        override fun onServiceDisconnected(name: ComponentName) { imageToPDFService = null }
    }

    fun initService(listener: Listener)
    {
        this.listener = listener
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