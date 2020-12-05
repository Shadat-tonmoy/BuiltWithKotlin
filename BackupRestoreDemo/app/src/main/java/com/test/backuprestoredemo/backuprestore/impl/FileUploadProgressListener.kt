package com.test.backuprestoredemo.backuprestore.impl

import android.util.Log
import com.google.api.client.googleapis.media.MediaHttpUploader
import com.google.api.client.googleapis.media.MediaHttpUploaderProgressListener
import com.test.backuprestoredemo.backuprestore.DriveServiceHelper

class FileUploadProgressListener (private val callBack : DriveServiceHelper.UploadProgressListener?, private val filePath : String) : MediaHttpUploaderProgressListener
{

    companion object{
        private const val TAG = "FileUploadProgressListe"
    }

    private var uploaded = 0L
    private var uploadedInCurrentChunk = 0L


    override fun progressChanged(mediaHttpUploader: MediaHttpUploader?) {
        when (mediaHttpUploader!!.uploadState) {
            MediaHttpUploader.UploadState.INITIATION_STARTED ->
            {
                val uploadedBytes = mediaHttpUploader.numBytesUploaded
//                Log.e(TAG, "progressChanged: INITIATION_STARTED uploadedBytes : $uploadedBytes")

            }
            MediaHttpUploader.UploadState.INITIATION_COMPLETE ->
            {
//                Log.e(TAG, "progressChanged: INITIATION_COMPLETE")

            }
            MediaHttpUploader.UploadState.MEDIA_IN_PROGRESS ->
            {
                val uploadedBytes = mediaHttpUploader.numBytesUploaded
                val chunkSize = mediaHttpUploader.chunkSize
                val progress = mediaHttpUploader.progress
                uploadedInCurrentChunk = uploadedBytes - uploaded
                uploaded = uploadedBytes
                callBack?.onUploadProgressUpdate(filePath, uploadedInCurrentChunk)
                Log.e(TAG, "progressChanged: MEDIA_IN_PROGRESS uploadedBytes : $uploadedBytes, ChunkSize : $chunkSize File : $filePath Progress : $progress uploadedInCurrentChunk : $uploadedInCurrentChunk")

            }
            MediaHttpUploader.UploadState.MEDIA_COMPLETE ->
            {

//                mediaHttpUploader.setDirectUploadEnabled()
                val uploadedBytes = mediaHttpUploader.numBytesUploaded
                val chunkSize = mediaHttpUploader.chunkSize
                val progress = mediaHttpUploader.progress
                uploadedInCurrentChunk = uploadedBytes - uploaded
                uploaded = uploadedBytes
                callBack?.onUploadProgressUpdate(filePath, uploadedInCurrentChunk)
                Log.e(TAG, "progressChanged: MEDIA_COMPLETE uploadedBytes : $uploadedBytes, ChunkSize : $chunkSize File : $filePath Progress : $progress uploadedInCurrentChunk : $uploadedInCurrentChunk")

            }
        }
    }
}