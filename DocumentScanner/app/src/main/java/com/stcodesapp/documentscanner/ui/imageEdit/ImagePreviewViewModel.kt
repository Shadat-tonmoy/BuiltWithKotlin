package com.stcodesapp.documentscanner.ui.imageEdit

import android.content.Intent
import com.stcodesapp.documentscanner.base.BaseViewModel
import com.stcodesapp.documentscanner.constants.Tags
import javax.inject.Inject

class ImagePreviewViewModel @Inject constructor() : BaseViewModel()
{
    lateinit var chosenImagePath : String

    fun setChosenImagePathFromIntent(intent: Intent)
    {
        if(intent.hasExtra(Tags.IMAGE_PATH))
        {
            chosenImagePath = intent.getStringExtra(Tags.IMAGE_PATH)!!
        }

    }
}