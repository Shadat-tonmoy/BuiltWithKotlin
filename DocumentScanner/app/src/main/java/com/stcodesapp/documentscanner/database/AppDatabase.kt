package com.stcodesapp.documentscanner.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.stcodesapp.documentscanner.database.daos.DocumentDao
import com.stcodesapp.documentscanner.database.daos.ImageDao
import com.stcodesapp.documentscanner.database.entities.Document
import com.stcodesapp.documentscanner.database.entities.Image

@Database(entities = [Document::class, Image::class], version = 1)
abstract class AppDatabase : RoomDatabase()
{

    abstract fun documentDao() : DocumentDao

    abstract fun imageDao() : ImageDao


}