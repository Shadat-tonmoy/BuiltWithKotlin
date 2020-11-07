package com.stcodesapp.documentscanner.ui.savedFiles

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.stcodesapp.documentscanner.DocumentScannerApp
import com.stcodesapp.documentscanner.base.BaseViewModel
import com.stcodesapp.documentscanner.helpers.FileHelper
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

class SavedFilesViewModel @Inject constructor(val app: DocumentScannerApp) : BaseViewModel(app)
{
    private val context = app.applicationContext

    companion object{
        private const val TAG = "SavedFilesViewModel"
    }

    fun fetchSavedFiles() : LiveData<List<File>>
    {
        val liveData = MutableLiveData<List<File>>()
        ioCoroutine.launch {
            val savedFiles = getSavedFiles()
            liveData.postValue(savedFiles)
        }
        return liveData

    }

    private fun getSavedFiles(): MutableList<File>
    {
        val savedFilePath = mutableListOf<File>()
        val savedFileDir = File(getSavedFileDirPath())
        val savedFiles = savedFileDir.listFiles()
        if (savedFiles != null) {
            for (file in savedFiles) {
                savedFilePath.add(file)
            }
        }
        return savedFilePath
    }


    private fun getSavedFileDirPath(): String
    {
        val fileHelper = FileHelper(context)
        val outputFolder = File(fileHelper.getSavedFilePath())
        if (!outputFolder.exists()) outputFolder.mkdirs()
        return outputFolder.absolutePath
    }
}