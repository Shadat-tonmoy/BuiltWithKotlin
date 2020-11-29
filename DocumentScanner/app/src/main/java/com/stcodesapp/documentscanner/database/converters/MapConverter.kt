package com.stcodesapp.documentscanner.database.converters

import android.graphics.PointF
import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class MapConverter {

    @TypeConverter
    fun fromString(value: String): Map<Int, PointF>? {
        val mapType = object : TypeToken<Map<Int, PointF>>() {

        }.type
        return Gson().fromJson(value, mapType)
    }

    @TypeConverter
    fun fromStringMap(map: Map<Int, PointF>?): String {
        val gson = Gson()
        return gson.toJson(map)
    }
}