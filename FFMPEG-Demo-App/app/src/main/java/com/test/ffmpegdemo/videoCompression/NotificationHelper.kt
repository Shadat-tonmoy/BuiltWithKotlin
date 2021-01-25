package com.test.ffmpegdemo.videoCompression

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.test.ffmpegdemo.R

class NotificationHelper(private val context: Context)
{
    companion object{
        private const val TAG = "NotificationHelper"
    }


    fun getNotification(title : String, showProgress : Boolean = true, progress : Int = 0, showRetryButton: Boolean = false, message : String) : Notification
    {
        val channelId = createNotificationChannel()
        var builder = NotificationCompat.Builder(context, channelId)
                .setColor(ContextCompat.getColor(context, R.color.teal_700))
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setAutoCancel(true)
        if(!message.isNullOrEmpty())
        {
            builder = builder.setContentText(message)
        }
        if(showRetryButton)
        {
            /*val retryPendingIntent = getNotificationPendingIntent()
            builder = builder
                    .clearActions()
                    .addAction(R.drawable.refresh_white_24, context.getString(R.string.retry),retryPendingIntent)
                    .setWhen(0)
                    .setPriority(NotificationCompat.PRIORITY_MAX)*/
        }
        //Log.e(TAG, "getNotification: ShowProgress : $showProgress, progress : $progress")
        if(showProgress)
        {
            val inDeterminate = progress <= 0
            builder = builder
                    .setProgress(100,progress, inDeterminate)
                    .setContentText("$progress%")
        }
        else builder = builder
                .setProgress(0,0, false)
        //Log.e(TAG, "getNotification: ReturningForMsg : $msg")

        return builder.build()
    }

    private fun createNotificationChannel():String
    {
        val channelId = "video-compression-notification"
        val channelName = "videoCompressionNotification"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            val descriptionText = "videoCompressionNotification"
            val importance = NotificationManager.IMPORTANCE_LOW
            val channel = NotificationChannel(channelId, channelName, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
        return channelId
    }

    private fun getNotificationPendingIntent() : PendingIntent?
    {
        return null
        /*val retryIntent = Intent(context, BackupRestoreService.RetryBroadcastReceiver::class.java)
        retryIntent.action = BackupRestoreService.RESTORE_RETRY_ACTION
        retryIntent.putExtra(BackupRestoreService.EXTRA_USER_ID, userId)
        retryIntent.putExtra(BackupRestoreService.EXTRA_DEVICE_ID, deviceId)
        return PendingIntent.getBroadcast(context, 0, retryIntent, PendingIntent.FLAG_UPDATE_CURRENT)*/
    }


}