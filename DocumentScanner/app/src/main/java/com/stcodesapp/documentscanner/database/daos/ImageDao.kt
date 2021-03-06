package com.stcodesapp.documentscanner.database.daos

import androidx.lifecycle.LiveData
import androidx.room.*
import com.stcodesapp.documentscanner.database.entities.Image

@Dao
interface ImageDao
{
    @Query("SELECT * FROM image WHERE docId = :documentId")
    fun getAllImageLiveDataForDocument(documentId : Long) : LiveData<List<Image>>


    @Query("SELECT * FROM image WHERE docId = :documentId")
    fun getAllImagesForDocument(documentId : Long) : List<Image>

    @Insert (onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertImage(image: Image) : Long

    @Update
    suspend fun updateImage(image: Image) : Int

    @Delete
    suspend fun deleteImage(image: Image)  : Int

    @Query("DELETE FROM image WHERE id = :id")
    suspend fun deleteImageById(id: Long)  : Int

    @Query("SELECT * FROM image WHERE id = :id")
    suspend fun getImageById(id : Long) : Image?

}