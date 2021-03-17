package com.stcodesapp.documentscanner.database.managers

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.stcodesapp.documentscanner.database.daos.DocumentDao
import com.stcodesapp.documentscanner.database.entities.Document

class DocumentManager(private val context: Context, private val dao : DocumentDao)
{
    suspend fun createOrUpdateDocument(document: Document) : Long
    {
        val existingDocument = dao.getDocumentById(document.id)
        return if(existingDocument==null) {
            dao.insertDocument(document)
        } else {
            dao.updateDocument(document)
            document.id
        }
    }

    suspend fun updateDocument(document: Document) : Long
    {
        dao.updateDocument(document)
        return document.id
    }

    suspend fun getDocumentById(id : Long) : Document?
    {
        return dao.getDocumentById(id)
    }

    fun getLiveDocumentById(id : Long) : LiveData<Document>
    {
        return dao.getLiveDocumentById(id)
    }

    suspend fun deleteDocumentById(id : Long) : Int
    {
        return dao.deleteDocumentById(id)
    }

    fun getDocumentListLiveData() : LiveData<List<Document>>
    {
        return dao.getDocumentListLiveData()
    }
}