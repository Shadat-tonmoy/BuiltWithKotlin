package com.stcodesapp.documentscanner.ui.documentPages

import android.content.Intent
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.stcodesapp.documentscanner.DocumentScannerApp
import com.stcodesapp.documentscanner.base.BaseViewModel
import com.stcodesapp.documentscanner.constants.Tags
import com.stcodesapp.documentscanner.database.core.AppDatabase
import com.stcodesapp.documentscanner.database.entities.Document
import com.stcodesapp.documentscanner.database.entities.Image
import com.stcodesapp.documentscanner.database.managers.DocumentManager
import com.stcodesapp.documentscanner.database.managers.ImageManager
import com.stcodesapp.documentscanner.helpers.FileHelper
import com.stcodesapp.documentscanner.models.ImageToPDFProgress
import com.stcodesapp.documentscanner.tasks.ImageToPdfTask
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

class DocumentPagesViewModel @Inject constructor(private val app: DocumentScannerApp) : BaseViewModel(app)
{

    companion object
    {
        private const val TAG = "DocumentPagesViewModel"
    }

    @Inject lateinit var appDB : AppDatabase
    @Inject lateinit var documentManager: DocumentManager
    @Inject lateinit var imageManager: ImageManager
    @Inject lateinit var imageToPdfTask: ImageToPdfTask
    @Inject lateinit var fileHelper: FileHelper

    var selectedImages : List<Image>? = null
    var selectedDocument : Document? = null

    var documentId : Long = 0

    fun bindValueFromIntent(intent: Intent)
    {
        if(intent.hasExtra(Tags.DOCUMENT_ID)) documentId = intent.getLongExtra(Tags.DOCUMENT_ID,0L)
    }

    fun fetchDocumentPages() : LiveData<List<Image>>
    {
        return imageManager.getDocumentPagesLiveData(documentId)
    }

    fun getLiveDocumentDetail() : LiveData<Document>
    {
        return documentManager.getLiveDocumentById(documentId)
    }

    fun createPDF(fileName: String) : LiveData<ImageToPDFProgress>
    {
        val liveData = MutableLiveData<ImageToPDFProgress>()
        ioCoroutine.launch {
            if(selectedImages != null)
            {
                //imageToPdfTask.createPdf(selectedImages!!, fileName) { liveData.postValue(it) }
            }
        }
        return liveData
    }

    fun deleteDoc() : LiveData<Int>
    {
        val liveData = MutableLiveData<Int>()
        ioCoroutine.launch {
            val documentWithId = documentManager.getDocumentById(documentId)
            val rowAffected = documentManager.deleteDocumentById(documentId)
            deleteDocFilesAndFolder(documentWithId)
            liveData.postValue(rowAffected)
        }
        return liveData
    }

    private fun deleteDocFilesAndFolder(documentWithId : Document?)
    {
        if (documentWithId != null)
        {
            val documentFolder = File(documentWithId.path)
            fileHelper.deleteData(documentFolder)
        }
    }

}