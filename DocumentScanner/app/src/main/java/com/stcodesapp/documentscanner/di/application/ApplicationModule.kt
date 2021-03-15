package com.stcodesapp.documentscanner.di.application

import android.content.Context
import androidx.room.Room
import com.stcodesapp.documentscanner.DocumentScannerApp
import com.stcodesapp.documentscanner.database.core.AppDatabase
import com.stcodesapp.documentscanner.database.managers.DocumentManager
import com.stcodesapp.documentscanner.database.managers.ImageManager
import com.stcodesapp.documentscanner.helpers.CacheHelper
import com.stcodesapp.documentscanner.helpers.FileHelper
import com.stcodesapp.documentscanner.helpers.FilterHelper
import com.stcodesapp.documentscanner.helpers.ImageHelper
import com.stcodesapp.documentscanner.tasks.ImageToPdfTask
import com.stcodesapp.documentscanner.tasks.imageToPDF.ImageToPDFNotificationHelper
import com.stcodesapp.documentscanner.tasks.imageToPDF.ImageToPDFServiceHelper
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
        val db = Room.databaseBuilder(context, AppDatabase::class.java,"DocumentScannerDB")
            .fallbackToDestructiveMigration()
            .build()
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


    @Provides
    fun provideImageToPdfTask(context: Context) : ImageToPdfTask
    {
        return ImageToPdfTask(context)
    }

    @Provides
    fun provideCacheHelper(context: Context) : CacheHelper
    {
        return CacheHelper(context)
    }


    @Provides
    fun provideFileHelper(context: Context) : FileHelper
    {
        return FileHelper(context)
    }

    @Provides
    fun provideImageToPDFServiceHelper(context: Context) : ImageToPDFServiceHelper
    {
        return ImageToPDFServiceHelper(context)
    }

    @Provides
    fun provideImageToPDFNotificationHelper(context: Context) : ImageToPDFNotificationHelper
    {
        return ImageToPDFNotificationHelper(context)
    }

    @Provides
    fun provideFilterHelper(context: Context) : FilterHelper
    {
        return FilterHelper(context)
    }

    @Provides
    fun provideImageHelper(context: Context) : ImageHelper
    {
        return ImageHelper(context)
    }






}