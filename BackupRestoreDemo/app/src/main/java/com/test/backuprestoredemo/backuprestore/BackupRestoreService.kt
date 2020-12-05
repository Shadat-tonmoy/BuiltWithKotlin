package com.test.backuprestoredemo.backuprestore


import android.accounts.Account
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.gson.Gson
import com.test.backuprestoredemo.backuprestore.impl.BackupRestoreManagerImpl2
import com.test.backuprestoredemo.backuprestore.interfaces.BackupRestoreManager
import com.test.backuprestoredemo.backuprestore.models.BackupInfo
import com.test.backuprestoredemo.backuprestore.models.FilePath
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.File
import java.io.FileReader


class BackupRestoreService : Service() {

    private val backupRestoreServiceBinder = BackupRestoreServiceBinder()
    private var backupRestoreManager : BackupRestoreManager? = null
    private val TAG = "BackupRestoreService"
    private val NOTIFICATION_ID = 10
    private val ioCoroutineScope = CoroutineScope(Dispatchers.IO)
    /*private val userManager by lazy {
        UserManager(this,db)
    }*/

    override fun onCreate() {
        super.onCreate()
        CoroutineScope(Dispatchers.IO).launch{
            initBackupRestoreManager("user1_hash12345678910111214131617","device1_hash12345678910111214131617")
        }
    }

    override fun onBind(intent: Intent): IBinder? {
        return backupRestoreServiceBinder
    }

    private fun initBackupRestoreManager(userId : String? = null, deviceId : String? = null) {

        val account = requestSignIn()
        if(account != null){
            // val userManager = UserManager(this,(application as MyApp).db)
            var myUserId = userId
            var myDeviceId = deviceId
//            if(myUserId == null){
//                val owner = userManager.getCurrentUserValue()
//                if(owner != null){ myUserId = owner.userId }
//            }
//            if(myDeviceId == null)
//            {
//                myDeviceId = getOwnerDeviceID()
//            }

            if(myUserId != null && myDeviceId!=null){
                //val deviceId = DeviceEnum().getDeviceId(this)
                val identityMap = HashMap<String,String>()
                identityMap["userId"] = myUserId
                identityMap["deviceId"] = myDeviceId

                val recursiveOperation = true
                backupRestoreManager = BackupRestoreManagerImpl2(this, account, identityMap,getRootDirToBackup(), recursiveOperation,uploadProgressCallback, downloadProgressCallback)
            }
        }

    }

    fun copyTempAppDB()
    {
        val dbName = getOwnerDBName()
        val dbPath = getDatabasePath(dbName).absolutePath
        if(File(dbPath).exists())
        {
            val appPrivateFolder = filesDir.absolutePath
            val tempDBDir = File("$appPrivateFolder/temp_db/")
            if (!tempDBDir.exists()) tempDBDir.mkdirs()
            val outputPath = "${tempDBDir.absolutePath}/$dbName"
            // val result = FileHelper(this).copyFile(dbPath, outputPath)
            // Log.e(TAG, "copyTempAppDB: CopyTempAppDb : result : $result outputPath : $outputPath")
//            FileLogger.getInstance()?.log(TAG, "initBackupRestoreManager: WillCopy From :$dbPath to $outputPath result is $result")
        }
        else
        {
            // FileLogger.getInstance()?.log(TAG,"copyTempAppDB No Local DB File Found!")
        }

    }

    private fun deleteTempAppDB()
    {
        val dbName = getOwnerDBName()
        val appPrivateFolder = filesDir.absolutePath
        val tempDBDir = File("$appPrivateFolder/temp_db/")
        if(tempDBDir.isDirectory && tempDBDir.exists())
        {
            val allFiles = tempDBDir.listFiles()
            if(allFiles != null)
            {
                for(file in allFiles)
                {
                    if(file != null && file.exists())
                    {
                        Log.e("TAG", "deleteTempAppDB: WillDeleteFile : ${file.absolutePath}")
                        file.delete()
                    }
                }
            }
            Log.e("TAG", "deleteTempAppDB: WillDeleteFile : ${tempDBDir.absolutePath}")
            val result = tempDBDir.delete()
//            Log.e("TAG", "deleteTempAppDB: Result : $result")
        }
    }

