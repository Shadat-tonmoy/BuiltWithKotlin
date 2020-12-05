package com.test.backuprestoredemo.backuprestore

import android.content.ContentResolver
import android.content.Intent
import android.net.Uri
import android.provider.OpenableColumns
import android.util.Log
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.api.client.http.InputStreamContent
import com.google.api.services.drive.Drive
import com.google.api.services.drive.model.File
import com.google.api.services.drive.model.FileList
import com.test.backuprestoredemo.backuprestore.impl.FileDownloadProgressListener
import com.test.backuprestoredemo.backuprestore.impl.FileUploadProgressListener
import java.io.*
import java.util.*
import java.util.concurrent.Callable
import java.util.concurrent.Executor
import java.util.concurrent.Executors
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


/**
 * A utility for performing read/write operations on Drive files via the REST API and opening a
 * file picker UI via Storage Access Framework.
 */
class DriveServiceHelper(driveService: Drive, private val uploadProgressListener: UploadProgressListener?, private val downloadProgressListener : DownloadProgressListener? = null) {

    companion object{
        private const val TAG = "DriveServiceHelper"
    }
    private val mExecutor: Executor = Executors.newSingleThreadExecutor()
    private val mDriveService: Drive = driveService

    interface UploadProgressListener
    {
        fun onUploadProgressUpdate(filePath : String, uploadedBytes : Long)
    }

    interface DownloadProgressListener
    {
        fun onDownloadProgressUpdate(filePath : String, downloadedBytes : Long)
    }

    /**
     * Creates a text file in the user's My Drive folder and returns its file ID.
     */
    fun createFileAsync(name: String): Task<String> {
        return Tasks.call(mExecutor, Callable {
            val metadata = File()
                .setParents(Collections.singletonList("appDataFolder"))
                .setMimeType("application/octet-stream")
                .setName(name)
            val googleFile = mDriveService.files().create(metadata).execute()
                ?: throw IOException("Null result when requesting file creation.")
            googleFile.id
        })
    }

    fun createFile(name: String): String {
        val metadata = File()
            .setParents(Collections.singletonList("appDataFolder"))
            .setMimeType("application/octet-stream")
            .setName(name)
        val googleFile = mDriveService.files().create(metadata).execute()
            ?: throw IOException("Null result when requesting file creation.")
        Log.e(TAG, "createFileInsideGoogleDrive : $googleFile")
        return googleFile.id
    }

    fun createFileInsideFolder(name: String, folderId : String): String {
        val metadata = File()
            .setParents(Collections.singletonList(folderId))
            .setMimeType("application/octet-stream")
            .setName(name)
        val googleFile = mDriveService.files().create(metadata).execute()
            ?: throw IOException("Null result when requesting file creation.")
        Log.e(TAG, "createFileInsideGoogleDrive : $googleFile")
        return googleFile.id
    }

//    fun getUploadSize(): Long {
//        var sum = 0L
//        val fileList = mDriveService.files().list().setFields("items/quotaBytesUsed").setSpaces("appDataFolder").execute()
//        if(fileList != null){
//            for(file in fileList.files){
//                sum += file.quotaBytesUsed
//            }
//        }
//        return sum
//    }

    fun createFolder(name: String,parent: List<String>): Task<String> {
        val parents = ArrayList<String>() // Collections.singletonList("appDataFolder",parent)
        parents.add("appDataFolder")
        parents.addAll(parent)
        return Tasks.call(mExecutor, Callable {
            val metadata = File()
                .setParents(parents)
                .setMimeType("application/vnd.google-apps.folder")
                .setName(name)
            val googleFile = mDriveService.files().create(metadata).execute()
                ?: throw IOException("Null result when requesting file creation.")
            Log.e(TAG, "createFolder: $googleFile")
            googleFile.id
        })
    }

    /**
     * Opens the file identified by `fileId` and returns a [Pair] of its name and
     * contents.
     */
    fun readFileAsync(fileId: String?, storePath : String): Task<String> {
        return Tasks.call(mExecutor, Callable {
            // Retrieve the metadata as a File object.
            val metadata = mDriveService.files()[fileId].execute()
            val name = metadata.name
            mDriveService.files()[fileId].executeMediaAsInputStream().use { `is` ->
                val file = java.io.File(storePath, name)
                if(!file.exists()){
                    file.createNewFile()
                }
                val fileOutputStream = FileOutputStream(file)
                BufferedInputStream(`is`).use { reader ->
                    var read : Int
                    val buffer = ByteArray(8388608)
                    while (reader.read(buffer).also { read = it } > 0) {
                        fileOutputStream.write(buffer,0, read)
                    }
                    fileOutputStream.flush()
                    fileOutputStream.close()
                    file.absolutePath
                }
            }
        }
        )
    }

    fun readFile(fileId: String?, fileOutputStream: FileOutputStream, filePath: String){
        val metadata = mDriveService.files()[fileId].execute()
        val name = metadata.name
        val file = mDriveService.files()[fileId]
        val downloader = file.mediaHttpDownloader
        downloader.progressListener = FileDownloadProgressListener(downloadProgressListener!!,0, filePath)
        file.executeMediaAsInputStream().use { inputStream ->
            BufferedInputStream(inputStream).use { reader ->
                var read : Int
                var totalSavedByte = 0L
                val buffer = ByteArray(8388608)
                while (reader.read(buffer).also { read = it } > 0) {
                    fileOutputStream.write(buffer,0, read)
                    totalSavedByte += read
                    Log.e("TAG", "readFile: TotalBytes : $totalSavedByte For $filePath")
                    downloadProgressListener?.onDownloadProgressUpdate(filePath,totalSavedByte)
                }
                fileOutputStream.flush()
            }
        }
    }


