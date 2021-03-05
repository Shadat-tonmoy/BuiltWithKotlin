package com.stcodesapp.documentscanner.tasks.imageToPDF

import android.content.Intent
import android.os.Binder
import android.os.Handler
import android.os.IBinder
import android.util.Log
import androidx.core.content.FileProvider
import com.stcodesapp.documentscanner.base.BaseService
import com.stcodesapp.documentscanner.database.entities.Image
import com.stcodesapp.documentscanner.helpers.CacheHelper
import com.stcodesapp.documentscanner.helpers.FileHelper
import com.stcodesapp.documentscanner.helpers.isAndroidX
import com.stcodesapp.documentscanner.models.ImageToPDFProgress
import com.stcodesapp.documentscanner.tasks.ImageToPdfTask
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject


class ImageToPDFService : BaseService()
{

    interface Listener{
        fun onImageToPDFProgressUpdate(progress: ImageToPDFProgress)
    }

    companion object{
        private const val TAG = "FileSavingService"
        private const val NOTIFICATION_ID = 123
    }

    @Inject lateinit var cacheHelper: CacheHelper
    @Inject lateinit var fileHelper: FileHelper
    @Inject lateinit var imageToPDFTask: ImageToPdfTask

    var listener : Listener? = null
    var isConversionRunning = false
    var lastProgress = ImageToPDFProgress(0,0)

    interface ImageToPDFServiceCallback
    {
        fun onRequirePersistableStoragePermission()
        fun onFileSaveToExternalStorageUpdate(progress : Int)
    }

    private val serviceBinder = ServiceBinder()

    private val notificationHelper by lazy {
        ImageToPDFNotificationHelper(this)
    }

    var callbacks : ImageToPDFServiceCallback? = null

    private var lastSavingProgress = 0

    override fun onCreate() {
        super.onCreate()
        appComponent.inejct(this)
    }

    fun createPDF(fileName : String,selectedImages : List<Image>)
    {
        isConversionRunning = true
        ioCoroutine.launch {
            if(isAndroidX())
            {
                convertForAndroidX(fileName, selectedImages)
            }
            else
            {
                convertForAndroid(fileName,selectedImages)
            }
        }
    }

    private suspend fun convertForAndroidX(fileName: String, selectedImages: List<Image>)
    {
        val persistableStorageUri = cacheHelper.getPersistableStorageUri()
        if (persistableStorageUri != null)
        {
            if (!fileHelper.isDocumentFileExists(fileName, persistableStorageUri)) {
                val newFile = fileHelper.getDocumentFileForSaving(fileName, persistableStorageUri)
                if (newFile != null)
                {
                    imageToPDFTask.createPdf(selectedImages, newFile.uri, imageToPDFListener)
                }
            }
        }
    }

    private suspend fun convertForAndroid(fileName: String, selectedImages: List<Image>)
    {
        if (!fileHelper.isFileAlreadyExists(fileName))
        {
            val newFile = fileHelper.getFileForSaving(fileName)
            if (newFile != null)
            {
                val fileURI = FileProvider.getUriForFile(this, applicationContext.packageName + ".provider", newFile)
                imageToPDFTask.createPdf(selectedImages, fileURI, imageToPDFListener)
            }
        }
    }

    private val imageToPDFListener = object : ImageToPdfTask.Listener
    {
        override fun onImageToPDFProgressUpdate(progress: ImageToPDFProgress)
        {
            lastProgress = progress
            val conversionProgress = (progress.totalDone.toFloat()/progress.totalToDone.toFloat())*100
            startForeground(NOTIFICATION_ID,notificationHelper.getNotification("Document Scanner","Conversion is in progress",conversionProgress.toInt()))
            isConversionRunning = true
            if(conversionProgress >= 100)
            {
                stopForeground(true)
                isConversionRunning = false
            }
            listener?.onImageToPDFProgressUpdate(progress)
        }

    }

    override fun onBind(intent: Intent): IBinder
    {
        return serviceBinder
    }

    inner class ServiceBinder  : Binder() {
        fun getService() : ImageToPDFService
        {
            return this@ImageToPDFService
        }
    }


}