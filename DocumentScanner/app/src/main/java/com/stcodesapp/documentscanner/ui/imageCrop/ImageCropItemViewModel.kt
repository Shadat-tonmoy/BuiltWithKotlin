package com.stcodesapp.documentscanner.ui.imageCrop

import android.os.Bundle
import androidx.lifecycle.ViewModel
import com.stcodesapp.documentscanner.DocumentScannerApp
import com.stcodesapp.documentscanner.base.BaseViewModel
import com.stcodesapp.documentscanner.constants.Tags
import com.stcodesapp.documentscanner.database.entities.Image
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
}