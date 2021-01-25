package com.test.backuprestoredemo.helper

import android.annotation.SuppressLint
import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.provider.OpenableColumns
import java.io.*

class FilePathHelper
{
    private val TAG = "FileUtil"
    val DOCUMENTS_DIR = "documents"

    @SuppressLint("NewApi")
    fun getPath(context: Context, uri: Uri): String? {
//        Logger.showLog(TAG,"URI Path"+uri.getPath());
        //check for KITKAT or above
        val isKitKat =
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
//            Logger.showLog(TAG,"isKitKat");
            if (isExternalStorageDocument(uri)) {
                val docId = DocumentsContract.getDocumentId(uri)
                val split = docId.split(":").toTypedArray()
                val type = split[0]

//                Logger.showLog(TAG,"isExternalStorageDocument "+type+" split : "+split[1]);
                return if ("primary".equals(type, ignoreCase = true)) {
                    //                    Logger.showLog(TAG,"primary");
                    val path = Environment.getExternalStorageDirectory()
                        .toString() + "/" + split[1]
                    //                    Logger.showLog(TAG,"Path : "+path);
                    //                    return "/storage/emulated/"+type + "/" + split[1];
                    Environment.getExternalStorageDirectory()
                        .toString() + "/" + split[1]
                } else {
                    File.separator + "storage" + File.separator + type + File.separator + split[1]
                }
            } else if (isDownloadsDocument(uri)) {
//                Logger.showLog(TAG,"isDownloadsDocument");
                val id = DocumentsContract.getDocumentId(uri)
                if (id != null && id.startsWith("raw:")) {
                    return id.substring(4)
                }
                val contentUriPrefixesToTry = arrayOf(
                    "content://downloads/public_downloads",
                    "content://downloads/my_downloads",
                    "content://downloads/all_downloads"
                )
                for (contentUriPrefix in contentUriPrefixesToTry) {
                    val contentUri = ContentUris.withAppendedId(
                        Uri.parse(contentUriPrefix), java.lang.Long.valueOf(id)
                    )
                    try {
                        val path = getDataColumn(context, contentUri, null, null)
                        if (path != null) {
                            return path
                        }
                    } catch (e: Exception) {
                    }
                }

                // path could not be retrieved using ContentResolver, therefore copy file to accessible cache using streams
                /*final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);*/return getPathByMovingToCache(
                    context,
                    uri
                )
            } else if (isMediaDocument(uri)) {
//                Logger.showLog(TAG,"isMediaDocument");
                val docId = DocumentsContract.getDocumentId(uri)
                val split = docId.split(":").toTypedArray()
                val type = split[0]
                var contentUri: Uri? = null
                if ("image" == type) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                } else if ("video" == type) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                } else if ("audio" == type) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                }
                val selection = "_id=?"
                val selectionArgs = arrayOf(
                    split[1]
                )
                try {
                    val path =
                        getDataColumn(context, contentUri, selection, selectionArgs)
                    if (path != null) return path
                } catch (e: Exception) {
                }
                return getPathByMovingToCache(context, uri)
            }
        } else if ("content".equals(uri.scheme, ignoreCase = true)) {
//            Logger.showLog(TAG,"content");
            // Return the remote address
            if (isGooglePhotosUri(uri)) return uri.lastPathSegment
            try {
                val path = getDataColumn(context, uri, null, null)
                if (path != null) return path
            } catch (e: Exception) {
            }
            return getPathByMovingToCache(context, uri)
        } else if ("file".equals(uri.scheme, ignoreCase = true)) {
//            Logger.showLog(TAG,"file");
            return uri.path
        }
        return null
    }

    private fun getPathByMovingToCache(
        context: Context,
        uri: Uri
    ): String? {
        val fileName = getFileName(context, uri)
        val cacheDir = getDocumentCacheDir(context)
        val file = generateFileName(fileName, cacheDir)
        var destinationPath: String? = null
        if (file != null) {
            destinationPath = file.absolutePath
            saveFileFromUri(context, uri, destinationPath)
        }
        return destinationPath
    }

    fun getDataColumn(
        context: Context, uri: Uri?, selection: String?,
        selectionArgs: Array<String>?
    ): String? {
        var cursor: Cursor? = null
        val column = "_data"
        val projection = arrayOf(
            column
        )
        try {
            cursor = context.contentResolver.query(
                uri!!, projection, selection, selectionArgs,
                null
            )
            if (cursor != null && cursor.moveToFirst()) {
                val index = cursor.getColumnIndexOrThrow(column)
                return cursor.getString(index)
            }
        } finally {
            cursor?.close()
        }
        return null
    }

    fun getFileName(context: Context, uri: Uri): String? {
        val mimeType = context.contentResolver.getType(uri)
        var filename: String? = null
        if (mimeType == null && context != null) {
            val path = getPath(context, uri)
            filename = if (path == null) {
                getName(uri.toString())
            } else {
                val file = File(path)
                file.name
            }
        } else {
            val returnCursor = context.contentResolver.query(
                uri, null,
                null, null, null
            )
            if (returnCursor != null) {
                val nameIndex =
                    returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                returnCursor.moveToFirst()
                filename = returnCursor.getString(nameIndex)
                returnCursor.close()
            }
        }
        return filename
    }

    fun getDocumentCacheDir(context: Context): File {
        val dir = File(context.cacheDir, DOCUMENTS_DIR)
        if (!dir.exists()) {
            dir.mkdirs()
        }
        //        logDir(context.getCacheDir());
//        logDir(dir);
        return dir
    }

    fun deleteDocumentCacheDir(context: Context) {
        val dir = File(context.cacheDir, DOCUMENTS_DIR)
        //        Logger.showLog("WillDeleteCache","DIR : "+dir.getAbsolutePath());
        if (dir.exists()) {
            val result = deleteDir(dir)
            //            Logger.showLog("WillDeleteCache","DIR : "+dir.getAbsolutePath()+" result "+result);
        }
//        logDir(context.getCacheDir());
//        logDir(dir);
    }

    fun deleteDir(dir: File?): Boolean {
        if (dir != null && dir.isDirectory) {
            val children = dir.list()
            for (i in children.indices) {
                val success = deleteDir(File(dir, children[i]))
                if (!success) {
                    return false
                }
            }
        }

        // The directory is now empty so delete it
        return dir!!.delete()
    }

    /* private static void logDir(File dir) {
        if(!DEBUG) return;
        Log.d(TAG, "Dir=" + dir);
        File[] files = dir.listFiles();
        for (File file : files) {
            Log.d(TAG, "File=" + file.getPath());
        }
    }*/

    /* private static void logDir(File dir) {
        if(!DEBUG) return;
        Log.d(TAG, "Dir=" + dir);
        File[] files = dir.listFiles();
        for (File file : files) {
            Log.d(TAG, "File=" + file.getPath());
        }
    }*/
    fun getName(filename: String?): String? {
        if (filename == null) {
            return null
        }
        val index = filename.lastIndexOf('/')
        return filename.substring(index + 1)
    }


    fun generateFileName(name: String?, directory: File?): File? {
        var name = name ?: return null
        var file = File(directory, name)
        if (file.exists()) {
            var fileName = name
            var extension = ""
            val dotIndex = name.lastIndexOf('.')
            if (dotIndex > 0) {
                fileName = name.substring(0, dotIndex)
                extension = name.substring(dotIndex)
            }
            var index = 0
            while (file.exists()) {
                index++
                name = "$fileName($index)$extension"
                file = File(directory, name)
            }
        }
        try {
            if (!file.createNewFile()) {
                return null
            }
        } catch (e: IOException) {
            return null
        }

//        logDir(directory);
        return file
    }


    fun isExternalStorageDocument(uri: Uri): Boolean {
        return "com.android.externalstorage.documents" == uri.authority
    }

    fun isDownloadsDocument(uri: Uri): Boolean {
        return "com.android.providers.downloads.documents" == uri.authority
    }

    fun isMediaDocument(uri: Uri): Boolean {
        return "com.android.providers.media.documents" == uri.authority
    }

    fun isGooglePhotosUri(uri: Uri): Boolean {
        return "com.google.android.apps.photos.content" == uri.authority
    }

    @Throws(IOException::class)
    fun copy(original: File?, copied: File?): File? {
        val `in`: InputStream = BufferedInputStream(
            FileInputStream(original)
        )
        val out: OutputStream = BufferedOutputStream(
            FileOutputStream(copied)
        )
        val buffer = ByteArray(1024)
        var lengthRead: Int
        while (`in`.read(buffer).also { lengthRead = it } > 0) {
            out.write(buffer, 0, lengthRead)
            out.flush()
        }
        return copied
    }

    private fun saveFileFromUri(
        context: Context,
        uri: Uri,
        destinationPath: String?
    ) {
        var `is`: InputStream? = null
        var bos: BufferedOutputStream? = null
        try {
            `is` = context.contentResolver.openInputStream(uri)
            bos = BufferedOutputStream(FileOutputStream(destinationPath, false))
            val buf = ByteArray(1024)
            `is`!!.read(buf)
            do {
                bos.write(buf)
            } while (`is`.read(buf) != -1)
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            try {
                `is`?.close()
                bos?.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }
}