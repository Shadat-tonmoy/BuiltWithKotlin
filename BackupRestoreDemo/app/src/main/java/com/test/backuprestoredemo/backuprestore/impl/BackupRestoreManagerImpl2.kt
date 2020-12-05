package com.test.backuprestoredemo.backuprestore.impl

import android.accounts.Account
import android.content.Context
import android.util.Log
import com.google.api.client.extensions.android.http.AndroidHttp
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.json.gson.GsonFactory
import com.google.api.services.drive.Drive
import com.google.api.services.drive.DriveScopes
import com.google.gson.Gson
import com.test.backuprestoredemo.BuildConfig
import com.test.backuprestoredemo.backuprestore.DriveServiceHelper
import com.test.backuprestoredemo.backuprestore.MD5
import com.test.backuprestoredemo.backuprestore.interfaces.BackupRestoreManager
import com.test.backuprestoredemo.backuprestore.models.BackupInfo
import com.test.backuprestoredemo.backuprestore.models.FileInfo
import com.test.backuprestoredemo.backuprestore.models.FilePath
import com.test.backuprestoredemo.backuprestore.models.FileToBackup
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.*
import java.nio.charset.StandardCharsets
import java.util.*
import kotlin.collections.HashMap


/***
 * implementation of backup restore manager.
 * @param context application context
 * @param account Google sign in account
 * @param identityMap a hash map to match user identity (user id, device id). This class does not aware of the members of identitiyMap. It just iterate through it and match each key and value.
 * @param rootDirsToBackup list of [FilePath] object which contains the path of root folder from where backup process is started. FilePath contains two property.
 * storagePath : storage path of file  like  storage/32FA-1D18/Android/data/com.sil.communicator/files/${userID}/${deviceId}/
 * dataDir :  path of the data directory like Media/
 * @param recursiveOperation indicates whether we should backup given root folders ($storagePath/$dataDir) reccursively or not
 * @param uploadListener callback to show upload progress
 * @param downloadListener callback to show download progress
 */
