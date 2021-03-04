package com.stcodesapp.documentscanner.di.application

import com.stcodesapp.documentscanner.di.ViewModelModule
import com.stcodesapp.documentscanner.di.activity.ActivityComponent
import com.stcodesapp.documentscanner.di.activity.modules.ActivityModule
import com.stcodesapp.documentscanner.helpers.CacheHelper
import com.stcodesapp.documentscanner.tasks.imageToPDF.ImageToPDFService
import dagger.Component
import dagger.Provides
import javax.inject.Singleton

@Singleton
@Component(modules = [ApplicationModule::class, ViewModelModule::class])
interface ApplicationComponent
{
    fun getActivityComponent(activityModule: ActivityModule): ActivityComponent

    fun inejct(imageToPDFService: ImageToPDFService)
}