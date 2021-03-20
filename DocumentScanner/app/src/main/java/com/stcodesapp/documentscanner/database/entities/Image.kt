package com.stcodesapp.documentscanner.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "image")
data class Image(
    @PrimaryKey(autoGenerate = true) var id : Long = 0,
    var path : String,
    var position : Int,
    var docId : Long,
    var rotationAngle : Double = 0.0,
    var cropArea: String? = null,
    var croppingRatio : Double = 1.0,
    var isCropped : Boolean = false,
    var filterName : String = "",
    var filterJson : String = "",
    var customFilterJson : String = "",
    var paperEffectJson : String = "",
    ) : Serializable