class BackupRestoreManagerImpl2(private val context: Context, account : Account, private var identityMap : HashMap<String,String>, private var rootDirsToBackup : List<FilePath>, private val recursiveOperation : Boolean, private val uploadListener : UploadListener, private val downloadListener : DownloadListener) :
    BackupRestoreManager {

    companion object{
        private const val TAG = "BackupRestoreManagerImp"
    }

    private val driveServiceHelper : DriveServiceHelper
    // private val pref = context.getSharedPreferences("backup-restore", Context.MODE_PRIVATE)
    private val filesToBackup = mutableListOf<FileToBackup>()
    private var totalBytesToUpload = 0L
    private var totalUploadedBytes = 0L
    private var totalBytesToDownload = 0L
    private var totalDownloadedBytes = 0L

    private val downloadBytesMap = HashMap<String, Long>()
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
                    override fun onUploadProgressUpdate(filePath : String, uploadedBytes: Long)
                    {
                        totalUploadedBytes += uploadedBytes
                        //val logMsg = "onUploadProgressUpdate: UploadedBytesInThisSession : $uploadedBytes TotalUploaded : $totalUploadedBytes TotalToUpload : $totalBytesToUpload"
                        //Log.e(TAG, logMsg)
                        uploadListener.onUploadProgressUpdated(totalUploadedBytes, totalBytesToUpload)
                    }

                },
                object : DriveServiceHelper.DownloadProgressListener{
                    override fun onDownloadProgressUpdate(filePath: String, downloadedBytes: Long)
                    {
                        // totalDownloadedBytes += downloadedBytes
                         downloadBytesMap[filePath] = downloadedBytes
                         calculateTotalDownloadedBytes()
                         //totalUploadedBytes += uploadedBytes
                        // Log.e(TAG, "onDownloadProgressUpdate: DownloadedBytesInThisSession : $downloadedBytes TotalDownloaded : $totalDownloadedBytes TotalToDownload : $totalBytesToDownload")
                        downloadListener.onDownloadProgressUpdated(totalDownloadedBytes, totalBytesToDownload)
                    }

                }
            )
    }

    /**
     * rootDir = object containing storage path like for Media : (storage/32FA-1D18/Android/data/com.sil.communicator/files) for DB : (/data/user/0/com.sil.communicator/files/) and relative path of the root of data directory like for Media : (Media/), for DB : (temp_db/), if we want to start backup from files/ directory then this part should be an empty string
     * rootDirectory = absolute path of root directory from where recursive traversing is started (storage/32FA-1D18/Android/data/com.sil.communicator/files/Media) or (storage/32FA-1D18/Android/data/com.sil.communicator/files) or (/data/user/0/com.sil.communicator/files/temp_db)
     * */
    override fun sync(excludeList: List<String>) {
        val fileInfoList = getFileInfoList() // get existing file info list from Google Drive
        for(rootDir in rootDirsToBackup)    // iterate all the root directory to find new files to backup
        {
            val rootDirectory = File("${rootDir.storagePath}${rootDir.dataDir}")
            findNewDataToUpload(rootDirectory,fileInfoList, rootDir, excludeList)
        }
        Log.e("TAG", "sync: FilesToBackup : $filesToBackup\n\nRootDirsToBackup : $rootDirsToBackup")
        calculateTotalUploadSize()
        traverseAndUploadData(fileInfoList)
        getFileInfoList() //to calculate upload size
    }

    /**
     * Returns list of [FileInfo] object from Google Drive. This list includes all the available
     * backup file for specific ${user_id} and ${device_id} determined by [matchWithIdentityMap] method.
     * Here ${user_id} is the id of the logged in user and ${device_id} is the id of the device chosen
     * by the user (if multi-device backup is available in Google Drive).
     * */
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
                //val parentName = driveServiceHelper.queryFileByID(file.id).files[0]
                // Log.e(TAG, "getFileInfoList: Parent : $parentName")
                if(map != null && matchWithIdentityMap(map))
                {
                    val parent = map["parent"]?:""  // {Media/Communicator Image/Sent/} for Media, {temp_db/} for DB
                    val md5sum = map["md5sum"]?:""
                    // Log.e(TAG, "getFileInfoList: parent : $parent")
                    sum += file.quotaBytesUsed
                    // no backslash (/) is required between path segment as every path has already a backslash at the end
                    val filePath = "$parent${file.name}" //Media/Communicator Images/Sent/abc.jpg
                    val fileInfo = FileInfo(file.id, file.name, filePath, parent, md5sum)
                    //Log.e("TAG", "getFileInfoList: FileInfo $fileInfo")
                    fileInfoList.add(fileInfo)
                }
            }
            if(fileList.nextPageToken == null) break
            else nextPageToken = fileList.nextPageToken
        }
        Log.e(TAG, "getFileInfoList: $fileInfoList")
        uploadListener.onTotalBytesOnDriveCalculated(sum)
        return fileInfoList
    }

    private fun getFileInfoListToRestore(rootDir: FilePath) : List<FileInfo>{
        // FileLogger.getInstance()?.log("restore","request file list")
        val fileInfoList = mutableListOf<FileInfo>()
        var nextPageToken : String? = null
        var sum = 0L
        while(true){
            val fileList = driveServiceHelper.queryFiles(nextPageToken)
            // FileLogger.getInstance()?.log("sync","got file list ${fileList.files.size}")
            for(file in fileList.files){
                val map = file.appProperties
                Log.e(TAG, "getFileInfoListToRestore: map : $map identityMap : $identityMap")
                if(map != null && matchWithIdentityMap(map))
                {
                    Log.e(TAG, "getFileInfoListToRestore: ${file.name} id : ${file.id}" )
                    val parent = map["parent"]?:""
                    val md5sum = map["md5sum"]?:""
                    // no backslash (/) is required between path segment as every path has already a backslash at the end
                    val filePath = "$parent${file.name}"
                    val fileInfo = FileInfo(file.id, file.name, filePath, parent, md5sum)
                    if(shouldDownload(fileInfo, rootDir))
                    {
                        Log.e(TAG, "getFileInfoListToRestore: ShouldDownloadFile : ${fileInfo.name}")
                        sum += file.quotaBytesUsed
                        // Log.e("TAG", "getFileInfoList: FileInfo $fileInfo")
                        fileInfoList.add(fileInfo)
                    }
                }
            }
            if(fileList.nextPageToken == null) break
            else nextPageToken = fileList.nextPageToken
        }
        totalBytesToDownload = sum
        downloadListener.onTotalDownloadSizeCalculated(totalBytesToDownload)

        return fileInfoList
    }

    private fun matchWithIdentityMap(map : Map<String,String>) : Boolean
    {
        for((key,value) in identityMap)
        {
            if(map.containsKey(key))
            {
                if(map[key] != value) return false
            }
            else return false
        }
        return true
    }

    private fun findNewDataToUpload(file : File, fileInfoList: List<FileInfo>, rootDir : FilePath, excludeList: List<String>)
    {
         Log.e("TAG", "findNewDataToUpload: insideDir : ${file.absolutePath}")
        if(file.exists()){
            if(file.isDirectory)
            {
                val allFiles = file.listFiles()
                if(allFiles != null){
                    for(f in allFiles)
                    {
                        if(f != null && f.exists())
                        {
                            if(f.isDirectory && recursiveOperation)
                            {
                                findNewDataToUpload(f, fileInfoList, rootDir, excludeList)
                            }
                            else
                            {
                                 //Log.e("TAG", "findNewDataToUpload: FilePath : ${rootDir} excludedFiles : $excludeList")
                                if(!isFileExcluded(f.absolutePath,excludeList,rootDir)
                                    && shouldUpload(f, fileInfoList, rootDir))
                                {
                                    filesToBackup.add(FileToBackup(f.absolutePath, rootDir, f.length()))
                                }
                            }
                        }
                    }
                }
            }
            else
            {
                if(!isFileExcluded(file.absolutePath,excludeList,rootDir) && shouldUpload(file, fileInfoList, rootDir))
                {
                    filesToBackup.add(FileToBackup(file.absolutePath, rootDir, file.length()))
                }
            }
        }
        else
        {
            // FileLogger.getInstance()?.log("sync","dir not exits ${file.name} ${file.absolutePath}")
        }
    }

    private fun isFileExcluded(filePath : String, excludeList: List<String>, rootDir: FilePath) : Boolean
    {
        //Log.e(TAG, "isFileExcluded: ExcludeList : ${excludeList} rootDir : $rootDir")
        for(relativePath in excludeList)
        {
            val absolutePath = "${rootDir.storagePath}${rootDir.dataDir}$relativePath"
            if(absolutePath == filePath)
            {
                // Log.e(TAG, "isFileExcluded: TrueFor $absolutePath relativePath : $relativePath")
                return true
            }
        }
        return false

    }

    /**
     * Loop through a list of FileToBackup and calculate total bytes to upload in this backup service call
     * */
    private fun calculateTotalUploadSize()
    {
        totalBytesToUpload = 0L
        for(fileToBackup in filesToBackup)
        {
            totalBytesToUpload += fileToBackup.fileSizeInBytes
        }
    }

    /**
     * Loop through a list of [FileInfo] and call upload service to upload these files to Google Drive.
     * If file is already available in Google Drive and content is modified then file is Updated with same ID
     * else a new file is created with new ID and saved to Google Drive
     * @param fileInfoList file info list from google drive to check if file content is updated or a new file to upload
     * */
    private fun traverseAndUploadData(fileInfoList: List<FileInfo>)
    {
        for(fileToBackup in filesToBackup)
        {
             Log.e(TAG, "traverseAndUploadData: ${fileToBackup.filePath} RootDir : ${fileToBackup.rootDir}")
            upload(File(fileToBackup.filePath), fileInfoList, fileToBackup.rootDir)
        }
    }

    private fun getStringFromInputStream(inputStream: InputStream): String
    {
        val bufferSize = 1024
        val buffer = CharArray(bufferSize)
        val out = StringBuilder()
        val inputStreamReader: Reader = InputStreamReader(inputStream, StandardCharsets.UTF_8)
        var charsRead: Int = 0
        while (inputStreamReader.read(buffer, 0, buffer.size).also({ charsRead = it }) > 0) {
            out.append(buffer, 0, charsRead)
        }
        val jsonString = out.toString()
        return jsonString
    }

    /***
     * @param file file to upload
     * @param fileInfoList list of [FileInfo] from google drive to check if file content is updated or a new file to upload
     * @param rootDir instance of [FilePath] containing storage path and relative parent path of the file
     */
    private fun upload(file: File, fileInfoList : List<FileInfo>, rootDir: FilePath) : Boolean {
        for(fileInfo in fileInfoList) {
            val filePathFromDrive = rootDir.storagePath + fileInfo.path
            // rootDir.storagePath = storage/32FA-1D18/Android/data/com.sil.communicator/files/{$userId}}/{$deviceId}/
            // fileInfo.path = Media/Communicator Images/Sent/abc.jpg
            if(fileInfo.name == file.name && filePathFromDrive == file.absolutePath){
                //file already uploaded, check md5
                val md5sum = MD5.calculateMD5(file)
                return if(fileInfo.md5sum == md5sum){ //no change

                    // FileLogger.getInstance()?.log("upload","file already uploaded, ${file.name} $md5sum")
                    false //false means not uploaded to drive
                } else{//file content changed, need to upload
                    // FileLogger.getInstance()?.log("upload","file change found, uploading ${file.name}")
                    val fileInputStream = FileInputStream(file)
                    driveServiceHelper.saveFile(fileInfo.id ,
                        fileInfo.name, fileInputStream ,
                        identityMap,
                        fileInfo.parent?:"",
                        md5sum,
                        filePathFromDrive
                    )
                    fileInputStream.close()
                    // fileInfo.md5sum = md5sum
                    // saveList(fileInfoList)
                    // FileLogger.getInstance()?.log("upload","file upload done ${file.name}")
                    true
                }
            }
        }
        //new file
        //creating file to drive
        // FileLogger.getInstance()?.log("upload","new file found, uploading ${file.name}")
        val fileId = driveServiceHelper.createFile(file.name)
        val md5sum = MD5.calculateMD5(file)
        val fileInfo = FileInfo(fileId, file.name, file.absolutePath, getNewFileParent(file.absolutePath,rootDir) , md5sum)
        val fileInputStream = FileInputStream(file)
        driveServiceHelper.saveFile(fileInfo.id ,
            fileInfo.name, fileInputStream ,
            identityMap,
            fileInfo.parent?:"",
            md5sum,
            file.absolutePath
        )
        fileInputStream.close()
        // FileLogger.getInstance()?.log("upload","file upload done ${file.name} Parent : ${fileInfo.parent}")
        return true
    }

    private fun shouldUpload(file: File, fileInfoList : List<FileInfo>, rootDir: FilePath) : Boolean {
        for(fileInfo in fileInfoList) {
            val filePathFromDrive = rootDir.storagePath + fileInfo.path
            if(fileInfo.name == file.name && filePathFromDrive == file.absolutePath){
                //file already uploaded, check md5
                val md5sum = MD5.calculateMD5(file)
                return if(fileInfo.md5sum == md5sum){ //no change

                    // FileLogger.getInstance()?.log("upload","file already uploaded, ${file.name} $md5sum")
                    false //false means not uploaded to drive
                } else{//file content changed, need to upload
//                    Log.e("TAG", "shouldUpload: FileContentChanged!NeedToUpload")
                    true }
            }
        }
//        Log.e("TAG", "shouldUpload: NewFile!NeedToUpload")
        return true
    }

    private fun shouldDownload(fileInfo : FileInfo, rootDir: FilePath) : Boolean
    {
        val filePathFromDrive = rootDir.storagePath + fileInfo.path
        val localFilePath = "${rootDir.storagePath}${fileInfo.path}"
        val localFile = File(localFilePath)
        if(localFile.exists() && fileInfo.name == localFile.name && filePathFromDrive == localFile.absolutePath){
            //file already downloaded, check md5
            val md5sum = MD5.calculateMD5(localFile)
            return if(fileInfo.md5sum == md5sum){
                //no change
                // FileLogger.getInstance()?.log("download","file already downloaded, ${localFile.name} $md5sum")
                false //false means file already downloaded and no changes in md5 so no need to re-download
            } else{//file content changed, need to download
                    Log.e("TAG", "shouldUpload: FileContentChanged!NeedToDownload : ${localFile.name}")
                true }
        }
        return true
    }


    override fun restore(includeList : List<String>, excludeList: List<String> , rootDir : FilePath) {
        val fileInfoList = getFileInfoListToRestore(rootDir)
        Log.e(TAG, "restore: FileInfoListToDownload : $fileInfoList")
        for(fileInfo in fileInfoList){
            if(includeList.isNotEmpty()){
                if(!includeList.contains(fileInfo.name)) continue
            }
            if(excludeList.isNotEmpty()){
                if(excludeList.contains(fileInfo.name)) continue
            }
            // Log.e("TAG", "restore: Parent : ${fileInfo.parent} Path : ${fileInfo.path}")
            val outputDirPath = rootDir.storagePath + fileInfo.parent
            // Log.e(TAG, "restore: OutputDirPath : $outputDirPath")
            val outputDir = File(outputDirPath)
            if(!outputDir.exists()) outputDir.mkdirs()

            val outputFilePath = rootDir.storagePath + fileInfo.path
            val outputFile = File(outputFilePath)
            // Log.e("TAG", "restore: outputFilePath : $outputFilePath")
            if(!outputFile.exists()) outputFile.createNewFile()
            else
            { //file exits in file system
                val md5sum = MD5.calculateMD5(outputFile)
                if(md5sum == fileInfo.md5sum){ //both file are same, don't download
                    // Log.e("TAG", "restore: FileAlreadyDownloaded ${outputFile.name}")
                    continue
                }
            }

            val fileOutputStream = FileOutputStream(outputFile)
            driveServiceHelper.readFile(fileInfo.id, fileOutputStream, outputFilePath)
            // FileLogger.getInstance()?.log(TAG, "File Download Completed. Saved at $outputFilePath")
            // Log.e(TAG, "restore: willsaveas : $outputFilePath")
            fileOutputStream.close()
        }
    }

    /**
     * returns a list of [BackupInfo] object fetched from Google Drive based on device id
     * @param userId logged in user id
     * */
    override fun getAvailableBackup(userId : String, deviceId : String): List<BackupInfo>
    {
        val backupInfoFilePrefix = "backup_info_$userId"
        val files = driveServiceHelper.queryBackupInfoFiles(backupInfoFilePrefix).files
        val backupInfoList = mutableListOf<BackupInfo>()
        for(backupInfoFile in files)
        {
            val inputStream = driveServiceHelper.readInputStreamFromFile(backupInfoFile.id)
            val jsonString = getStringFromInputStream(inputStream)
            val backupInfoFromDrive = Gson().fromJson(jsonString,BackupInfo::class.java)
            backupInfoList.add(backupInfoFromDrive)
        }
        Log.e(TAG, "getAvailableBackup: list : $backupInfoList prefix : $backupInfoFilePrefix")
        return backupInfoList
    }

    private fun getNewFileParent(filePath : String, rootDir: FilePath) : String
    {
        /*storage/32FA-1D18/Android/data/com.sil.communicator/files/$userId/$deviceId/Media/Communicator Images/Sent/1602153756329.png*/

        //parent : Media/Communicator Images/Sent/
        // every folder path must end with a /

        val startIndex = rootDir.storagePath.length
        val endIndex = filePath.indexOf(File(filePath).name)
        val newFileParent = filePath.substring(startIndex,endIndex)
        Log.e(TAG, "getNewFileParent: $newFileParent")
        return filePath.substring(startIndex,endIndex)
    }

    private fun calculateTotalDownloadedBytes()
    {
        totalDownloadedBytes = 0
        for ((key, value) in downloadBytesMap)
        {
            val downloadedBytes = downloadBytesMap[key]
            if (downloadedBytes != null) {
                totalDownloadedBytes += downloadedBytes
            }
        }
    }

    override fun createFolder(name: String) {
        CoroutineScope(Dispatchers.IO).launch { traverseDrive() }
        /*val parentList = ArrayList<String>()

        driveServiceHelper.createFolder(name, emptyList()).addOnSuccessListener {
            Log.e(TAG, "createFolder: Success $it")
            parentList.add(it)

        }.addOnFailureListener {
            Log.e(TAG, "createFolder: Failed: $it")
        }
        driveServiceHelper.createFolder("$name-2", parentList).addOnSuccessListener {
            parentList.add(it)
            Log.e(TAG, "createFolder: Success $it ParentList : $parentList")
        }
        driveServiceHelper.createFolder("$name-3", parentList).addOnSuccessListener {
            parentList.add(it)
            Log.e(TAG, "createFolder: Success $it ParentList : $parentList")
            CoroutineScope(Dispatchers.IO).launch {
                val fileID = driveServiceHelper.createFileInsideFolder("tempAt${System.currentTimeMillis()}.txt",it)
                Log.e(TAG, "createFileInsideFolder : $fileID")
                val file = File("/storage/32FA-1D18/Android/data/com.sil.communicator/files/Media/Communicator Images/Sent/1606403681455.jpg")
                val fileInputStream = FileInputStream(file)
                driveServiceHelper.saveFile(fileID ,
                    "testFileInsideFolder.png", fileInputStream ,
                    identityMap,
                    "",
                    "md5sum",
                    "filePathFromDrive")
                fileInputStream.close()
                getFileInfoList()
            }
        }*/
    }


    private fun traverseDrive()
    {
        var nextPageToken : String? = null
        Log.e(TAG, "traverseDrive: FolderList : called")
        while(true){
            val folderList = driveServiceHelper.queryFolders(nextPageToken)
            Log.e(TAG, "traverseDrive: FolderList : $folderList")
            for(file in folderList.files)
            {
                Log.e(TAG, "traverseDrive: Folder FileInfo : Name : ${file.name} MimeType : ${file.mimeType} Parents : ${file.parents} Spaces : ${file.spaces} ID : ${file.id}")
                traverseFolder(file.id)
            }
            if(folderList.nextPageToken == null) break
            else nextPageToken = folderList.nextPageToken
        }
    }

    private fun traverseFolder(folderId : String)
    {
        var nextPageToken : String? = null
        while(true){
            val folderList = driveServiceHelper.queryInsideFolder(nextPageToken,folderId)
            for(file in folderList.files)
            {
                Log.e(TAG, "traverseDrive: InsideFolder : $folderId FileInfo : Name : ${file.name} MimeType : ${file.mimeType} Parents : ${file.parents} Spaces : ${file.spaces} ID : ${file.id}")

            }
            if(folderList.nextPageToken == null) break
            else nextPageToken = folderList.nextPageToken
        }
    }

    override fun setRootDirsToBackup(rootDirs: List<FilePath>) {
        this.rootDirsToBackup = rootDirs
    }

    override fun setIdentityMap(identityMap: HashMap<String, String>) {
        this.identityMap = identityMap
    }

    interface UploadListener{
        fun onTotalBytesOnDriveCalculated(totalBytesOnDrive : Long)
        fun onBackupInfoOnDriveCalculated(backupInfo: BackupInfo)
        fun onUploadProgressUpdated(totalUploadedBytes : Long, totalBytesToUpload : Long)
    }

    interface DownloadListener{
        fun onTotalDownloadSizeCalculated(totalBytesToDownload : Long)
        fun onDownloadProgressUpdated(totalDownloadedBytes : Long, totalBytesToDownlaod : Long)
    }
}