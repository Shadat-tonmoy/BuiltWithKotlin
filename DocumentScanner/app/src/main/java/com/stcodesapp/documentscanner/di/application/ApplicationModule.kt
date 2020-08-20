package com.tigerit.pothghat.di.application

import android.content.Context
import dagger.Module
import dagger.Provides

@Module
class ApplicationModule(private val context: Context)
{

    @Provides
    fun provideApplicationContext() : Context
    {
        return context
    }

}