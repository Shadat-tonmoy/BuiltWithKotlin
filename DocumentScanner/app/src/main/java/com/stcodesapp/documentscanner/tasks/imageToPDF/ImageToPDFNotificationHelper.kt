package com.stcodesapp.documentscanner.tasks.imageToPDF

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.stcodesapp.documentscanner.R

class ImageToPDFNotificationHelper(private val context: Context)
{
    companion object{
        private const val TAG = "ImageToPDFNotification"
    }

    fun getNotification(title : String, message : String, progress : Int = 0) : Notification
    {
        val channelId = createNotificationChannel()
        var builder = NotificationCompat.Builder(context, channelId)
            .setColor(ContextCompat.getColor(context, R.color.colorPrimary))
            .setSmallIcon(R.drawable.pdf_icon)
            .setContentTitle(title)
            .setAutoCancel(true)
        if(!message.isNullOrEmpty())
        {
            builder = builder.setContentText(message)
        }
        val inDeterminate = progress <= 0
        Log.e(TAG, "getNotification: progress : $progress, title : $title, message : $message")
        builder = builder
            .setProgress(100,progress, inDeterminate)
            .setContentText("$progress%")

        return builder.build()
    }

    private fun createNotificationChannel():String
    {
        val channelId = "image-to-pdf-notification"
        val channelName = "FileSavingNotification"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            val descriptionText = "Saving File..."
            val importance = NotificationManager.IMPORTANCE_LOW
            val channel = NotificationChannel(channelId, channelName, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
        return channelId
    }

}