package com.stcodesapp.documentscanner.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "document")
data class Document (
    @PrimaryKey(autoGenerate = true) var id : Long = 0,
    val path : String,
    val title : String,
    var thumbPath : String = "",
    val lastModified : Long)