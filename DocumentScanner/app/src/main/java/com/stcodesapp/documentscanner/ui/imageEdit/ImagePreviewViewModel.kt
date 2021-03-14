package com.stcodesapp.documentscanner.ui.imageEdit

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Matrix
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.stcodesapp.documentscanner.DocumentScannerApp
import com.stcodesapp.documentscanner.base.BaseViewModel
import com.stcodesapp.documentscanner.constants.ConstValues
import com.stcodesapp.documentscanner.constants.Tags
import com.stcodesapp.documentscanner.database.managers.ImageManager
import com.stcodesapp.documentscanner.helpers.FilterHelper
import com.stcodesapp.documentscanner.models.CropArea
import com.stcodesapp.documentscanner.models.Filter
import com.stcodesapp.documentscanner.utils.BitmapUtil
import kotlinx.coroutines.launch
import org.opencv.android.Utils
import org.opencv.core.CvType
import org.opencv.core.Mat
import org.opencv.imgproc.Imgproc
import javax.inject.Inject


class ImagePreviewViewModel @Inject constructor(app : DocumentScannerApp) : BaseViewModel(app)
{
    lateinit var chosenImagePath : String
    private var chosenImageId : Long = -1L
    private val imageBitmapLiveData = MutableLiveData<Bitmap>()
    private var rotationAngle = 0f
    var isCropped = false
    var imageBitmap : Bitmap? = null
    var originalBitmap : Bitmap? = null
    @Inject lateinit var imageManager: ImageManager

    companion object{
        private const val TAG = "ImagePreviewViewModel"
    }


    fun setChosenImagePathFromIntent(intent: Intent)
    {
        if(intent.hasExtra(Tags.IMAGE_PATH))
        {
            chosenImagePath = intent.getStringExtra(Tags.IMAGE_PATH)!!
            chosenImageId = intent.getLongExtra(Tags.IMAGE_ID,-1)
            getBitmapFromPath()
            applySavedFilter()
        }
    }

    private fun getBitmapFromPath()
    {
        val bitmapUtil = BitmapUtil(context)
        imageBitmap = bitmapUtil.getBitmapFromPath(chosenImagePath,ConstValues.MIN_IMAGE_DIMEN,ConstValues.MIN_IMAGE_DIMEN)
        originalBitmap = imageBitmap?.copy(Bitmap.Config.ARGB_8888, true)
        imageBitmapLiveData.postValue(imageBitmap)
    }

    fun getImageBitmapLiveData() : LiveData<Bitmap>
    {
        return imageBitmapLiveData
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
            if(filter.type != null)
            {
                if(!isSameFilterAlreadyApplied(filter))
                {
                    imageBitmap = FilterHelper(context).applyFilter(originalBitmap!!, filter)
                    updateFilterNameInDB(filter)
                }
                imageBitmapLiveData.postValue(imageBitmap)
            }
            else {
                updateFilterNameInDB(filter)
                imageBitmapLiveData.postValue(originalBitmap)
            }
        }
    }

    fun applySavedFilter()
    {
        ioCoroutine.launch {
            applyFilterFromDB()
        }
    }

    private suspend fun applyFilterFromDB()
    {
        val image = imageManager.getImageById(chosenImageId)
        if(image != null)
        {
            val filterName = image.filterName
            if(filterName.isNotEmpty())
            {
                imageBitmap = FilterHelper(context).applyFilterByName(originalBitmap!!, filterName)
                imageBitmapLiveData.postValue(imageBitmap)
            }

        }
    }

    private suspend fun updateFilterNameInDB(filter: Filter)
    {
        val image = imageManager.getImageById(chosenImageId)
        if (image != null) {
            imageManager.updateImage(image.apply { filterName = filter.title })
        }
    }

    private suspend fun isSameFilterAlreadyApplied(filter: Filter) : Boolean
    {
        val image = imageManager.getImageById(chosenImageId)
        if (image != null) {
            return image.filterName == filter.title
        }
        return false
    }

    private suspend fun updateCropAreaInDB(area: CropArea)
    {
        val image = imageManager.getImageById(chosenImageId)
        if (image != null)
        {
            val cropAreaJson = Gson().toJson(area,CropArea::class.java)
            imageManager.updateImage(image.apply { cropArea = cropAreaJson })
        }
    }

    fun saveCropArea(cropArea: CropArea) {
        ioCoroutine.launch {
            if(isCropped) { cropArea.empty() }
            updateCropAreaInDB(cropArea)
            isCropped = !isCropped
        }


    }

    fun rotateBitmap(angle: Float) : Float{
        rotationAngle += angle
        if (rotationAngle > 360) rotationAngle = 0f
        return rotationAngle
    }

    fun deleteImage() : LiveData<Int>
    {
        val liveData = MutableLiveData<Int>()
        ioCoroutine.launch {
            val deletedRows = imageManager.deleteImageById(chosenImageId)
            liveData.postValue(deletedRows)
        }
        return liveData


    }

}