package com.stcodesapp.documentscanner.ui.imageCrop

import android.content.Intent
import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.stcodesapp.documentscanner.DocumentScannerApp
import com.stcodesapp.documentscanner.base.BaseViewModel
import com.stcodesapp.documentscanner.constants.Tags
import com.stcodesapp.documentscanner.database.core.AppDatabase
import com.stcodesapp.documentscanner.database.entities.Document
import com.stcodesapp.documentscanner.database.entities.Image
import com.stcodesapp.documentscanner.database.managers.DocumentManager
import com.stcodesapp.documentscanner.database.managers.ImageManager
import com.stcodesapp.documentscanner.helpers.FileHelper
import com.stcodesapp.documentscanner.models.*
import com.stcodesapp.documentscanner.scanner.getBrightenImage
import com.stcodesapp.documentscanner.scanner.getGrayscaleImage
import com.stcodesapp.documentscanner.scanner.getLightenImage
import com.stcodesapp.documentscanner.tasks.ImageToPdfTask
import kotlinx.coroutines.launch
import java.io.File
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
    @Inject lateinit var fileHelper: FileHelper

    var originalImageBitmap: Bitmap? = null

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

    fun deleteImage(chosenImage : Image) : LiveData<Int>
    {
        val liveData = MutableLiveData<Int>()
        ioCoroutine.launch {
            val deletedRows = imageManager.deleteImageById(chosenImage.id)
            val imageFile = File(chosenImage.path)
            val deleteResult = imageFile.delete()
            Log.e(TAG, "deleteImage: FileDeleteFor ${chosenImage.path}, result is : $deleteResult")
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

    fun saveCurrentImageFilterInfo(image: Image, filter : Filter) : LiveData<Long>
    {
        val liveData = MutableLiveData<Long>()
        ioCoroutine.launch {
            image.apply {
                filterName = filter.type.toString()
                filterJson = getFilterJSON(filter.type)
            }
            val affectedRow = imageManager.updateImage(image)
            liveData.postValue(affectedRow)
        }
        liveData.postValue(-1L)
        return liveData
    }
    fun saveCustomImageFilterInfo(image: Image, filter : CustomFilter) : LiveData<Long>
    {
        val liveData = MutableLiveData<Long>()
        ioCoroutine.launch {
            val filterJSON = Gson().toJson(filter, CustomFilter::class.java)
            image.apply {
                customFilterJson = filterJSON
            }
            val affectedRow = imageManager.updateImage(image)
            liveData.postValue(affectedRow)
        }
        liveData.postValue(-1L)
        return liveData
    }

    private fun getFilterJSON(filterType: FilterType) : String
    {
        when(filterType)
        {
            FilterType.GRAY_SCALE -> ""
            //FilterType.BLACK_AND_WHITE -> getBlackAndWhiteImage(srcBitmap,dstBitmap)
            FilterType.BRIGHTEN -> Gson().toJson(BrightenFilter(CropImageSingleItemViewModel.BRIGHTEN_FILTER_VALUE))
            FilterType.LIGHTEN -> Gson().toJson(LightenFilter(CropImageSingleItemViewModel.LIGHTEN_FILTER_VALUE))
            else -> return ""
        }
        return ""
    }



}