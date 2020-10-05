package com.stcodesapp.documentscanner.database.daos

import androidx.lifecycle.LiveData
import androidx.room.*
import com.stcodesapp.documentscanner.database.entities.Image

@Dao
interface ImageDao
{
    @Query("SELECT * FROM image WHERE docId = :documentId")
    fun getAllImageLiveDataForDocument(documentId : Long) : LiveData<List<Image>>

    @Insert (onConflict = OnConflictStrategy.IGNORE)
    fun insertImage(image: Image)

    @Update
    fun updateImage(image: Image)

    @Delete
    fun deleteImage(image: Image)

}