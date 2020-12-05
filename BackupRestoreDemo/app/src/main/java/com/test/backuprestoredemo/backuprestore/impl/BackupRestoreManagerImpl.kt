package com.test.backuprestoredemo.backuprestore.impl

import android.accounts.Account
import android.content.Context
import android.util.Log
import com.google.api.client.extensions.android.http.AndroidHttp
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.json.gson.GsonFactory
import com.google.api.services.drive.Drive
import com.google.api.services.drive.DriveScopes
import com.test.backuprestoredemo.BuildConfig
import com.test.backuprestoredemo.backuprestore.interfaces.BackupRestoreManager
import com.test.backuprestoredemo.backuprestore.DriveServiceHelper
import com.test.backuprestoredemo.backuprestore.MD5
import com.test.backuprestoredemo.backuprestore.models.BackupInfo
import com.test.backuprestoredemo.backuprestore.models.FileInfo
import com.test.backuprestoredemo.backuprestore.models.FilePath
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.*
import kotlin.collections.HashMap

class BackupRestoreManagerImpl(private val context: Context, private val userId : String, account : Account, private val directoryList : List<String>) :
    BackupRestoreManager {
    private val driveServiceHelper : DriveServiceHelper
    private val pref = context.getSharedPreferences("backup-restore", Context.MODE_PRIVATE)
    init {
        val credential = GoogleAccountCredential.usingOAuth2(
            context, Collections.singleton(DriveScopes.DRIVE_APPDATA)
        )
        credential.selectedAccount = account
        val googleDriveService =
            Drive.Builder(AndroidHttp.newCompatibleTransport(), GsonFactory(), credential)
                .setApplicationName(BuildConfig.APPLICATION_ID)
                .build()

        // The DriveServiceHelper encapsulates all REST API and SAF functionality.
        // Its instantiation is required before handling any onClick actions.
        driveServiceHelper =
            DriveServiceHelper(
                googleDriveService,
                object : DriveServiceHelper.UploadProgressListener{
                    override fun onUploadProgressUpdate(filePath: String, uploadedBytes: Long) {
                        TODO("Not yet implemented")
                    }

                }
            )
    }

//    private fun saveList(list : List<FileInfo>){
//        pref.edit().putString("file-info-list", Gson().toJson(list)).apply()
//    }

//    private fun getList() : List<FileInfo>{
//        val json = pref.getString("file-info-list",null)
//        if(json != null){
//            val listType: Type = object : TypeToken<List<FileInfo?>?>() {}.type
//            return Gson().fromJson(json, listType)
//        }
//        return listOf()
//    }

    private fun getFileInfoList() : List<FileInfo>{
        // FileLogger.getInstance()?.log("sync","request file list")
        val fileInfoList = mutableListOf<FileInfo>()
        var nextPageToken : String? = null
        var sum = 0L
        while(true){
            val fileList = driveServiceHelper.queryFiles(nextPageToken)
            // FileLogger.getInstance()?.log("sync","got file list ${fileList.files.size}")
            for(file in fileList.files){
                val map = file.appProperties
                if(map != null && map.containsKey("userId")){
                    val userId = map["userId"]?:""
                    val parent = map["parent"]?:""
                    val md5sum = map["md5sum"]?:""
                    if(userId == this.userId){
                        sum += file.quotaBytesUsed
                        val fileInfo = FileInfo(file.id, file.name, "$parent/${file.name}", parent, md5sum)
                        fileInfoList.add(fileInfo)
                    }
                }
            }
            if(fileList.nextPageToken == null) break
            else nextPageToken = fileList.nextPageToken
        }

        setUploadFileSize(sum)
        return fileInfoList
    }

    override fun sync(excludeList: List<String>) {
        val fileInfoList = getFileInfoList()
        for(directory in directoryList){
            val dir = File(directory)
            if(dir.exists()){
                if(dir.isDirectory){
                    val listFiles = dir.listFiles()
                    if(listFiles != null){
                        for(f in listFiles){
                            if(f != null && f.exists()){
                                upload(f, fileInfoList)
                            }
                        }
                    }
                }
                else{
                    upload(dir, fileInfoList)
                }
            }
            else{
                // FileLogger.getInstance()?.log("sync","dir not exits ${dir.name} ${dir.absolutePath}")
            }
        }
        setLastBackupTime()
        getFileInfoList() //to calculate upload size
    }

    private fun setUploadFileSize(uploadSize: Long) {
        pref.edit().putLong("total_upload_size",uploadSize).apply()
    }

    private fun upload(file: File, fileInfoList : List<FileInfo>) : Boolean {
        for(fileInfo in fileInfoList){
            if(fileInfo.name == file.name && fileInfo.path == file.absolutePath){
                //file already uploaded, check md5
                val md5sum = MD5.calculateMD5(file)
                return if(fileInfo.md5sum == md5sum){ //no change
                    //FileLogger.getInstance()?.log("upload","file already uploaded, ${file.name} $md5sum")
                    false //false means not uploaded to drive
                } else{//file content changed, need to upload
                    //FileLogger.getInstance()?.log("upload","file change found, uploading ${file.name}")
                    val fileInputStream = FileInputStream(file)
                    driveServiceHelper.saveFile(fileInfo.id ,
                        fileInfo.name, fileInputStream ,
                        HashMap(),
                        fileInfo.parent?:"",
                        md5sum
                    )
                    fileInputStream.close()
//                    fileInfo.md5sum = md5sum
//                    saveList(fileInfoList)
                    //FileLogger.getInstance()?.log("upload","file upload done ${file.name}")
                    true
                }
            }
        }
        //new file
        //creating file to drive
        //FileLogger.getInstance()?.log("upload","new file found, uploading ${file.name}")
        val fileId = driveServiceHelper.createFile(file.name)
        val md5sum = MD5.calculateMD5(file)
        val fileInfo = FileInfo(fileId, file.name, file.absolutePath, file.parent?: "" , md5sum)
        val fileInputStream = FileInputStream(file)
        driveServiceHelper.saveFile(fileInfo.id ,
            fileInfo.name, fileInputStream ,
            HashMap(),
            fileInfo.parent?:"",
            md5sum
        )
        fileInputStream.close()
        //FileLogger.getInstance()?.log("upload","file upload done ${file.name}")
        return true
    }


    override fun restore(includeList : List<String>, excludeList: List<String>,rootDir : FilePath) {
        val fileInfoList = getFileInfoList()
        for(fileInfo in fileInfoList){
            if(includeList.isNotEmpty()){
                if(!includeList.contains(fileInfo.name)) continue
            }
            if(excludeList.isNotEmpty()){
                if(excludeList.contains(fileInfo.name)) continue
            }
            val parent = File(fileInfo.parent)
            if(!parent.exists()){
                parent.mkdirs()
            }
            val file = File(fileInfo.path)
            if(!file.exists()){
                file.createNewFile()
            }
            else{ //file exits in file system
                val md5sum = MD5.calculateMD5(file)
                if(md5sum == fileInfo.md5sum){ //both file are same, don't download
                    Log.e("restore","file found in file system, don't download ${fileInfo.name} ${fileInfo.path}")
                    continue
                }
            }
            Log.e("restore","downloading file ${fileInfo.name} ${fileInfo.path}")
            val fileOutputStream = FileOutputStream(file)
            driveServiceHelper.readFile(fileInfo.id, fileOutputStream,"")
            fileOutputStream.close()
            Log.e("restore","downloading complete ${fileInfo.name} ${fileInfo.path}")
        }
    }

    private fun setLastBackupTime(){
        //pref.edit().putLong("last_backup_time", CommunicatorTime.getInstance().now()).apply()
    }

    override fun getAvailableBackup(userId: String, deviceId : String): List<BackupInfo> {
        TODO("Not yet implemented")
    }

    override fun setRootDirsToBackup(rootDirs: List<FilePath>) {
        TODO("Not yet implemented")
    }

    override fun setIdentityMap(identityMap: HashMap<String, String>) {
        TODO("Not yet implemented")
    }

    /*override fun getLastBackupTime(): Long {
        return pref.getLong("last_backup_time", 0L)
    }

    override fun getTotalBackupSize(): Long {
        return pref.getLong("total_upload_size",0L)
    }*/

    override fun createFolder(name: String) {
        TODO("Not yet implemented")
    }
}