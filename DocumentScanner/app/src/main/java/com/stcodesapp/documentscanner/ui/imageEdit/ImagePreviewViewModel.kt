package com.stcodesapp.documentscanner.ui.imageEdit

import android.content.Intent
import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.stcodesapp.documentscanner.DocumentScannerApp
import com.stcodesapp.documentscanner.base.BaseViewModel
import com.stcodesapp.documentscanner.constants.ConstValues
import com.stcodesapp.documentscanner.constants.Tags
import com.stcodesapp.documentscanner.utils.BitmapUtil
import org.opencv.android.Utils
import org.opencv.core.CvType
import org.opencv.core.Mat
import org.opencv.imgproc.Imgproc
import javax.inject.Inject


class ImagePreviewViewModel @Inject constructor(app : DocumentScannerApp) : BaseViewModel(app)
{
    lateinit var chosenImagePath : String
    private val imageBitmapLiveData = MutableLiveData<Bitmap>()
    var imageBitmap : Bitmap? = null

    companion object{
        private const val TAG = "ImagePreviewViewModel"
    }


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

    fun toGrayScale()
    {
        if(imageBitmap != null)
        {
            Log.e(TAG, "toGrayScale: Called")
            val tmp = Mat(imageBitmap!!.width, imageBitmap!!.height, CvType.CV_8UC1)
            Utils.bitmapToMat(imageBitmap!!, tmp)
            Imgproc.cvtColor(tmp, tmp, Imgproc.COLOR_RGB2GRAY)
            imageBitmapLiveData.postValue(imageBitmap)
            Utils.matToBitmap(tmp, imageBitmap)
        }

    }


}