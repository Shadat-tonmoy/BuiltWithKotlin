package com.stcodesapp.documentscanner.database.daos

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import com.stcodesapp.documentscanner.database.entities.Document

@Dao
interface DocumentDao
{
    @Query("SELECT * FROM document")
    fun getAllDocumentLiveData() : LiveData<List<Document>>
}