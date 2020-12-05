package com.test.backuprestoredemo.backuprestore.impl

import android.util.Log
import com.google.api.client.googleapis.media.MediaHttpDownloader
import com.google.api.client.googleapis.media.MediaHttpDownloaderProgressListener
import com.test.backuprestoredemo.backuprestore.DriveServiceHelper

class FileDownloadProgressListener (private val callBack : DriveServiceHelper.DownloadProgressListener, private val fileStreamSize : Int, private val filePath : String) : MediaHttpDownloaderProgressListener
{

    companion object{
        private const val TAG = "FileDownloadProgressLis"
    }


    override fun progressChanged(mediaHttpDownloader: MediaHttpDownloader?) {
        when (mediaHttpDownloader?.downloadState) {
            MediaHttpDownloader.DownloadState.NOT_STARTED ->
            {
                val uploadedBytes = mediaHttpDownloader.numBytesDownloaded
//                Log.e(TAG, "progressChanged: INITIATION_STARTED uploadedBytes : $uploadedBytes")

            }
            MediaHttpDownloader.DownloadState.MEDIA_IN_PROGRESS ->
            {
                val downloadedBytes = mediaHttpDownloader.numBytesDownloaded
//                val chunkSize = mediaHttpUploader.chunkSize
                callBack.onDownloadProgressUpdate(filePath, downloadedBytes)
                Log.e(TAG, "progressChanged: downloadedBytes : $downloadedBytes")

            }
            MediaHttpDownloader.DownloadState.MEDIA_COMPLETE ->
            {
//                mediaHttpUploader.setDirectUploadEnabled()
//                val uploadedBytes = mediaHttpUploader.numBytesUploaded
//                val chunkSize = mediaHttpUploader.chunkSize
                callBack.onDownloadProgressUpdate(filePath, fileStreamSize.toLong())
//                Log.e(TAG, "progressChanged: MEDIA_COMPLETE bytesUploaded : $uploadedBytes isDirectUploadEnabled : ${mediaHttpUploader.isDirectUploadEnabled}")

            }
        }
    }
}