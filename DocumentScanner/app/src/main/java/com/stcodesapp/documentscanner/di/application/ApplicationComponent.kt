package com.tigerit.pothghat.di.application

import com.stcodesapp.documentscanner.di.ViewModelModule
import com.stcodesapp.documentscanner.di.activity.ActivityComponent
import com.stcodesapp.documentscanner.di.activity.modules.ActivityModule
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [ApplicationModule::class, ViewModelModule::class])
interface ApplicationComponent
{
    fun getActivityComponent(activityModule: ActivityModule): ActivityComponent
}