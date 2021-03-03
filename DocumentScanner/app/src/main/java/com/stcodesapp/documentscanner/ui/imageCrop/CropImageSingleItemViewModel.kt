package com.stcodesapp.documentscanner.ui.imageCrop

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.stcodesapp.documentscanner.DocumentScannerApp
import com.stcodesapp.documentscanner.base.BaseViewModel
import com.stcodesapp.documentscanner.database.entities.Image
import com.stcodesapp.documentscanner.database.managers.DocumentManager
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
    @Inject lateinit var documentManager: DocumentManager

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

}