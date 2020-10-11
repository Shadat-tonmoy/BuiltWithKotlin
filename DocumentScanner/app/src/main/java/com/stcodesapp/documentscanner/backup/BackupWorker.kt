package com.stcodesapp.documentscanner.backup

import android.content.Context
import android.os.Environment
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.stcodesapp.documentscanner.DocumentScannerApp
import com.stcodesapp.documentscanner.database.AppDatabase
import java.io.File
import java.util.*

class BackupWorker (private val context: Context, workerParams: WorkerParameters) : Worker(context, workerParams)
{
    private val TAG = "BackupWorker"
    private val appDatabase: AppDatabase = (context.applicationContext as DocumentScannerApp).appComponent.getAppDB()
    override fun doWork(): Result
    {
        val backupDirectory = File(getExternalStorageDir(), "test_backup")
        if(!backupDirectory.exists()) backupDirectory.mkdirs()
        val testFile = File("${backupDirectory.absolutePath}${File.separator}${getFormattedTime(System.currentTimeMillis())}.txt")
        testFile.printWriter().use { out -> out.println("FileContentAt ${getFormattedTime(System.currentTimeMillis())}") }
        Log.e(TAG, "doWork: Called at : ${System.currentTimeMillis()} WrittenLogAt : ${testFile.absolutePath}")


        val backupHelper = DBBackupHelper(context,appDatabase)
        backupHelper.backupDB("${System.currentTimeMillis()}")
        backupHelper.backupMedia("${System.currentTimeMillis()}")
        return Result.success()
    }

    fun getExternalStorageDir() : String
    {
        return Environment.getExternalStorageDirectory().absolutePath
    }

    fun getFormattedTime(timeStamp : Long) : String
    {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = timeStamp
        return "${calendar.get(Calendar.DATE)}-${calendar.get(Calendar.MONTH)}-${calendar.get(Calendar.YEAR)}-${calendar.get(Calendar.HOUR)}-${calendar.get(Calendar.MINUTE)}-${calendar.get(Calendar.SECOND)}"
    }



}