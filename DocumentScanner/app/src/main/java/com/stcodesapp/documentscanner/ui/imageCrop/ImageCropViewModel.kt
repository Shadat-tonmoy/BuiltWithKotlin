package com.stcodesapp.documentscanner.ui.imageCrop

import android.content.Intent
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.stcodesapp.documentscanner.DocumentScannerApp
import com.stcodesapp.documentscanner.base.BaseViewModel
import com.stcodesapp.documentscanner.constants.Tags
import com.stcodesapp.documentscanner.database.core.AppDatabase
import com.stcodesapp.documentscanner.database.entities.Image
import com.stcodesapp.documentscanner.database.managers.DocumentManager
import com.stcodesapp.documentscanner.database.managers.ImageManager
import com.stcodesapp.documentscanner.helpers.FileHelper
import com.stcodesapp.documentscanner.tasks.ImageToPdfTask
import kotlinx.coroutines.launch
import javax.inject.Inject

class ImageCropViewModel @Inject constructor(val app: DocumentScannerApp) : BaseViewModel(app)
{
    companion object{
        private const val TAG = "ImageCropViewModel"
    }

    @Inject lateinit var appDB : AppDatabase
    @Inject lateinit var documentManager: DocumentManager
    @Inject lateinit var imageManager: ImageManager
    @Inject lateinit var imageToPdfTask: ImageToPdfTask
    @Inject lateinit var fileHelper: FileHelper

    private var documentId : Long = 0
    private var imageId : Long = 0

    fun bindValueFromIntent(intent: Intent)
    {
        if(intent.hasExtra(Tags.DOCUMENT_ID)) documentId = intent.getLongExtra(Tags.DOCUMENT_ID,0L)
        if(intent.hasExtra(Tags.IMAGE_ID)) imageId = intent.getLongExtra(Tags.IMAGE_ID,0L)
    }

    fun fetchChosenImageToReCrop() : LiveData<Image?>
    {
        val liveData = MutableLiveData<Image?>()
        ioCoroutine.launch {
            val imageWithId = imageManager.getImageById(imageId)
            liveData.postValue(imageWithId)
        }
        return liveData

    }

    fun fetchDocumentPages() : LiveData<List<Image>>
    {
        return imageManager.getDocumentPagesLiveData(documentId)
    }

}