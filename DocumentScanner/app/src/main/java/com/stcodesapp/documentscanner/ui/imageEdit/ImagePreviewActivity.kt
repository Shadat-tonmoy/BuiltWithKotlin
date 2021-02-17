package com.stcodesapp.documentscanner.ui.imageEdit

import android.os.Bundle
import android.util.Log
import android.widget.FrameLayout
import androidx.lifecycle.Observer
import com.stcodesapp.documentscanner.R
import com.stcodesapp.documentscanner.base.BaseActivity
import com.stcodesapp.documentscanner.databinding.ImagePreviewLayoutBinding
import com.stcodesapp.documentscanner.models.Filter
import com.stcodesapp.documentscanner.ui.helpers.DialogHelper
import com.stcodesapp.documentscanner.ui.helpers.FragmentFrameWrapper
import com.theartofdev.edmodo.cropper.CropImageView
import kotlinx.android.synthetic.main.image_preview_layout.*
import javax.inject.Inject


class ImagePreviewActivity : BaseActivity(), FragmentFrameWrapper {


    @Inject lateinit var viewModel: ImagePreviewViewModel
    @Inject lateinit var dataBinding : ImagePreviewLayoutBinding
    private var lastWidth = 0
    private var lastHeight = 0

    companion object{
        private const val TAG = "ImagePreviewActivity"
        init {
            System.loadLibrary("NativeImageProcessor")
        }
    }


    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        activityComponent.inject(this)
        initUI()
        observeImageBitmap()
    }

    private fun initUI()
    {
        setContentView(dataBinding.root)
        dataBinding.viewModel = viewModel
        viewModel.setChosenImagePathFromIntent(intent)
        cropButton.setOnClickListener {
            cropImageView.setImageBitmap(cropImageView.croppedBitmapByPolygon)
            cropImageView.printCropPolygon()
            cropImageView.isShowCropOverlay = false
        }

        rotateButton.setOnClickListener {
            cropImageView.rotateImage(90)


        }
        deleteButton.setOnClickListener { showDeleteImageWarning() }
    }

    private fun observeImageBitmap()
    {
        viewModel.getImageBitmapLiveData().observe(this, Observer {
            if(it != null)
            {
                cropImageView.setImageBitmap(it)
                cropImageView.guidelines = CropImageView.Guidelines.OFF
                val pointMaps = cropImageView.getEdgePoints(it)
                Log.e(TAG, "observeImageBitmap: pointMaps : $pointMaps")
            }
        })
    }

    private fun showDeleteImageWarning()
    {
        val dialogHelper = DialogHelper(this)
        dialogHelper.showWarningDialog(getString(R.string.image_delete_warning_msg)) {onImageDeleteConfirmed()}
    }

    private fun onImageDeleteConfirmed()
    {
        viewModel.deleteImage().observe(this, Observer {
            if(it != null && it > 0) finish()
        })

    }



    fun onFilterClicked(filter : Filter)
    {
        viewModel.applyFilter(filter)
    }

    fun onFilterMenuLoad()
    {
        //will scale down image view
    }

    fun onFilterMenuClosed()
    {
        //will scale up image view
    }


    override fun getFragmentFrame(): FrameLayout? {
        return filterOptionContainer
    }
}