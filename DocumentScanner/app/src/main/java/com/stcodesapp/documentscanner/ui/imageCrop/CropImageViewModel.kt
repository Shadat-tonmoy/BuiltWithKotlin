package com.stcodesapp.documentscanner.ui.imageCrop

import android.content.Intent
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.stcodesapp.documentscanner.DocumentScannerApp
import com.stcodesapp.documentscanner.base.BaseViewModel
import com.stcodesapp.documentscanner.constants.Tags
import com.stcodesapp.documentscanner.database.core.AppDatabase
import com.stcodesapp.documentscanner.database.entities.Image
import com.stcodesapp.documentscanner.database.managers.DocumentManager
import com.stcodesapp.documentscanner.database.managers.ImageManager
import com.stcodesapp.documentscanner.tasks.ImageToPdfTask
import kotlinx.coroutines.launch
import javax.inject.Inject

class CropImageViewModel @Inject constructor(val app: DocumentScannerApp) : BaseViewModel(app)
{

    companion object{
        private const val TAG = "CropImageViewModel"
    }

    @Inject lateinit var appDB : AppDatabase
    @Inject lateinit var documentManager: DocumentManager
    @Inject lateinit var imageManager: ImageManager
    @Inject lateinit var imageToPdfTask: ImageToPdfTask

    var documentPages : List<Image>? = null

    private var documentId : Long = 0
    var chosenImagePosition : Int = -1

    fun bindValueFromIntent(intent: Intent)
    {
        if(intent.hasExtra(Tags.DOCUMENT_ID)) documentId = intent.getLongExtra(Tags.DOCUMENT_ID,0L)
    }

    fun fetchDocumentPages() : LiveData<List<Image>>
    {
        return imageManager.getDocumentPagesLiveData(documentId)
    }

    fun deleteDoc() : LiveData<Int>
    {
        val liveData = MutableLiveData<Int>()
        ioCoroutine.launch {
            val rowAffected = documentManager.deleteDocumentById(documentId)
            liveData.postValue(rowAffected)
        }
        return liveData
    }

    fun deleteImage(chosenImage : Image) : LiveData<Int>
    {
        val liveData = MutableLiveData<Int>()
        ioCoroutine.launch {
            val deletedRows = imageManager.deleteImageById(chosenImage.id)
            val document = documentManager.getDocumentById(chosenImage.docId)
            Log.e(TAG, "deleteImage: deletedRow : $deletedRows")
            if(document != null)
            {
                val allImagesOfDocument = imageManager.getDocumentPagesValue(document.id)
                val lastImage = allImagesOfDocument.maxBy { it.id }
                Log.e(TAG, "deleteImage: lastImage : $lastImage")
                if(lastImage != null)
                {
                    document.apply {
                        thumbPath = lastImage.path
                    }
                    documentManager.updateDocument(document)
                }
            }
            liveData.postValue(deletedRows)
        }
        return liveData
    }



}