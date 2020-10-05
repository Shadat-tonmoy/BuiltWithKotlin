package com.tigerit.pothghat.di.application

import android.content.Context
import androidx.room.Room
import com.stcodesapp.documentscanner.DocumentScannerApp
import com.stcodesapp.documentscanner.database.AppDatabase
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
        val db = Room.databaseBuilder(context, AppDatabase::class.java,"DocumentScannerDB").build();
        return db
    }

}