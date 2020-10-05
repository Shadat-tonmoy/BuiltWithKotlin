package com.stcodesapp.documentscanner.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "document")
data class Document (
    @PrimaryKey val id : Long,
    val path : String,
    val thumbPath : String,
    val lastModified : Long)