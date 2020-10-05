package com.stcodesapp.documentscanner.database.entities

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.stcodesapp.documentscanner.models.CropArea

@Entity(tableName = "image")
data class Image(
    @PrimaryKey val id : Long,
    val path : String,
    val position : Int,
    val docId : Long,
    val rotationAngle : Double,
    @Embedded val cropArea: CropArea)