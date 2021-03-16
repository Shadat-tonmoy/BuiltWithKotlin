package com.stcodesapp.documentscanner.helpers

import android.content.Context
import android.net.Uri
import android.os.Environment
import androidx.documentfile.provider.DocumentFile
import com.stcodesapp.documentscanner.R
import com.stcodesapp.documentscanner.constants.ConstValues.Companion.OUTPUT_DIRECTORY_NAME
import com.stcodesapp.documentscanner.constants.ConstValues.Companion.SAVE_DIRECTORY_NAME
import java.io.File

class FileHelper(private val context: Context)
{
    fun getSavedFilePath(): String {
        val externalStorage = Environment.getExternalStorageDirectory()
        val externalStoragePath = externalStorage.absolutePath
        val appPublicDirPath = "$externalStoragePath/$SAVE_DIRECTORY_NAME"
        val appPublicDir = File(appPublicDirPath)
        if(!appPublicDir.exists())
        {
            appPublicDir.mkdirs()
        }
        val fileBaseDir = File("$appPublicDir/$OUTPUT_DIRECTORY_NAME")
        if(!fileBaseDir.exists())
        {
            fileBaseDir.mkdirs()
        }
        return fileBaseDir.absolutePath
    }

    fun getPDFFullPathToSave(fileTitle: String): String
    {
        val outputFolder = File(getSavedFilePath())
        if (!outputFolder.exists()) outputFolder.mkdirs()
        return "$outputFolder/$fileTitle"
    }

    fun isDocumentFileExists(fileName : String, persistableStorageUri : Uri) : Boolean
    {
        val permittedDir = DocumentFile.fromTreeUri(context, persistableStorageUri)
        val baseDirName = OUTPUT_DIRECTORY_NAME
        var baseDir = permittedDir?.findFile(baseDirName)
        if(baseDir == null) baseDir = permittedDir?.createDirectory(baseDirName)
        val outputFile = baseDir?.findFile(fileName)
        return outputFile != null
    }

    fun getDocumentFileForSaving(fileName : String, persistableStorageUri : Uri) : DocumentFile?
    {
        val permittedDir = DocumentFile.fromTreeUri(context, persistableStorageUri)
        val baseDirName = OUTPUT_DIRECTORY_NAME
        var baseDir = permittedDir?.findFile(baseDirName)
        if(baseDir == null) baseDir = permittedDir?.createDirectory(baseDirName)
        val outputFile = baseDir?.createFile("application/pdf",fileName)
        return outputFile
    }

    fun getSavedFileDir(persistableStorageUri : Uri) : DocumentFile?
    {
        val permittedDir = DocumentFile.fromTreeUri(context, persistableStorageUri)
        val baseDirName = OUTPUT_DIRECTORY_NAME
        var baseDir = permittedDir?.findFile(baseDirName)
        if(baseDir == null) baseDir = permittedDir?.createDirectory(baseDirName)
        return baseDir
    }

    fun isFileAlreadyExists(fileName : String) : Boolean
    {
        val externalStorage = Environment.getExternalStorageDirectory()
        if(externalStorage != null)
        {
            val externalStoragePath = externalStorage.absolutePath
            val appPublicDirPath = "$externalStoragePath/$SAVE_DIRECTORY_NAME"
            val appPublicDir = File(appPublicDirPath)
            if(!appPublicDir.exists())
            {
                appPublicDir.mkdirs()
            }
            val fileBaseDir = File("$appPublicDir/$OUTPUT_DIRECTORY_NAME")
            if(!fileBaseDir.exists())
            {
                fileBaseDir.mkdirs()
            }
            val outputFile = File(fileBaseDir, "$fileName.pdf")
            return outputFile.exists()
        }
        return false
    }


    fun getFileForSaving(fileName : String) : File?
    {
        val fileBaseDir = File(getSavedFilePath())
        if(!fileBaseDir.exists())
        {
            fileBaseDir.mkdirs()
        }

        val outputFile = File(fileBaseDir, "$fileName.pdf")
        if(!outputFile.exists()) outputFile.createNewFile()
        return outputFile
    }

    fun getThumbDirectory(documentDirPath : String) : File
    {
        val thumbDirPath = "$documentDirPath${File.separator}thumbs"
        val thumbDir = File(thumbDirPath)
        if(!thumbDir.exists()) thumbDir.mkdirs()
        return thumbDir
    }

    fun getThumbFile(documentDirPath : String, fileName : String) : File
    {
        val thumbDir = getThumbDirectory(documentDirPath)
        val thumbFilePath = "${thumbDir.absolutePath}${File.separator}$fileName"
        val thumbFile = File(thumbFilePath)
        if(!thumbFile.exists()) thumbFile.createNewFile()
        return thumbFile

    }


}