package com.stcodesapp.documentscanner.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
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
import java.io.*
import javax.inject.Inject


class MainViewModel @Inject constructor(val app: DocumentScannerApp) : BaseViewModel(app)
{
    companion object{
        private const val TAG = "MainViewModel"
    }

    fun fetchDocumentListLiveData() : LiveData<List<Document>>
    {
        return documentManager.getDocumentListLiveData()
    }

    fun copySelectedImages(selectedImages: MutableList<String>?) : LiveData<List<Image>>
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
                /*for(imagePath in selectedImages)
                {
                    val outputImagePath = getOutputImagePath(outputDirPath)
                    val result = imageHelper.copyImage(imagePath,outputImagePath)
                    if(result)
                    {
                        val newImage = createNewImage(outputImagePath,position++, newDocument.id)
                        copiedImageList.add(newImage)
                        copiedImageLiveData.postValue(copiedImageList)
                    }
                }*/
            }
        }
        return copiedImageLiveData
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