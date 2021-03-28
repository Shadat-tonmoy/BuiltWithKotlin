package com.stcodesapp.documentscanner.database.core

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.stcodesapp.documentscanner.database.converters.MapConverter
import com.stcodesapp.documentscanner.database.daos.DocumentDao
import com.stcodesapp.documentscanner.database.daos.ImageDao
import com.stcodesapp.documentscanner.database.entities.Document
import com.stcodesapp.documentscanner.database.entities.Image

@Database(entities = [Document::class, Image::class], version = 10)
@TypeConverters(MapConverter::class)
abstract class AppDatabase : RoomDatabase()
{

    abstract fun documentDao() : DocumentDao

    abstract fun imageDao() : ImageDao


}