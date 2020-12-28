package com.test.backuprestoredemo.application

import android.app.Application
import android.util.Log
import androidx.work.*
import com.test.backuprestoredemo.scheduler.TestWorker
import com.test.backuprestoredemo.scheduler.TestWorker.Companion.SCHEDULE_TIME_HOUR
import com.test.backuprestoredemo.scheduler.TestWorker.Companion.SCHEDULE_TIME_MINUTE
import com.tigerit.filelogger.FileLogger
import java.util.*
import java.util.concurrent.ExecutionException
import java.util.concurrent.TimeUnit

class App : Application()
{

    override fun onCreate() {
        super.onCreate()
        FileLogger.getInstance(this)
        initDailyTestWorker()
        isWorkScheduled(TestWorker.TAG)
    }



    private fun initDailyTestWorker()
    {
        val workManager = WorkManager.getInstance(this)
        workManager.cancelAllWorkByTag(TestWorker.TAG)
//        workManager.pruneWork()
//        if(BuildConfig.DEBUG) return

        //current date time
        val currentDate = Calendar.getInstance()
        val dueDate = Calendar.getInstance()

        // Set Execution around 12:00:00 AM
        dueDate.set(Calendar.HOUR_OF_DAY, SCHEDULE_TIME_HOUR)
        dueDate.set(Calendar.MINUTE, SCHEDULE_TIME_MINUTE)
        dueDate.set(Calendar.SECOND, 0)
        if (dueDate.before(currentDate)) {
            dueDate.add(Calendar.MINUTE, 1)
        }
        val timeDiff = dueDate.timeInMillis - currentDate.timeInMillis

        val constraints = Constraints.Builder()
            .setRequiresCharging(true)
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val dailyWorkRequest = OneTimeWorkRequestBuilder<TestWorker>()
            .setConstraints(constraints)
            .setInitialDelay(timeDiff, TimeUnit.MILLISECONDS)
            .addTag(TestWorker.TAG)
            .setBackoffCriteria(BackoffPolicy.LINEAR,10, TimeUnit.SECONDS)
            .build()
        WorkManager.getInstance(this).enqueueUniqueWork(TestWorker.TAG,ExistingWorkPolicy.APPEND_OR_REPLACE,dailyWorkRequest)
    }

    private fun isWorkScheduled(tag: String): Boolean {
        val instance = WorkManager.getInstance(this)
        val statuses =
            instance.getWorkInfosByTag(tag)
        return try {
            var running = false
            val workInfoList = statuses.get()
            for (workInfo in workInfoList)
            {
                val state = workInfo.state
                Log.e(TestWorker.TAG, "isWorkScheduled: State : $state RunAttemps : ${workInfo.runAttemptCount}")
                running = state == WorkInfo.State.RUNNING || state == WorkInfo.State.ENQUEUED
            }
            running
        } catch (e: ExecutionException) {
            e.printStackTrace()
            false
        } catch (e: InterruptedException) {
            e.printStackTrace()
            false
        }
    }

}