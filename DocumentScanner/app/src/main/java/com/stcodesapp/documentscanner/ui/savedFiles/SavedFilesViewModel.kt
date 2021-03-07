package com.stcodesapp.documentscanner.ui.savedFiles

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.stcodesapp.documentscanner.DocumentScannerApp
import com.stcodesapp.documentscanner.base.BaseViewModel
import com.stcodesapp.documentscanner.helpers.*
import com.stcodesapp.documentscanner.models.SavedFile
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

class SavedFilesViewModel @Inject constructor(val app: DocumentScannerApp) : BaseViewModel(app)
{

    @Inject lateinit var cacheHelper : CacheHelper
    @Inject lateinit var fileHelper : FileHelper

    companion object{
        private const val TAG = "SavedFilesViewModel"
    }

    fun fetchSavedFiles() : LiveData<List<SavedFile>>
    {
        val liveData = MutableLiveData<List<SavedFile>>()
        ioCoroutine.launch {
            val savedFiles = if(isAndroidX()) getSavedFilesAndroidX() else getSavedFiles()
            liveData.postValue(savedFiles)
        }
        return liveData

    }

    private suspend fun getSavedFiles(): List<SavedFile>
    {
        val savedFilePath = mutableListOf<File>()
        val savedFileDir = File(getSavedFileDirPath())
        val allFilesInsideDir = savedFileDir.listFiles()
        if (allFilesInsideDir != null)
        {
            val allFileList = allFilesInsideDir.toMutableList()
            val savedFileList = getSavedFileListFromFile(allFileList)
            savedFileList.sortByDescending { it.lastModified }
            return savedFileList

        }
        return emptyList()
    }

    private suspend fun getSavedFilesAndroidX(): List<SavedFile>
    {
        val persistableUri = cacheHelper.getPersistableStorageUri()
        if(persistableUri != null)
        {
            val savedFilesDir = fileHelper.getSavedFileDir(persistableUri)
            if(savedFilesDir != null)
            {
                val allSavedFiles = savedFilesDir.listFiles().toMutableList()
                val savedFileList = getSavedFileListFromDocumentFile(allSavedFiles)
                savedFileList.sortByDescending { it.lastModified }
                return savedFileList
            }
        }
        return emptyList()

    }


    private fun getSavedFileDirPath(): String
    {
        val fileHelper = FileHelper(context)
        val outputFolder = File(fileHelper.getSavedFilePath())
        if (!outputFolder.exists()) outputFolder.mkdirs()
        return outputFolder.absolutePath
    }

    fun deleteFile(savedFile: SavedFile) : LiveData<Boolean>
    {
        val liveData = MutableLiveData<Boolean>()
        ioCoroutine.launch {
            val result = if(isAndroidX()) deleteFileForAndroidX(savedFile) else deleteFileForAndroid(savedFile)
            liveData.postValue(result)
        }
        return liveData
    }

    private fun deleteFileForAndroid(savedFile: SavedFile) : Boolean
    {
        val fileToDelete = File(savedFile.pathString)
        return fileToDelete.delete()
    }

    private fun deleteFileForAndroidX(savedFile: SavedFile) : Boolean
    {
        val persistableUri = cacheHelper.getPersistableStorageUri()
        if(persistableUri != null)
        {
            val savedFilesDir = fileHelper.getSavedFileDir(persistableUri)
            if (savedFilesDir != null)
            {
                val fileToDelete = savedFilesDir.findFile(savedFile.displayName)
                if(fileToDelete != null)
                {
                    return fileToDelete.delete()
                }

            }
        }
        return false
    }
}