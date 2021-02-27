package com.stcodesapp.documentscanner.ui.imageCrop

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.stcodesapp.documentscanner.DocumentScannerApp
import com.stcodesapp.documentscanner.base.BaseViewModel
import com.stcodesapp.documentscanner.database.entities.Image
import com.stcodesapp.documentscanner.database.managers.ImageManager
import com.stcodesapp.documentscanner.helpers.getCropAreaJsonFromPolygon
import com.theartofdev.edmodo.cropper.Polygon
import kotlinx.coroutines.launch
import javax.inject.Inject

class CropImageSingleItemViewModel @Inject constructor(val app: DocumentScannerApp) : BaseViewModel(app)
{

    var chosenImage : Image? = null
    var chosenImagePosition : Int = -1

    @Inject lateinit var imageManager: ImageManager

    fun deleteImage() : LiveData<Int>
    {
        val liveData = MutableLiveData<Int>()
        ioCoroutine.launch {
            if(chosenImage != null)
            {
                val deletedRows = imageManager.deleteImageById(chosenImage!!.id)
                liveData.postValue(deletedRows)
            }
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

}