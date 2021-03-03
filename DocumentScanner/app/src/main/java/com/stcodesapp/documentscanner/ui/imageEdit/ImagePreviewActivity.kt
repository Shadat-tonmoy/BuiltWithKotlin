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
    }

    private fun initUI()
    {
        setContentView(dataBinding.root)
        dataBinding.viewModel = viewModel
        viewModel.setChosenImagePathFromIntent(intent)
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