    fun readInputStreamFromFile(fileId: String?) : InputStream{
        val metadata = mDriveService.files()[fileId].execute()
        val name = metadata.name
        val file = mDriveService.files()[fileId]
        return file.executeMediaAsInputStream()
    }

    /**
     * Updates the file identified by `fileId` with the given `name` and `content`.
     */
    fun saveFileAsync(fileId: String?, name: String?, fileInputStream: FileInputStream , userId : String, parent : String, path : String): Task<Void?> {
        return Tasks.call(mExecutor, Callable<Void?> {
            // Create a File containing any metadata changes.
            val metadata = File().setName(name)
            val map = HashMap<String,String>()
            map["user-id"] = userId
            map["parent"] = parent
            map["path"] = path
            metadata.appProperties = map
            // Update the metadata and contents.
            mDriveService.files().update(fileId, metadata, InputStreamContent("application/octet-stream", fileInputStream)).execute()
            null
        }
        )
    }

    fun saveFile(fileId: String?, name: String?, fileInputStream: FileInputStream, identitiyMap : HashMap<String,String>, parent : String, md5sum : String, localFilePath : String = ""){
        val metadata = File().setName(name)
        val map = HashMap<String,String>()
        for((key,value) in identitiyMap)
        {
            map[key] = value
        }
        // map["userId"] = userId
        map["parent"] = parent
        map["md5sum"] = md5sum
        metadata.appProperties = map
        // FileLogger.getInstance()?.log(TAG,"Saving file with map : $map")
        // Update the metadata and contents.
        val inputStreamContent = InputStreamContent("application/octet-stream", fileInputStream)
        inputStreamContent.length = fileInputStream.available().toLong()
        val update = mDriveService.files().update(fileId, metadata, inputStreamContent)
        val mediaUploader = update.mediaHttpUploader
        // mediaUploader.chunkSize = 3*1024*1024
        // val cz = mediaUploader.chunkSize
        // val totalFileStreamSize = fileInputStream.available()
        // Log.e("TAG", "saveFile: TotalFileSize : ${fileInputStream.available()}")

        mediaUploader.progressListener = FileUploadProgressListener(uploadProgressListener, localFilePath)
        update.execute()
    }

    /**
     * Returns a [FileList] containing all the visible files in the user's My Drive.
     * The returned list will only contain files visible to this app, i.e. those which were
     * created by this app. To perform operations on files not created by the app, the project must
     * request Drive Full Scope in the [Google
     * Developer's Console](https://play.google.com/apps/publish) and be submitted to Google for verification.
     */
    fun queryFilesAsync(): Task<FileList> {
        return Tasks.call(mExecutor, Callable {
            mDriveService.files().list().setFields("*").setSpaces("appDataFolder").execute()
        }
        )
    }

    fun queryFiles(token : String? = null): FileList {
        var fileList =  mDriveService.files().list()
            .setFields("*")
            .setSpaces("appDataFolder")
        if(token != null) fileList = fileList.setPageToken(token)
        return fileList.execute()
    }

    fun queryFolders(token : String? = null): FileList {
        var fileList =  mDriveService.files().list()
            .setQ("mimeType='application/vnd.google-apps.folder'")
            .setFields("*")
            .setSpaces("appDataFolder")
        if(token != null) fileList = fileList.setPageToken(token)
        return fileList.execute()
    }

    fun queryInsideFolder(token : String? = null, folderId: String): FileList {
        var fileList =  mDriveService.files().list()
            .setQ("'$folderId' in parents")
            .setFields("*")
            .setSpaces("appDataFolder")
        if(token != null) fileList = fileList.setPageToken(token)
        return fileList.execute()
    }

    /**
     * method to query backup info files with userId
     * */
    fun queryBackupInfoFiles(userId: String): FileList {
        val fileList =  mDriveService.files().list()
            .setFields("*")
            .setQ("name contains '${userId.substring(0,20)}'")
            .setSpaces("appDataFolder")
        //if(token != null) fileList = fileList.setPageToken(token)
        return fileList.execute()
    }

    fun queryFileByID(fileId : String): FileList {
        val fileList =  mDriveService.files().list()
            .setFields("*")
            .setQ("id='$fileId'")
            .setSpaces("appDataFolder")
        //if(token != null) fileList = fileList.setPageToken(token)
        return fileList.execute()
    }

    /**
     * Returns an [Intent] for opening the Storage Access Framework file picker.
     */
    fun createFilePickerIntent(): Intent {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.type = "text/plain"
        return intent
    }

    /**
     * Opens the file at the `uri` returned by a Storage Access Framework [Intent]
     * created by [.createFilePickerIntent] using the given `contentResolver`.
     */
    fun openFileUsingStorageAccessFramework(
        contentResolver: ContentResolver,
        uri: Uri
    ): Task<Pair<String, String>> {
        return Tasks.call(mExecutor, Callable {
            // Retrieve the document's display name from its metadata.
            var name = ""
            contentResolver.query(uri, null, null, null, null).use { cursor ->
                name = if (cursor != null && cursor.moveToFirst()) {
                    val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    cursor.getString(nameIndex)
                } else {
                    throw IOException("Empty cursor returned for file.")
                }
            }
            // Read the document's contents as a String.
            var content = ""
            contentResolver.openInputStream(uri).use { `is` ->
                BufferedReader(InputStreamReader(`is`!!)).use { reader ->
                    val stringBuilder = StringBuilder()
                    var line: String?
                    while (reader.readLine().also { line = it } != null) {
                        stringBuilder.append(line)
                    }
                    content = stringBuilder.toString()
                }
            }
            Pair(name, content)
        }
        )
    }
}