package com.stcodesapp.documentscanner.ui.imageCrop

import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.stcodesapp.documentscanner.DocumentScannerApp
import com.stcodesapp.documentscanner.base.BaseViewModel
import com.stcodesapp.documentscanner.constants.ConstValues.Companion.THUMB_SIZE
import com.stcodesapp.documentscanner.database.entities.Image
import com.stcodesapp.documentscanner.database.managers.DocumentManager
import com.stcodesapp.documentscanner.database.managers.ImageManager
import com.stcodesapp.documentscanner.helpers.*
import com.stcodesapp.documentscanner.models.BrightenFilter
import com.stcodesapp.documentscanner.models.Filter
import com.stcodesapp.documentscanner.models.FilterType
import com.stcodesapp.documentscanner.models.LightenFilter
import com.stcodesapp.documentscanner.scanner.getBrightenImage
import com.stcodesapp.documentscanner.scanner.getGrayscaleImage
import com.stcodesapp.documentscanner.scanner.getLightenImage
import com.theartofdev.edmodo.cropper.Polygon
import kotlinx.coroutines.launch
import javax.inject.Inject

class CropImageSingleItemViewModel @Inject constructor(val app: DocumentScannerApp) : BaseViewModel(app)
{
    companion object{
        const val BRIGHTEN_FILTER_VALUE = 15
        const val LIGHTEN_FILTER_VALUE = 1.6F
        private const val TAG = "CropImageSingleItemView"
    }

    var chosenImage : Image? = null
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

    fun applyFilterToCurrentImage(filter: Filter, srcBitmap : Bitmap) : LiveData<Bitmap>
    {
        val liveData = MutableLiveData<Bitmap>()

        val filteredBitmap = getFilteredBitmap(filter.type,srcBitmap)
        liveData.postValue(filteredBitmap)

        return liveData

    }

    fun applySavedFilter(image: Image, srcBitmap: Bitmap) : Bitmap
    {
        val filterName = image.filterName
        when(filterName)
        {
            FilterType.CUSTOM_FILTER.toString() -> {

            }
            FilterType.DEFAULT.toString() -> {


            }
            FilterType.GRAY_SCALE.toString() -> {
                getGrayscaleImage(srcBitmap,srcBitmap)
            }
            FilterType.BLACK_AND_WHITE.toString() -> {

            }
            FilterType.PAPER.toString() -> {

            }
            FilterType.POLISH.toString() -> {

            }
            FilterType.BRIGHTEN.toString() ->
            {
                val filter = Gson().fromJson(image.filterJson,BrightenFilter::class.java)
                getBrightenImage(srcBitmap,srcBitmap, filter.brightnessValue)
            }
            FilterType.LIGHTEN.toString() -> {
                val filter = Gson().fromJson(image.filterJson,LightenFilter::class.java)
                getLightenImage(srcBitmap,srcBitmap, filter.contrastValue)
            }
        }
        return srcBitmap
    }

}