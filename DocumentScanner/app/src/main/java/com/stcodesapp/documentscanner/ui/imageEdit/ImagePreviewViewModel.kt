package com.stcodesapp.documentscanner.ui.imageEdit

import android.content.Intent
import com.stcodesapp.documentscanner.DocumentScannerApp
import com.stcodesapp.documentscanner.base.BaseViewModel
import com.stcodesapp.documentscanner.constants.Tags
import javax.inject.Inject

class ImagePreviewViewModel @Inject constructor(app : DocumentScannerApp) : BaseViewModel(app)
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