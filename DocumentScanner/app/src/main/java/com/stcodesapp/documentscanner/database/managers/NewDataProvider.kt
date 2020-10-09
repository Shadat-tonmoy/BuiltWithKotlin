package com.stcodesapp.documentscanner.database.managers

import com.stcodesapp.documentscanner.database.entities.Document
import com.stcodesapp.documentscanner.database.entities.Image
import com.stcodesapp.documentscanner.helpers.getFileNameFromPath

fun getNewDocument(documentPath : String) : Document
{
    return Document(path = documentPath,
        title = getFileNameFromPath(documentPath),
        lastModified = System.currentTimeMillis())
}

fun getNewImage(path : String, position : Int, docId : Long) : Image
{
    return Image(path = path,
        position = position,
        docId = docId)
}