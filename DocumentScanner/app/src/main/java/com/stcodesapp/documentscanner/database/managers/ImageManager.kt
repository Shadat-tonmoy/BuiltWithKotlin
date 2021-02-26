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

    suspend fun updateImage(image: Image) : Long
    {
        dao.updateImage(image)
        return image.id
    }

    fun getDocumentPagesLiveData(docId : Long) : LiveData<List<Image>>
    {
        return dao.getAllImageLiveDataForDocument(docId)
    }

    fun getDocumentPagesValue(docId : Long) : List<Image>
    {
        return dao.getAllImagesForDocument(docId)
    }

    suspend fun getImageById(id : Long) : Image?
    {
        return dao.getImageById(id)
    }

    suspend fun deleteImage(image : Image) : Int
    {
        return dao.deleteImage(image)
    }

    suspend fun deleteImageById(id : Long) : Int
    {
        return dao.deleteImageById(id)
    }
}