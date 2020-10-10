package com.stcodesapp.documentscanner.database.managers

import android.content.Context
import androidx.lifecycle.LiveData
import com.stcodesapp.documentscanner.database.daos.ImageDao
import com.stcodesapp.documentscanner.database.entities.Image

class ImageManager(private val context: Context, private val dao : ImageDao)
{
    suspend fun createOrUpdateImage(image: Image) : Long
    {
        val existingImage = dao.getImageById(image.id)
        return if(existingImage==null) {
            dao.insertImage(image)
        } else {
            dao.updateImage(image)
            image.id
        }
    }

    fun getDocumentPagesLiveData(docId : Long) : LiveData<List<Image>>
    {
        return dao.getAllImageLiveDataForDocument(docId)
    }
}