    private fun getRootDirToBackup() : List<FilePath>
    {
        val pathList = mutableListOf<FilePath>()
        val appPrivateFolder = filesDir.absolutePath
        // appPrivateFolder = Android/data/com.sil.communicator/data/files/

        val appInternalStorage = getExternalFilesDir(null)!!.absolutePath+"/"
        // appInternalStorage = storage/32FA-1D18/Android/data/com.sil.communicator/files/
        val baseMediaDir = "Media/"
        pathList.add(FilePath("$appPrivateFolder/","temp_db/"))
        pathList.add(FilePath(appInternalStorage,""))
        // Log.e("TAG", "getRootDirToBackup: AppPrivateFOlder : $appPrivateFolder")
        return pathList
    }

    private fun getBackupInfoRootDir() : List<FilePath>
    {
        val pathList = mutableListOf<FilePath>()
        val appPrivateFolder = filesDir.absolutePath
        // appPrivateFolder = Android/data/com.sil.communicator/data/files/
        pathList.add(FilePath("$appPrivateFolder/","info/"))
        return pathList
    }



    private fun requestSignIn() : Account?{
        return GoogleSignIn.getLastSignedInAccount(this)?.account
    }
    private var syncJob : Job? = null
    var backupRestoreCallback : BackupRestoreCallback? = null
    private var syncRunning = false

    fun isSyncRunning() : Boolean{
        return syncRunning
    }

    fun sync(){
        syncJob?.cancel()
        syncJob = CoroutineScope(Dispatchers.IO).launch {
            syncRunning = true
            backupRestoreCallback?.onSyncStarted()
            initBackupRestoreManager()
            try {
                startForeground(NOTIFICATION_ID,getNotification("Backup in progress"))
                backupRestoreManager?.setRootDirsToBackup(getRootDirToBackup())
                backupRestoreManager?.sync(getBackupExcludeList())

                //save backup info file in Google Drive. Need to update root dirs to backup info file root dir
                backupRestoreManager?.setRootDirsToBackup(getBackupInfoRootDir())
                backupRestoreManager?.sync(emptyList())

                backupRestoreCallback?.onSyncComplete()
                deleteTempAppDB()
                startForeground(NOTIFICATION_ID,getNotification("Backup complete", false))
                stopForeground(false)
            }catch (ex : Exception){
                backupRestoreCallback?.onSyncFailed()
                startForeground(NOTIFICATION_ID,getNotification("Backup failed", false))
                stopForeground(false)
                // FileLogger.getInstance()?.printStackTrace(ex)
                ex.printStackTrace()
            }
            syncRunning = false
        }
    }

    fun getAvailableBackup(userId: String) : LiveData<List<BackupInfo>>
    {
        val livaData = MutableLiveData<List<BackupInfo>>()
        ioCoroutineScope.launch {
            initBackupRestoreManager(userId)
            val deviceId = getOwnerDeviceID()
            Log.e(TAG, "getAvailableBackup: manager : $backupRestoreManager deviceId : $deviceId" )
            livaData.postValue(backupRestoreManager?.getAvailableBackup(userId, deviceId))
        }

        return livaData
    }

    private fun getBackupExcludeList() : List<String>
    {
        val logFile = "log.txt"
        // for testing
        //val mediaLogFile = "Media/log.txt"
        //val imagesLogFile = "Media/Communicator Images/log.txt"
        //val dbFile = "${storage.dbName}"
        //return listOf(logFile,mediaLogFile, imagesLogFile, dbFile)
        return listOf(logFile)
    }

    fun restoreDbOnly(userId: String, deviceId: String, dbName : String)
    {
        val ownerDeviceId = getOwnerDeviceID()
        val appPrivateFolder = filesDir.absolutePath
        val rootDir = FilePath("$appPrivateFolder/","temp_db/")
        //val dbName= storage.dbName
        val backupInfoFileName = "backup_info_${userId}_${deviceId}.json"
        Log.e(TAG, "restoreDbOnly: userId : $userId DeviceId : $deviceId OwnerDeviceId : $ownerDeviceId")
        restore(userId, deviceId, listOf(dbName,backupInfoFileName), listOf(), rootDir)
    }

