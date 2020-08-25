package com.stcodesapp.documentscanner.di.activity


import com.stcodesapp.documentscanner.ui.main.MainActivity
import com.stcodesapp.documentscanner.di.activity.modules.ActivityModule
import com.stcodesapp.documentscanner.ui.documentPages.DocumentPagesActivity
import dagger.Subcomponent

@Subcomponent(modules = [ActivityModule::class])
interface ActivityComponent
{
    fun inject(mainActivity: MainActivity)
    fun inject(mainActivity: DocumentPagesActivity)

}