package com.stcodesapp.documentscanner.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.stcodesapp.documentscanner.DocumentScannerApp
import com.stcodesapp.documentscanner.base.BaseViewModel
import com.stcodesapp.documentscanner.database.AppDatabase
import com.stcodesapp.documentscanner.helpers.ImageHelper
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.*
import javax.inject.Inject


class MainViewModel @Inject constructor(val app: DocumentScannerApp) : BaseViewModel(app)
{
    @Inject lateinit var appDB : AppDatabase

    companion object{
        private const val TAG = "MainViewModel"
    }

    fun copySelectedImages(selectedImages: MutableList<String>?) : LiveData<List<String>>
    {
        val copiedImageLiveData = MutableLiveData<List<String>>()
        val copiedImageList = mutableListOf<String>()
        val imageHelper = ImageHelper()
        ioCoroutine.launch {
            if(selectedImages!=null)
            {
                val outputDirPath = getOutputImageDir()
                for(imagePath in selectedImages)
                {
//                    val outputImagePath = getOutputImagePath(outputDirPath)
//                    val result = imageHelper.copyImage(imagePath,outputImagePath)
                    delay(1000)
                    val result = true
                    if(result)
                    {
                        copiedImageList.add("outputImagePath")
                        copiedImageLiveData.postValue(copiedImageList)
                    }
                }
            }
        }
        return copiedImageLiveData
    }

    private fun getOutputImageDir(): String
    {
        val appPrivateStorage = app.filesDir
        val documentTitle = System.currentTimeMillis()
        return appPrivateStorage.absolutePath + File.separator + documentTitle
    }

    private fun getOutputImagePath(outputDirPath: String): String {
        val outputImageTitle = System.currentTimeMillis()
        return outputDirPath + File.separator + outputImageTitle
    }


}