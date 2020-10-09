package com.stcodesapp.documentscanner.database.daos

import androidx.lifecycle.LiveData
import androidx.room.*
import com.stcodesapp.documentscanner.database.entities.Document

@Dao
interface DocumentDao
{
    @Query("SELECT * FROM document")
    fun getAllDocumentLiveData() : LiveData<List<Document>>

    @Insert
    suspend fun insertDocument(document: Document) : Long

    @Update
    suspend fun updateDocument(document: Document) : Int

    @Delete
    suspend fun deleteDocument(document: Document)

    @Query("SELECT * FROM document WHERE id = :id")
    suspend fun getDocumentById(id: Long) : Document?

}