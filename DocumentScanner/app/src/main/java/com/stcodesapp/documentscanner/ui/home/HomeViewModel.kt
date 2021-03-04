package com.stcodesapp.documentscanner.ui.home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.shadattonmoy.imagepickerforandroid.model.ImageFile
import com.stcodesapp.documentscanner.DocumentScannerApp
import com.stcodesapp.documentscanner.base.BaseViewModel
import com.stcodesapp.documentscanner.constants.ConstValues
import com.stcodesapp.documentscanner.database.entities.Document
import com.stcodesapp.documentscanner.database.entities.Image
import com.stcodesapp.documentscanner.database.managers.DocumentManager
import com.stcodesapp.documentscanner.database.managers.ImageManager
import com.stcodesapp.documentscanner.database.managers.getNewDocument
import com.stcodesapp.documentscanner.database.managers.getNewImage
import com.stcodesapp.documentscanner.helpers.ImageHelper
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

class HomeViewModel @Inject constructor(val app: DocumentScannerApp) : BaseViewModel(app)
{
    @Inject lateinit var documentManager : DocumentManager
    @Inject lateinit var imageManager: ImageManager

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
                        updateNewDocumentThumb(newDocument,newImage.path)
                        copiedImageList.add(newImage)
                        copiedImageLiveData.postValue(copiedImageList)
                    }
                }
            }
        }
        return copiedImageLiveData
    }

    private suspend fun updateNewDocumentThumb(document: Document, thumbPath : String)
    {
        document.thumbPath = thumbPath
        documentManager.updateDocument(document)
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