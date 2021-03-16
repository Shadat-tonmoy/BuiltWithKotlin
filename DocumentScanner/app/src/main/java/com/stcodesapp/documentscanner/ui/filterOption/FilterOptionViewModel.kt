package com.stcodesapp.documentscanner.ui.filterOption

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.stcodesapp.documentscanner.DocumentScannerApp
import com.stcodesapp.documentscanner.base.BaseViewModel
import com.stcodesapp.documentscanner.database.entities.Image
import com.stcodesapp.documentscanner.database.managers.DocumentManager
import com.stcodesapp.documentscanner.database.managers.ImageManager
import com.stcodesapp.documentscanner.helpers.FileHelper
import com.stcodesapp.documentscanner.helpers.FilterHelper
import com.stcodesapp.documentscanner.helpers.ImageHelper
import com.stcodesapp.documentscanner.models.Filter
import com.stcodesapp.documentscanner.ui.imageCrop.CropImageSingleItemViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

class FilterOptionViewModel  @Inject constructor(val app: DocumentScannerApp) : BaseViewModel(app)
{

    companion object{
        private const val TAG = "FilterOptionViewModel"
    }

    @Inject lateinit var imageManager: ImageManager
    @Inject lateinit var documentManager: DocumentManager
    @Inject lateinit var imageHelper: ImageHelper
    @Inject lateinit var fileHelper: FileHelper
    @Inject lateinit var filterHelper: FilterHelper



    var chosenImage : Image? = null
    var chosenImagePosition : Int = -1

    fun getFilters() : LiveData<List<Filter>>
    {
        val liveData = MutableLiveData<List<Filter>>()
        val filteredThumbList = mutableListOf<Filter>()
        ioCoroutine.launch {
            if(chosenImage != null)
            {
                val document = documentManager.getDocumentById(chosenImage!!.docId)
                if(document != null)
                {
                    val documentPath = document.path
                    val imagePath = chosenImage!!.path
                    val filterList = filterHelper.getFilterList(imagePath)
                    for(filter in filterList)
                    {
                        val thumbFileName = filterHelper.getFilteredThumbFileName(imagePath,filter.type)
                        Log.e(TAG, "saveImageThumbnail: thumbFileName : $thumbFileName")
                        val thumbFile = fileHelper.getThumbFile(documentPath,thumbFileName)
                        Log.e(TAG, "saveImageThumbnail: thumbFile : $thumbFile")
                        val thumbFilePath = thumbFile.absolutePath
                        filteredThumbList.add(filter.apply { imagepath = thumbFilePath })
                    }
                    liveData.postValue(filteredThumbList)
                }

            }

        }

        return liveData
    }
}