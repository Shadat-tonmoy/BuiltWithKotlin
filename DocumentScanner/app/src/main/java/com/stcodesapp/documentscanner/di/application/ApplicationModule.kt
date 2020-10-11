package com.tigerit.pothghat.di.application

import android.content.Context
import androidx.room.Room
import com.stcodesapp.documentscanner.DocumentScannerApp
import com.stcodesapp.documentscanner.R
import com.stcodesapp.documentscanner.database.AppDatabase
import com.stcodesapp.documentscanner.database.managers.DocumentManager
import com.stcodesapp.documentscanner.database.managers.ImageManager
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class ApplicationModule(private val context: Context)
{

    @Provides
    fun provideApplicationContext() : Context
    {
        return context
    }

    @Provides
    fun provideApplication() : DocumentScannerApp
    {
        return context as DocumentScannerApp
    }

    @Provides
    @Singleton
    fun provideAppDB(context: Context) : AppDatabase
    {
        val db = Room.databaseBuilder(context, AppDatabase::class.java,context.getString(R.string.app_db_name)).build()
        return db
    }

    @Provides
    fun provideDocumentManager(context: Context, db : AppDatabase) : DocumentManager
    {
        return DocumentManager(context,db.documentDao())
    }

    @Provides
    fun provideImageManager(context: Context, db : AppDatabase) : ImageManager
    {
        return ImageManager(context,db.imageDao())
    }



}