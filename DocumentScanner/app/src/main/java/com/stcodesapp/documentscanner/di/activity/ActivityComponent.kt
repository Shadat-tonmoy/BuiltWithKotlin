package com.stcodesapp.documentscanner.di.activity


import com.stcodesapp.documentscanner.ui.main.MainActivity
import com.stcodesapp.documentscanner.di.activity.modules.ActivityModule
import com.stcodesapp.documentscanner.ui.documentPages.DocumentPagesActivity
import com.stcodesapp.documentscanner.ui.filterOption.FilterOptionFragment
import com.stcodesapp.documentscanner.ui.home.HomeFragment
import com.stcodesapp.documentscanner.ui.imageCrop.ImageCropActivity
import com.stcodesapp.documentscanner.ui.imageCrop.ImageCropItemFragment
import com.stcodesapp.documentscanner.ui.imageEdit.ImageEditItemFragment
import com.stcodesapp.documentscanner.ui.imageEdit.ImageEditActivity
import com.stcodesapp.documentscanner.ui.imageEffect.ImageEffectFragment
import com.stcodesapp.documentscanner.ui.more.MoreFragment
import com.stcodesapp.documentscanner.ui.paperEffect.PaperEffectFragment
import com.stcodesapp.documentscanner.ui.savedFiles.SavedFilesFragment
import dagger.Subcomponent

@Subcomponent(modules = [ActivityModule::class])
interface ActivityComponent
{
    fun inject(mainActivity: MainActivity)
    fun inject(mainActivity: DocumentPagesActivity)
    fun inject(homeFragment: HomeFragment)
    fun inject(savedFilesFragment: SavedFilesFragment)
    fun inject(moreFragment: MoreFragment)
    fun inject(imageEditActivity: ImageEditActivity)
    fun inject(imageEditItemFragment: ImageEditItemFragment)
    fun inject(paperEffectFragment: PaperEffectFragment)
    fun inject(imageEffectFragment: ImageEffectFragment)
    fun inject(filterOptionFragment: FilterOptionFragment)
    fun inject(imageCropActivity: ImageCropActivity)
    fun inject(imageCropItemFragment: ImageCropItemFragment)


}