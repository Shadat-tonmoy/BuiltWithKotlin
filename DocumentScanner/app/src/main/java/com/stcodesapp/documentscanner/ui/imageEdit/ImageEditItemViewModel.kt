package com.stcodesapp.documentscanner.ui.imageEdit

import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.stcodesapp.documentscanner.DocumentScannerApp
import com.stcodesapp.documentscanner.base.BaseViewModel
import com.stcodesapp.documentscanner.constants.ConstValues
import com.stcodesapp.documentscanner.constants.ConstValues.Companion.MIN_IMAGE_DIMEN
import com.stcodesapp.documentscanner.constants.ConstValues.Companion.THUMB_SIZE
import com.stcodesapp.documentscanner.database.entities.Image
import com.stcodesapp.documentscanner.database.managers.DocumentManager
import com.stcodesapp.documentscanner.database.managers.ImageManager
import com.stcodesapp.documentscanner.helpers.*
import com.stcodesapp.documentscanner.models.*
import com.stcodesapp.documentscanner.scanner.*
import com.theartofdev.edmodo.cropper.Polygon
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

class ImageEditItemViewModel @Inject constructor(val app: DocumentScannerApp) : BaseViewModel(app)
{
    companion object{
        const val BRIGHTEN_FILTER_VALUE = 15
        const val LIGHTEN_FILTER_VALUE = 1.6F
        private const val TAG = "CropImageSingleItemView"
    }

    var chosenImage : Image? = null
    var chosenImageId : Long = -1
    var chosenImagePosition : Int = -1

    @Inject lateinit var imageManager: ImageManager
    @Inject lateinit var documentManager: DocumentManager
    @Inject lateinit var imageHelper: ImageHelper
    @Inject lateinit var fileHelper: FileHelper
    @Inject lateinit var filterHelper: FilterHelper

    fun deleteImage() : LiveData<Int>
    {
        val liveData = MutableLiveData<Int>()
        ioCoroutine.launch {
            if(chosenImage != null)
            {
                val deletedRows = imageManager.deleteImageById(chosenImage!!.id)
                val document = documentManager.getDocumentById(chosenImage!!.docId)
                if(document != null)
                {
                    val allImagesOfDocument = imageManager.getDocumentPagesValue(document.id)
                    val lastImage = allImagesOfDocument.maxBy { it.id }
                    if(lastImage != null)
                    {
                        document.apply {
                            thumbPath = lastImage.path
                        }
                        documentManager.updateDocument(document)
                    }
                }
                liveData.postValue(deletedRows)
            }
            else liveData.postValue(-1)

        }
        return liveData
    }

    fun updateImageCropPolygon(cropPolygon: Polygon) : LiveData<Long>
    {
        val liveData = MutableLiveData<Long>()
        ioCoroutine.launch {
            if(chosenImage != null)
            {
                val cropAreaJson = getCropAreaJsonFromPolygon(cropPolygon)
                chosenImage!!.apply { cropArea = cropAreaJson }
                val updatedRow = imageManager.updateImage(chosenImage!!)
                liveData.postValue(updatedRow)
            }
            else liveData.postValue(-1)
        }
        return liveData
    }

    fun updateImageRotationAngle(angle: Double) : LiveData<Long>
    {
        val liveData = MutableLiveData<Long>()
        ioCoroutine.launch {
            if(chosenImage != null)
            {
                chosenImage!!.apply { rotationAngle = angle }
                val updatedRow = imageManager.updateImage(chosenImage!!)
                liveData.postValue(updatedRow)
            }
            else liveData.postValue(-1)
        }
        return liveData
    }

    fun updateImageCroppedFlag(flag: Boolean) : LiveData<Long>
    {
        val liveData = MutableLiveData<Long>()
        ioCoroutine.launch {
            if(chosenImage != null)
            {
                chosenImage!!.apply { isCropped = flag }
                val updatedRow = imageManager.updateImage(chosenImage!!)
                liveData.postValue(updatedRow)
            }
            else liveData.postValue(-1)
        }
        return liveData
    }

    fun saveImageCropData(imageBitmap : Bitmap)
    {
        ioCoroutine.launch {
            if(chosenImage != null)
            {
                val document = documentManager.getDocumentById(chosenImage!!.docId)
                if(document != null)
                {
                    val documentPath = document.path
                    val filterList = filterHelper.getFilterList(chosenImage!!.path)
                    for(filter in filterList)
                    {
                        saveThumbWithFilter(filter, documentPath, imageBitmap)
                    }
                    //setImageCropFlag(true)
                }
            }
        }
    }

