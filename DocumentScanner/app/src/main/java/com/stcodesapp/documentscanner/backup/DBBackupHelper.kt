package com.stcodesapp.documentscanner.backup

import android.content.Context
import android.os.Environment
import android.util.Log
import com.stcodesapp.documentscanner.R
import com.stcodesapp.documentscanner.database.AppDatabase
import com.stcodesapp.documentscanner.helpers.FileHelper
import java.io.*


class DBBackupHelper(private val context: Context, private val appDatabase: AppDatabase)
{
    companion object
    {
        private const val TAG = "DBBackupHelper"
    }

    private val DB_NAME = context.getString(R.string.app_db_name)

    fun backupDB(backupDir : String = "")
    {
        appDatabase.close()
        val dbFile: File = context.getDatabasePath(DB_NAME)
        val dbFolder = File(context.getDatabasePath(DB_NAME).parent!!)
        val allFiles = dbFolder.listFiles()
        if(allFiles!=null)
        {
            val backupDirectory = File(getExternalStorageDir(), "test_backup${File.separator}db${File.separator}$backupDir")
            val fileHelper = FileHelper()
            for(file in allFiles)
            {
                val sourceFilePath = file.absolutePath
                val destinationFileName = File(sourceFilePath).name
                val destinationFilePath = "${backupDirectory.path}${File.separator}${destinationFileName}"
                fileHelper.copyFile(sourceFilePath,destinationFilePath)
                Log.e(TAG, "backupDB: DBPath ${file.absolutePath}")
            }
        }
    }

    fun restoreDB()
    {
        val backupDirectory = File(getExternalStorageDir(), "test_backup${File.separator}db")
        val allDBFile = backupDirectory.listFiles()
        val dbFolder = File(context.getDatabasePath(DB_NAME).parent!!)
        if(allDBFile!=null)
        {
            appDatabase.close()
            if(!dbFolder.exists()) dbFolder.mkdirs()
            val fileHelper = FileHelper()
            for(dbFile in allDBFile)
            {
                val sourcePath = dbFile.absolutePath
                val destinationPath = "${dbFolder.absolutePath}${File.separator}${dbFile.name}"
                fileHelper.copyFile(sourcePath,destinationPath)
                Log.e(TAG, "restoreDB: From $sourcePath to $destinationPath")
            }
        }

    }

    fun backupMedia(backupDir : String = "")
    {
        val appPrivateStorage = context.filesDir
        val allFolders = appPrivateStorage.listFiles()
        if(allFolders != null)
        {
            val backupDirectory = File(getExternalStorageDir(), "test_backup${File.separator}media${File.separator}$backupDir")
            val fileHelper = FileHelper()
            for(folder in allFolders)
            {
                if(folder.isDirectory)
                {
                    val allFiles = folder.listFiles()
                    if(allFiles != null)
                    {
                        for(file in allFiles)
                        {
                            val sourcePath = file.absolutePath
                            val destinationDir = File("$backupDirectory${File.separator}${folder.name}")
                            if(!destinationDir.exists()) destinationDir.mkdirs()
                            val destinationPath = "$destinationDir${File.separator}${file.name}"
                            fileHelper.copyFile(sourcePath,destinationPath)
                            Log.e(TAG, "backupMedia: Copy From : $sourcePath to $destinationPath")
                        }
                    }
                }
                Log.e(TAG, "backupMedia: FolderPath : ${folder.absolutePath}")
            }
        }
    }

    fun restoreMedia()
    {
        val appPrivateStorage = context.filesDir
        val backupDirectory = File(getExternalStorageDir(), "test_backup${File.separator}media")
        val allMediaFolder = backupDirectory.listFiles()
        if(allMediaFolder != null)
        {
            val fileHelper = FileHelper()
            for(folder in allMediaFolder)
            {
                if(folder.isDirectory)
                {
                    val allFiles = folder.listFiles()
                    if(allFiles != null)
                    {
                        for(file in allFiles)
                        {
                            val sourcePath = file.absolutePath
                            val destinationDir = File("$appPrivateStorage${File.separator}${folder.name}")
                            if(!destinationDir.exists()) destinationDir.mkdirs()
                            val destinationPath = "$destinationDir${File.separator}${file.name}"
                            fileHelper.copyFile(sourcePath,destinationPath)
                            Log.e(TAG, "restoreMedia: Copy From : $sourcePath to $destinationPath")
                        }
                    }
                }
                Log.e(TAG, "restoreMedia: FolderPath : ${folder.absolutePath}")
            }
        }
    }

    fun getExternalStorageDir() : String
    {
        return Environment.getExternalStorageDirectory().absolutePath
    }


}