package com.stcodesapp.documentscanner.ui.imageEdit

import android.content.Intent
import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.stcodesapp.documentscanner.DocumentScannerApp
import com.stcodesapp.documentscanner.base.BaseViewModel
import com.stcodesapp.documentscanner.constants.ConstValues
import com.stcodesapp.documentscanner.constants.Tags
import com.stcodesapp.documentscanner.utils.BitmapUtil
import javax.inject.Inject


class ImagePreviewViewModel @Inject constructor(app : DocumentScannerApp) : BaseViewModel(app)
{
    lateinit var chosenImagePath : String
    private val imageBitmapLiveData = MutableLiveData<Bitmap>()
    var imageBitmap : Bitmap? = null


    fun setChosenImagePathFromIntent(intent: Intent)
    {
        if(intent.hasExtra(Tags.IMAGE_PATH))
        {
            chosenImagePath = intent.getStringExtra(Tags.IMAGE_PATH)!!
            getBitmapFromPath()
        }
    }

    fun getBitmapFromPath()
    {
        val bitmapUtil = BitmapUtil(context)
        imageBitmap = bitmapUtil.getBitmapFromPath(chosenImagePath,ConstValues.MIN_IMAGE_DIMEN,ConstValues.MIN_IMAGE_DIMEN)
        imageBitmapLiveData.postValue(imageBitmap)
    }

    fun getImageBitmapLiveData() : LiveData<Bitmap>
    {
        return imageBitmapLiveData
    }


}