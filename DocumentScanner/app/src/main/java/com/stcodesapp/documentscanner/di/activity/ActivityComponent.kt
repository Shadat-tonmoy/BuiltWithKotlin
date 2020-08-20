package com.stcodesapp.documentscanner.di.activity


import com.stcodesapp.documentscanner.di.activity.modules.ActivityModule
import dagger.Subcomponent

@Subcomponent(modules = [ActivityModule::class])
interface ActivityComponent
{

}