    fun restoreMedia(userId: String, selectedDeviceId: String){
        // if(BuildConfig.DEBUG) return
        val appInternalStorage = getExternalFilesDir(null)!!.absolutePath
        //val ownerDeviceId = DeviceEnum().getDeviceId(this)
        val storagePath = "$appInternalStorage/"
        val baseMediaDir = "Media/"
        val rootDir = FilePath(storagePath,baseMediaDir)
         val dbName= getOwnerDBName()
        restore(userId, selectedDeviceId, listOf(), listOf(dbName),rootDir)
    }

    private fun restore(userId : String, deviceId: String, includeList : List<String>, excludeList : List<String>, rootDir : FilePath){
        syncJob?.cancel()
        syncJob = CoroutineScope(Dispatchers.IO).launch {
            syncRunning = true
            backupRestoreCallback?.onSyncStarted()
            initBackupRestoreManager(userId,deviceId)
            try {
                startForeground(NOTIFICATION_ID,getNotification("Restore in progress"))

                backupRestoreManager?.restore(includeList, excludeList, rootDir)
                startForeground(NOTIFICATION_ID,getNotification("Restore complete", false))
                stopForeground(false)
                backupRestoreCallback?.onSyncComplete()
                readBackupInfoJSON(userId, deviceId)
            }catch (ex : Exception){
                startForeground(NOTIFICATION_ID,getNotification("Restore failed", false))
                stopForeground(false)
                backupRestoreCallback?.onSyncFailed()
                // FileLogger.getInstance()?.printStackTrace(ex)
                ex.printStackTrace()
            }
            syncRunning = false

        }
    }

    fun restoreInfoFromDB(userId: String, deviceId: String)
    {
        // (application as MyApp).closeDb()
        val downloadedDBName = getDBName(userId, deviceId)
        val ownerDbName = getOwnerDBName()
        val dbPath = getDatabasePath(ownerDbName).absolutePath
        val appPrivateFolder = filesDir.absolutePath
        val tempDB = File("$appPrivateFolder/temp_db/$downloadedDBName")
        Log.e("TAG", "restoreInfoFromDB: WillCopy From ${tempDB.absolutePath} to $dbPath ownerDbName : $ownerDbName DownloadedDBName : $downloadedDBName")
//        val result = FileHelper(this).copyFile(tempDB.absolutePath,dbPath)
//        Log.e("TAG", "restoreInfoFromDB: CopyResult : $result")
//        deleteTempAppDB()
//        (application as MyApp).openDb()
    }

    /*fun getLastBackupTime() : Long{
        return backupRestoreManager?.getLastBackupTime()?: 0L
    }

    fun getTotalBackupSize() : Long {
        return backupRestoreManager?.getTotalBackupSize() ?: 0L
    }*/

    interface BackupRestoreCallback{
        fun onSyncComplete()
        fun onSyncFailed()
        fun onSyncStarted()
    }

    inner class BackupRestoreServiceBinder  : Binder() {
        fun getService() : BackupRestoreService
        {
            return this@BackupRestoreService
        }
    }

    private fun getNotification(msg : String, showProgress : Boolean = true, progress : Int = 0) : Notification {
        val channelId = createNotificationChannel()
        var builder = NotificationCompat.Builder(this, channelId)
            //.setColor(ContextCompat.getColor(this, com.tigerit.signalmessenger.R.color.colorPrimary))
            //.setSmallIcon(com.tigerit.signalmessenger.R.drawable.ic_not_icon)
            .setContentTitle(msg)
            .setAutoCancel(true)
        //Log.e(TAG, "getNotification: ShowProgress : $showProgress, progress : $progress")
        if(showProgress)
        {
            val inDeterminate = progress <= 0
            builder = builder.setProgress(100,progress, inDeterminate)
        }

        return builder.build()


    }

