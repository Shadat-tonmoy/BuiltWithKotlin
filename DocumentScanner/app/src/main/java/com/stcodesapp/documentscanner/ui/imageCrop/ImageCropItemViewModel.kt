package com.stcodesapp.documentscanner.ui.imageCrop

import android.os.Bundle
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.stcodesapp.documentscanner.DocumentScannerApp
import com.stcodesapp.documentscanner.base.BaseViewModel
import com.stcodesapp.documentscanner.constants.Tags
import com.stcodesapp.documentscanner.database.entities.Image
import com.stcodesapp.documentscanner.helpers.getCropAreaJsonFromPolygon
import com.theartofdev.edmodo.cropper.Polygon
import kotlinx.coroutines.launch
import javax.inject.Inject

class ImageCropItemViewModel @Inject constructor(val app: DocumentScannerApp) : BaseViewModel(app)
{

    var chosenImage : Image? = null
    var chosenImageId : Long = -1
    var chosenImagePosition : Int = -1
    var showOriginalImage : Boolean = false

    fun bindValueFromArgument(arguments : Bundle?)
    {
        if(arguments != null)
        {
            val serializedImage = arguments.getSerializable(Tags.SERIALIZED_IMAGE) as Image?
            if(serializedImage != null)
            {
                chosenImage = serializedImage
                chosenImageId = serializedImage.id
            }
            chosenImagePosition = arguments.getInt(Tags.IMAGE_POSITION)
            showOriginalImage = arguments.getBoolean(Tags.SHOW_ORIGINAL_IMAGE,false)
        }

    }

    fun updateImageCropPolygon(cropPolygonByRatio: Polygon, originalCropPolygon : Polygon) : LiveData<Long>
    {
        val liveData = MutableLiveData<Long>()
        ioCoroutine.launch {
            if(chosenImage != null)
            {
                val cropAreaByRatioJson = getCropAreaJsonFromPolygon(cropPolygonByRatio)
                val originalCropAreaJson = getCropAreaJsonFromPolygon(originalCropPolygon)
                chosenImage!!.apply {
                    cropArea = cropAreaByRatioJson
                    originalCropArea = originalCropAreaJson
                }
                val updatedRow = imageManager.updateImage(chosenImage!!)
                liveData.postValue(updatedRow)
            }
            else liveData.postValue(-1)
        }
        return liveData
    }
}