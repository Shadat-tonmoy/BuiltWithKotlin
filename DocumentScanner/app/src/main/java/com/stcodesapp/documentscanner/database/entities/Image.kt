package com.stcodesapp.documentscanner.database.entities

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.stcodesapp.documentscanner.models.CropArea

@Entity(tableName = "image")
data class Image(
    @PrimaryKey(autoGenerate = true) var id : Long = 0,
    val path : String,
    val position : Int,
    val docId : Long,
    val rotationAngle : Double = 0.0,
    @Embedded val cropArea: CropArea? = null)