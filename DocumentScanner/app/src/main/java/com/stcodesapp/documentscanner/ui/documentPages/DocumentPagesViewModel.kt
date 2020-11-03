package com.stcodesapp.documentscanner.ui.documentPages

import android.content.Intent
import android.util.Log
import android.view.View
import androidx.lifecycle.LiveData
import com.stcodesapp.documentscanner.DocumentScannerApp
import com.stcodesapp.documentscanner.base.BaseViewModel
import com.stcodesapp.documentscanner.constants.Tags
import com.stcodesapp.documentscanner.database.AppDatabase
import com.stcodesapp.documentscanner.database.entities.Image
import com.stcodesapp.documentscanner.database.managers.DocumentManager
import com.stcodesapp.documentscanner.database.managers.ImageManager
import com.stcodesapp.documentscanner.tasks.ImageToPdfTask
import com.stcodesapp.documentscanner.ui.dialogs.ImageToPDFNameDialog
import com.stcodesapp.documentscanner.ui.main.MainViewModel
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

    var selectedImages : List<Image>? = null



    private var documentId : Long = 0

    fun bindValueFromIntent(intent: Intent)
    {
        if(intent.hasExtra(Tags.DOCUMENT_ID)) documentId = intent.getLongExtra(Tags.DOCUMENT_ID,0L)
    }

    fun fetchDocumentPages() : LiveData<List<Image>>
    {
        return imageManager.getDocumentPagesLiveData(documentId)
    }

    fun createPDF()
    {
        if(selectedImages != null)
        {
            imageToPdfTask.createPdf(selectedImages!!, "TestPDF")
        }

    }

    fun showPDFNameDialog(view : View)
    {
        val dialog = ImageToPDFNameDialog(view.context)
        dialog.showDialog()

    }

}