    private fun saveThumbWithFilter(filter: Filter, documentPath: String, imageBitmap: Bitmap)
    {
        val thumbFileName = filterHelper.getFilteredThumbFileName(chosenImage!!.path, filter.type)
        val thumbFile = fileHelper.getThumbFile(documentPath, thumbFileName)
        val thumbBitmap = imageHelper.getResizedBitmapByThreshold(imageBitmap, THUMB_SIZE)
        val thumbFilePath = thumbFile.absolutePath
        if (thumbBitmap != null)
        {
            val filteredBitmap = getFilteredBitmap(filter.type, thumbBitmap)
            imageHelper.saveBitmapInFile(filteredBitmap, thumbFilePath, quality = 100)
        }
    }

    private suspend fun setImageCropFlag(flag: Boolean)
    {
        if(chosenImage != null)
        {
            chosenImage?.apply { isCropped = flag }
            imageManager.updateImage(chosenImage!!)
        }
    }

    private fun getFilteredBitmap(filterType: FilterType, srcBitmap : Bitmap) : Bitmap
    {
        return filterHelper.getFilteredBitmap(filterType,srcBitmap)
    }

    fun applyFilterToCurrentImage(filter: Filter) : LiveData<Bitmap>
    {
        val liveData = MutableLiveData<Bitmap>()

        if(chosenImage != null)
        {
            val imagePath = chosenImage!!.path
            var srcBitmap = imageHelper.decodeBitmapFromUri(Uri.fromFile(File(imagePath)),MIN_IMAGE_DIMEN,MIN_IMAGE_DIMEN)
            if(srcBitmap != null)
            {
                if(!chosenImage!!.cropArea.isNullOrEmpty())
                {
                    Log.e(TAG, "getCropPolygonByRation: bitmapW : " + srcBitmap.getWidth() + " bitmapH : " + srcBitmap.getHeight()
                    )
                    val cropPolygon = getPolygonFromCropAreaJson(chosenImage!!.cropArea!!)
                    val dstBitmap = Bitmap.createBitmap(ConstValues.A4_PAPER_WIDTH, ConstValues.A4_PAPER_HEIGHT, Bitmap.Config.ARGB_8888)
                    getWarpedImage(srcBitmap, dstBitmap,cropPolygon)
                    srcBitmap = dstBitmap
                }
                val filteredBitmap = getFilteredBitmap(filter.type,srcBitmap!!)
                liveData.postValue(filteredBitmap)
            }
        }
        return liveData

    }

    fun applySavedFilter(image: Image, srcBitmap: Bitmap, dstBitmap: Bitmap) : Bitmap
    {
        val filterName = image.filterName
        val filterType = getFilterTypeFromName(filterName)
        when(filterType)
        {
            FilterType.CUSTOM_FILTER -> {

            }
            FilterType.DEFAULT -> {


            }
            FilterType.GRAY_SCALE -> {
                getGrayscaleImage(srcBitmap,dstBitmap)
            }
            FilterType.BLACK_AND_WHITE -> {

            }
            FilterType.PAPER -> {

            }
            FilterType.POLISH -> {

            }
            FilterType.BRIGHTEN ->
            {
                val filter = Gson().fromJson(image.filterJson,BrightenFilter::class.java)
                getBrightenImage(srcBitmap,dstBitmap, filter.brightnessValue)
            }
            FilterType.LIGHTEN -> {
                val filter = Gson().fromJson(image.filterJson,LightenFilter::class.java)
                getLightenImage(srcBitmap,dstBitmap, filter.contrastValue)
            }
        }
        return srcBitmap
    }

    fun applyBrightnessAndContrast(brightnessValue: Int, contrastValue: Float, srcBitmap: Bitmap?) : LiveData<Bitmap>
    {
        val liveData = MutableLiveData<Bitmap>()
        if(srcBitmap != null && chosenImage != null)
        {
            ioCoroutine.launch {
                val dstBitmap = srcBitmap.copy(srcBitmap.config, true)
                val imageId = chosenImage!!.id
                chosenImage = imageManager.getImageById(imageId)
                if(chosenImage != null)
                {
                    FilterHelper(context).applyFilterByName(dstBitmap,chosenImage!!)
                }
                getCustomBrightnessAndContrastImage(dstBitmap, dstBitmap, brightnessValue, contrastValue)
                liveData.postValue(dstBitmap)
            }
        }
        return liveData


    }

}