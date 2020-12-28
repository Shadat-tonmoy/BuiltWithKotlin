package com.test.backuprestoredemo.scheduler

import android.content.Context
import android.util.Log
import androidx.concurrent.futures.CallbackToFutureAdapter
import androidx.work.*
import com.google.common.util.concurrent.ListenableFuture
import com.test.backuprestoredemo.helper.getHumanReadableTime
import com.tigerit.filelogger.FileLogger
import java.io.File
import java.util.*
import java.util.concurrent.TimeUnit

class TestWorker(private val context: Context, workerParams: WorkerParameters) : ListenableWorker(context, workerParams)
{
    companion object {
        const val TAG = "TestWorker"
        const val SCHEDULE_TIME_HOUR = 12
        const val SCHEDULE_TIME_MINUTE = 36
    }

    interface WorkerCallback
    {
        fun onSuccess()
        fun onError()
    }

    var callback: WorkerCallback? = null

    override fun startWork(): ListenableFuture<Result>
    {
        FileLogger.getInstance()?.log(TAG,"TestWorker StartWork called")
        return CallbackToFutureAdapter.getFuture<Result> { completer: CallbackToFutureAdapter.Completer<Result> ->

            callback = object : WorkerCallback
            {
                override fun onSuccess() {
                    FileLogger.getInstance()?.log(TAG,"TestWorker onSuccessCalled")
                    completer.set(Result.success())
                }

                override fun onError() {
                    FileLogger.getInstance()?.log(TAG,"TestWorker onErrorCalled! Will retry")
                    completer.set(Result.retry())
                }
            }

//            runTestWork()
            callback
        }

    }

    private fun runTestWork()
    {
        val result = false //logInfoFileForTest()
        if(result)
        {
            scheduleNextWork()
            callback?.onSuccess()
        }
        else callback?.onError()
    }

    private fun scheduleNextWork()
    {
        FileLogger.getInstance()?.log(TAG,"Scheduling next BackupWorker.")
        val currentDate = Calendar.getInstance()
        val dueDate = Calendar.getInstance()

        var dueDateInMillis = dueDate.timeInMillis
        dueDateInMillis += (1*60*1000)
        dueDate.timeInMillis = dueDateInMillis

        // Set Execution around 12:00:00 AM
        /*dueDate.set(Calendar.HOUR_OF_DAY, BACKUP_TIME_HOUR)
        dueDate.set(Calendar.MINUTE, BACKUP_TIME_MINUTE)
        dueDate.set(Calendar.SECOND, 0)
        if (dueDate.before(currentDate)) {
            dueDate.add(Calendar.HOUR_OF_DAY, 24)
        }*/
        val timeDiff = dueDate.timeInMillis - currentDate.timeInMillis
        Log.e(TAG, "scheduleNextBackupWork: At : ${getFormattedTime(dueDate.timeInMillis)}")

        val constraints = Constraints.Builder()
            .setRequiresCharging(true)
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val dailyWorkRequest = OneTimeWorkRequestBuilder<TestWorker>()
            .setInitialDelay(timeDiff, TimeUnit.MILLISECONDS)
            .setConstraints(constraints)
            .addTag(TAG)
            .setBackoffCriteria(BackoffPolicy.LINEAR,10, TimeUnit.SECONDS)
            .build()

        //WorkManager.getInstance(applicationContext)
        WorkManager.getInstance(applicationContext).enqueueUniqueWork(TAG,ExistingWorkPolicy.APPEND_OR_REPLACE,dailyWorkRequest)

    }

    private fun logInfoFileForTest() : Boolean
    {
        try {
            val backupDirectory = File(getExternalStorageDir(), "test_backup")
            if (!backupDirectory.exists()) backupDirectory.mkdirs()
            val testFile = File("${backupDirectory.absolutePath}${File.separator}${getFormattedTime(System.currentTimeMillis())}.txt")
            if(!testFile.exists()) testFile.createNewFile()
            testFile.printWriter()
                .use { out -> out.println("FileContentAt ${getHumanReadableTime(System.currentTimeMillis())}") }
            Log.e(TAG, "doWork: Called at : ${getHumanReadableTime(System.currentTimeMillis())} WrittenLogAt : ${testFile.absolutePath}")
            return true
        }catch (e : Exception)
        {
            e.printStackTrace()
            return false
        }
    }

    private fun getExternalStorageDir() : String
    {
        return context.getExternalFilesDir(null)!!.absolutePath
    }

    private fun getFormattedTime(timeStamp : Long) : String
    {
        return getHumanReadableTime(timeStamp)
    }
}