    private fun createNotificationChannel():String
    {
        val channelId = "backup-restore-channel-low"
        val channelName = "RestoreBackupNotification"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            val descriptionText = "RestoreBackupNotification"
            val importance = NotificationManager.IMPORTANCE_LOW
            val channel = NotificationChannel(channelId, channelName, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
        return channelId
    }

    private fun readBackupInfoJSON(userId: String, deviceId: String)
    {
        val appPrivateFolder = filesDir.absolutePath
        //val userId = userManager.getCurrentUserValue().userId
        //val deviceId = DeviceEnum().getDeviceId(this@BackupRestoreService)
        val backupInfoFileName = "backup_info_${userId}_${deviceId}.json"
        // appPrivateFolder = Android/data/com.sil.communicator/data/files/
        val backupInfoFile = File("$appPrivateFolder/info/$backupInfoFileName")
        // FileLogger.getInstance(this).log(TAG,"readBackupInfoJSON: BackupInfoFile : ${backupInfoFile.absolutePath} exists : ${backupInfoFile.exists()}")
        //Log.e(TAG, "readBackupInfoJSON: BackupInfoFile : ${backupInfoFile.absolutePath} exists : ${backupInfoFile.exists()}")
        val fileReader = FileReader(backupInfoFile)
        val bufferedReader = BufferedReader(fileReader)
        val stringBuilder = StringBuilder()
        var line: String?
        while (bufferedReader.readLine().also { line = it } != null) {
            stringBuilder.append(line)
        }
        bufferedReader.close()

        val backupInfoJSON = stringBuilder.toString()
        //Log.e(TAG, "readBackupInfoJSON: backupInfoJson : $backupInfoJSON")
        val backupInfo = Gson().fromJson(backupInfoJSON,BackupInfo::class.java)
        //Log.e(TAG, "readBackupInfoJSON: backupInfo : $backupInfo")
        // storage.cacheLastBackupInfo(backupInfo)
    }


    private val uploadProgressCallback = object : BackupRestoreManagerImpl2.UploadListener{

        override fun onTotalBytesOnDriveCalculated(totalBytesOnDrive: Long)
        {
//
        }

        override fun onBackupInfoOnDriveCalculated(backupInfo: BackupInfo) {
            //Log.e(TAG, "onBackupInfoOnDriveCalculated: backupInfo : $backupInfo")
            //storage.cacheLastBackupInfo(backupInfo)
        }

        override fun onUploadProgressUpdated(totalUploadedBytes: Long, totalBytesToUpload: Long)
        {
            val progress = ((totalUploadedBytes.toFloat() / totalBytesToUpload) * 100).toInt()
            updateNotificationProgress("Backup in progress",progress)
            // Log.e(TAG, "onUploadProgressUpdated: uploadedSizeInBytes : $totalUploadedBytes, totalSizeInByte : $totalBytesToUpload Progress : $progress")

        }

    }

    private val downloadProgressCallback = object : BackupRestoreManagerImpl2.DownloadListener{
        override fun onTotalDownloadSizeCalculated(totalBytesToDownload: Long) {
            // Log.e(TAG, "onTotalUploadSizeCalculated: sizeInByte : $totalBytesToDownload")
        }

        override fun onDownloadProgressUpdated(totalDownloadedBytes: Long, totalBytesToDownlaod: Long) {
            val progress = ((totalDownloadedBytes.toFloat() / totalBytesToDownlaod) * 100).toInt()
            updateNotificationProgress("Restore in progress",progress)
            // Log.e(TAG, "onDownloadProgressUpdated: downloadedSizeInBytes : $totalDownloadedBytes, totalSizeInByte : $totalBytesToDownlaod Progress : $progress")
        }

    }

    /**
     * This is the method that can be called to update the Notification
     */
    private fun updateNotificationProgress(msg : String, progress : Int) {
        val notification = getNotification(msg, progress =  progress)
        val mNotificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        mNotificationManager.notify(NOTIFICATION_ID, notification)
    }

    fun getOwnerDBName() : String
    {
        return "storage.dbName"
    }

    fun getOwnerDeviceID() : String
    {
        return "storage.ownerDeviceId"
    }

    fun getDBName(userId: String, deviceId: String) : String
    {
        return "${userId}-${deviceId}.db"
    }

    fun createFolder(name : String)
    {
        backupRestoreManager?.createFolder(name)
    }
}