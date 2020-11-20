package com.stcodesapp.documentscanner.ui.imageEdit

import android.content.Intent
import android.graphics.*
import android.os.Environment
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.stcodesapp.documentscanner.DocumentScannerApp
import com.stcodesapp.documentscanner.base.BaseViewModel
import com.stcodesapp.documentscanner.constants.ConstValues
import com.stcodesapp.documentscanner.constants.Tags
import com.stcodesapp.documentscanner.helpers.FilterHelper
import com.stcodesapp.documentscanner.models.Filter
import com.stcodesapp.documentscanner.utils.BitmapUtil
import kotlinx.coroutines.launch
import org.opencv.android.Utils
import org.opencv.core.CvType
import org.opencv.core.Mat
import org.opencv.imgproc.Imgproc
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
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

    fun detectEdges() {
        Log.e(TAG, "detectEdges: Called")
        val rgba = Mat()
        Utils.bitmapToMat(imageBitmap, rgba)
        val edges = Mat(rgba.size(), CvType.CV_8UC1)
        Imgproc.cvtColor(rgba, edges, Imgproc.COLOR_RGB2GRAY, 4)
        Imgproc.Canny(edges, edges, 80.0, 100.0)
        Log.e(TAG, "detectEdges: edges : $edges")

        // Don't do that at home or work it's for visualization purpose.
        val resultBitmap = Bitmap.createBitmap(
            edges.cols(),
            edges.rows(),
            Bitmap.Config.ARGB_8888
        )
        Utils.matToBitmap(edges, resultBitmap)
        imageBitmapLiveData.postValue(resultBitmap)
    }

    fun applyFilter(filter: Filter) {
        ioCoroutine.launch {
            imageBitmapLiveData.postValue(FilterHelper(context).applyFilter(imageBitmap!!,filter.type))
        }
    }


}