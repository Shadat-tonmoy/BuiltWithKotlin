package com.stcodesapp.documentscanner.ui.home

import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import androidx.core.content.FileProvider
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.shadattonmoy.imagepickerforandroid.model.ImageFile
import com.stcodesapp.documentscanner.BuildConfig
import com.stcodesapp.documentscanner.DocumentScannerApp
import com.stcodesapp.documentscanner.base.BaseViewModel
import com.stcodesapp.documentscanner.constants.ConstValues
import com.stcodesapp.documentscanner.constants.ConstValues.Companion.THUMB_SIZE
import com.stcodesapp.documentscanner.database.entities.Document
import com.stcodesapp.documentscanner.database.entities.Image
import com.stcodesapp.documentscanner.database.managers.DocumentManager
import com.stcodesapp.documentscanner.database.managers.ImageManager
import com.stcodesapp.documentscanner.database.managers.getNewDocument
import com.stcodesapp.documentscanner.database.managers.getNewImage
import com.stcodesapp.documentscanner.helpers.FileHelper
import com.stcodesapp.documentscanner.helpers.FilterHelper
import com.stcodesapp.documentscanner.helpers.ImageHelper
import com.stcodesapp.documentscanner.models.Filter
import com.stcodesapp.documentscanner.models.FilterType
import com.stcodesapp.documentscanner.ui.imageCrop.CropImageSingleItemViewModel
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

class HomeViewModel @Inject constructor(val app: DocumentScannerApp) : BaseViewModel(app)
{
    @Inject lateinit var documentManager : DocumentManager
    @Inject lateinit var imageManager: ImageManager
    @Inject lateinit var filterHelper: FilterHelper
    @Inject lateinit var fileHelper: FileHelper
    @Inject lateinit var imageHelper: ImageHelper

    companion object{
        private const val TAG = "HomeViewModel"
    }

    fun fetchDocumentListLiveData() : LiveData<List<Document>>
    {
        return documentManager.getDocumentListLiveData()
    }

    fun copySelectedImages(selectedImages: MutableList<ImageFile>?) : LiveData<List<Image>>
    {
        val copiedImageLiveData = MutableLiveData<List<Image>>()
        val copiedImageList = mutableListOf<Image>()
        val imageHelper = ImageHelper(context)
        var lastImagePath : String? = null
        ioCoroutine.launch {
            if(selectedImages!=null)
            {
                val newDocument = createNewDocument()
                val outputDirPath = newDocument.path
                var position = 1
                for(imageFile in selectedImages)
                {
                    val outputImagePath = getOutputImagePath(outputDirPath)
                    Log.e(TAG, "copySelectedImages: selectedImage : $imageFile")
                    val result = imageHelper.copyImage(imageFile.imageFileUri,outputImagePath)
                    if(result)
                    {
                        val newImage = createNewImage(outputImagePath,position++, newDocument.id)
                        copiedImageList.add(newImage)
                        lastImagePath = newImage.path
                        copiedImageLiveData.postValue(copiedImageList)
                        saveFilteredThumb(outputImagePath,newDocument.path)
                    }
                }
                updateNewDocumentThumb(newDocument,lastImagePath)
            }
        }
        return copiedImageLiveData
    }

    private fun saveFilteredThumb(imagePath : String, documentPath: String)
    {
        val imageUri = Uri.fromFile(File(imagePath))
        val imageBitmap = imageHelper.decodeBitmapFromUri(imageUri,THUMB_SIZE, THUMB_SIZE)
        if(imageBitmap != null)
        {
            val filterList = filterHelper.getFilterList(imagePath)
            for(filter in filterList)
            {
                saveThumbWithFilter(filter, imagePath, documentPath, imageBitmap)
                Log.e(TAG, "saveFilteredThumb: success")
            }
        }
        else
        {
            Log.e(TAG, "saveFilteredThumb: Could not save filtered thumb as bitmap is null")
        }
    }

    private fun saveThumbWithFilter(filter: Filter,imagePath : String, documentPath: String, imageBitmap: Bitmap)
    {
        val thumbFileName = filterHelper.getFilteredThumbFileName(imagePath, filter.type)
        val thumbFile = fileHelper.getThumbFile(documentPath, thumbFileName)
        val thumbBitmap = imageHelper.getResizedBitmapByThreshold(imageBitmap, THUMB_SIZE)
        val thumbFilePath = thumbFile.absolutePath
        if (thumbBitmap != null)
        {
            val filteredBitmap = getFilteredBitmap(filter.type, thumbBitmap)
            imageHelper.saveBitmapInFile(filteredBitmap, thumbFilePath, quality = 100)
        }
    }

    private fun getFilteredBitmap(filterType: FilterType, srcBitmap : Bitmap) : Bitmap
    {
        return filterHelper.getFilteredBitmap(filterType,srcBitmap)
    }

    private suspend fun updateNewDocumentThumb(document: Document, thumbPath : String?)
    {
        if(thumbPath != null)
        {
            document.thumbPath = thumbPath
            documentManager.updateDocument(document)
        }
    }

    private suspend fun createNewDocument() : Document
    {
        val newDocument = getNewDocument(getOutputImageDir())
        val docId = documentManager.createOrUpdateDocument(newDocument)
        val newDocumentDir = File(newDocument.path)
        if(!newDocumentDir.exists()) { newDocumentDir.mkdirs() }
        return newDocument.apply { id = docId }
    }

    private suspend fun createNewImage(path : String, position : Int, docId : Long) : Image
    {
        val newImage = getNewImage(path,position,docId)
        val imageId = imageManager.createOrUpdateImage(newImage)
        return newImage.apply { id = imageId }
    }

    private fun getOutputImageDir(): String
    {
        val appPrivateStorage = app.filesDir
        val documentTitle = System.currentTimeMillis()
        return appPrivateStorage.absolutePath + File.separator + documentTitle
    }

    private fun getOutputImagePath(outputDirPath: String): String {
        val outputImageTitle = "${System.currentTimeMillis()}${ConstValues.PNG_EXTENSION}"
        return outputDirPath + File.separator + outputImageTitle
    }
}