package com.test.ffmpegdemo.videoCompression

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.arthenica.mobileffmpeg.Config
import com.test.ffmpegdemo.videoCompression.FFMPEGHelper.Companion.VIDEO_INFO_NOT_AVAILABLE
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File

class VideoCompressionService : Service()
{
    companion object{
        const val VIDEO_INPUT_PATH_KEY = "video_input_path"
        const val VIDEO_OUTPUT_PATH_KEY = "video_output_path"
        private const val TAG = "VideoCompressionService"
        private val NOTIFICATION_ID = 10
    }

    private val ioCoroutine = CoroutineScope(Dispatchers.IO)
    private val uiCoroutine = CoroutineScope(Dispatchers.Main)

    interface VideoCompressionCallback
    {
        fun onVideoCompressionStarted()
        fun onVideoCompressionProgressUpdated(compressionProgress: CompressionProgress)
        fun onVideoCompressionCompleted(savedFilePath : String)
        fun onVideoCompressionFailed(failureMessage : String)
    }

    private val videoCompressionServiceBinder = VideoCompressionServiceBinder()

    var callback : VideoCompressionCallback? = null

    private val notificationHelper by lazy { NotificationHelper(this) }

    override fun onBind(intent: Intent): IBinder {
        return videoCompressionServiceBinder
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int
    {
        Log.e(TAG, "onStartCommand: Called")
        if(intent != null)
        {
            /*val videoInputPath = intent.getStringExtra(VIDEO_INPUT_PATH_KEY)
            val videoOutputPath = intent.getStringExtra(VIDEO_OUTPUT_PATH_KEY)
            if(videoInputPath != null && videoOutputPath != null)
            {
                startCompression(videoInputPath,videoOutputPath)

            }*/
        }
        return START_NOT_STICKY
    }

    fun startCompression(inputPath : String, outputPath : String, maxVideoBitrate : Int, maxVideoResolution : Int)
    {
        val ffmpegHelper = FFMPEGHelper(this)
        ffmpegHelper.maxVideoBitRate = maxVideoBitrate
        ffmpegHelper.maxVideoResolution = maxVideoResolution
        val compressionProgressLiveData = MutableLiveData<Float>()

        startForeground(NOTIFICATION_ID,notificationHelper.getNotification("Compressing Video",message = "Starting video compression..."))

        CoroutineScope(Dispatchers.IO).launch {
            val resultCode = ffmpegHelper.compressFileSync(inputPath,outputPath, object : FFMPEGHelper.Listener{
                override fun onCompressionProgress(compressionProgress: CompressionProgress)
                {
                    callback?.onVideoCompressionProgressUpdated(compressionProgress)
                    Log.e(TAG, "onCompressionProgress: Progress : $compressionProgress.progress" )
                    startForeground(NOTIFICATION_ID,notificationHelper.getNotification("Compressing Video",progress = compressionProgress.progress.toInt(),message = "Compression in progress ($compressionProgress.progress%)"))
                    compressionProgressLiveData.postValue(compressionProgress.progress)
                }

                override fun onCompressionCompleted(savedFilePath: String)
                {
                    Log.e(TAG, "onCompressionCompleted: Saved At : $savedFilePath")
                    ioCoroutine.launch { delay(1000) }.invokeOnCompletion {
                        startForeground(NOTIFICATION_ID,notificationHelper.getNotification("Video Compression Completed!",progress = 100,message = "Compression Done (100%)"))
                        stopForeground(false)

                    }
                    val inputFile = File(inputPath)
                    if(inputFile.exists()) inputFile.delete()
                    callback?.onVideoCompressionCompleted(savedFilePath)

                }

                override fun onCompressionFailed(failureMessage: String)
                {
                    callback?.onVideoCompressionFailed(failureMessage)
                }

            })
            if(resultCode != Config.RETURN_CODE_SUCCESS)
            {
                var errorMessage = "Something went wrong!"
                if(resultCode == VIDEO_INFO_NOT_AVAILABLE) errorMessage = "Could not get video info!"
                callback?.onVideoCompressionFailed(errorMessage)
            }
        }
    }

    inner class VideoCompressionServiceBinder  : Binder() {
        fun getService() : VideoCompressionService
        {
            return this@VideoCompressionService
        }
    }
}