package com.stcodesapp.documentscanner.di.activity


import com.stcodesapp.documentscanner.ui.main.MainActivity
import com.stcodesapp.documentscanner.di.activity.modules.ActivityModule
import com.stcodesapp.documentscanner.ui.documentPages.DocumentPagesActivity
import com.stcodesapp.documentscanner.ui.home.HomeFragment
import com.stcodesapp.documentscanner.ui.imageEdit.ImagePreviewActivity
import dagger.Subcomponent

@Subcomponent(modules = [ActivityModule::class])
interface ActivityComponent
{
    fun inject(mainActivity: MainActivity)
    fun inject(mainActivity: DocumentPagesActivity)
    fun inject(imagePreviewActivity: ImagePreviewActivity)
    fun inject(homeFragment: HomeFragment)

}