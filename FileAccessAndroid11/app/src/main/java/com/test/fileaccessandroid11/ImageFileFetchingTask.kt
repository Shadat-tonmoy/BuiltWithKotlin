package com.test.fileaccessandroid11

import android.content.Context
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import kotlinx.coroutines.launch

class ImageFileFetchingTask(private val context: Context, private val listener : Listener) : BaseTask()
{

    interface Listener{
        fun onImageListFetched(imageURIList : List<Image>)
    }
    companion object{
        private const val TAG = "ImageFileFetchingTask"
    }

    fun fetchImages()
    {
        val imageList = ArrayList<Image>()
        ioCoroutine.launch {
            val uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI

            val projection = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                arrayOf(MediaStore.Images.Media._ID, MediaStore.Images.Media.DISPLAY_NAME, MediaStore.MediaColumns.DATE_ADDED, MediaStore.Images.Media.RELATIVE_PATH)
            } else {
                arrayOf(MediaStore.MediaColumns.DATA, MediaStore.MediaColumns.DATE_ADDED,
                    MediaStore.Images.Media.DISPLAY_NAME)

            }
            val sortOrder = MediaStore.MediaColumns.DATE_ADDED + " desc"

            val cursor = context.contentResolver.query(uri, projection, null, null, sortOrder)

            if (cursor != null)
            {
                val idIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
                val displayNameIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)
                //val relativePathIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.RELATIVE_PATH)
                while (cursor.moveToNext())
                {
                    val id = cursor.getString(idIndex)
                    val displayName = cursor.getString(displayNameIndex)
                    //val relativePath = cursor.getString(relativePathIndex)
                    val uri = Uri.withAppendedPath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)
                    imageList.add(Image(displayName,uri.path,uri))
                    Log.e(TAG, "fetchImages: displayName : $displayName, uri : $uri")
                }
                if (!cursor.isClosed) cursor.close()
                uiCoroutine.launch { listener.onImageListFetched(imageList) }
                /*if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
                {
                    val idIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
                    val displayNameIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)
                    val relativePathIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.RELATIVE_PATH)
                    while (cursor.moveToNext())
                    {
                        val id = cursor.getString(idIndex)
                        val displayName = cursor.getString(displayNameIndex)
                        val relativePath = cursor.getString(relativePathIndex)
                        val uri = Uri.withAppendedPath(
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                            id)

                        Log.e(TAG, "fetchImages: displayName : $displayName, relativePath : $relativePath, uri : $uri")
                    }
                    if (!cursor.isClosed) cursor.close()
                }
                else
                {
                    val imagePathIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
                    val displayNameIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)
                    while (cursor.moveToNext())
                    {
                        val displayName = cursor.getString(displayNameIndex)
                        val imagePath = cursor.getString(imagePathIndex)
                        Log.e(TAG, "fetchImages: displayName : $displayName, imagePath : $imagePath")
                    }
                    if (!cursor.isClosed) cursor.close()
                }*/
            }
        }

    }
    

}