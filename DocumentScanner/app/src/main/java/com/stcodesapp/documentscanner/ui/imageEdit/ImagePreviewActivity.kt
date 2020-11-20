package com.stcodesapp.documentscanner.ui.imageEdit

import android.graphics.Bitmap
import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.Toast
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.labters.documentscanner.base.CropperErrorType
import com.labters.documentscanner.base.DocumentScanActivity
import com.labters.documentscanner.helpers.ScannerConstants
import com.labters.documentscanner.libraries.PolygonView
import com.stcodesapp.documentscanner.DocumentScannerApp
import com.stcodesapp.documentscanner.databinding.ImagePreviewLayoutBinding
import com.stcodesapp.documentscanner.di.activity.ActivityComponent
import com.stcodesapp.documentscanner.di.activity.modules.ActivityModule
import com.stcodesapp.documentscanner.models.Filter
import com.stcodesapp.documentscanner.ui.helpers.FragmentFrameWrapper
import com.tigerit.pothghat.di.application.ApplicationComponent
import kotlinx.android.synthetic.main.image_preview_layout.*
import javax.inject.Inject


class ImagePreviewActivity : DocumentScanActivity(), FragmentFrameWrapper {


    @Inject lateinit var viewModel: ImagePreviewViewModel
    @Inject lateinit var dataBinding : ImagePreviewLayoutBinding

    private val appComponent: ApplicationComponent by lazy {
        (application as DocumentScannerApp).appComponent
    }

    private val activityComponent: ActivityComponent by lazy {
        appComponent.getActivityComponent(ActivityModule(this))
    }

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
        startCropping()


    }

    private fun initUI()
    {
        setContentView(dataBinding.root)
        dataBinding.viewModel = viewModel
        viewModel.setChosenImagePathFromIntent(intent)
        cropButton.setOnClickListener { showCroppedImage() }
    }

    private fun observeImageBitmap()
    {
        viewModel.getImageBitmapLiveData().observe(this, Observer {
            if(it != null)
            {
//                initializeImage()
                Glide.with(this)
                    .load(scaledBitmap(it, holderImageCrop?.width!!, holderImageCrop?.height!!))
                    .into(dataBinding.imageView)
            }
        })
    }

    private fun showCroppedImage()
    {
        Glide.with(this)
            .load(croppedImage)
            .into(dataBinding.imageView)
        polygonView?.visibility = View.GONE
    }

    fun onFilterClicked(filter : Filter)
    {
        viewModel.applyFilter(filter)
    }

    override fun getHolderImageCrop(): FrameLayout? { return dataBinding.holderImageCrop }

    override fun getImageView(): ImageView? { return dataBinding.imageView }

    override fun getPolygonView(): PolygonView? { return dataBinding.polygonView }

    override fun getBitmapImage(): Bitmap? { return viewModel.imageBitmap }

    override fun showProgressBar() { dataBinding.progressBar.visibility = View.VISIBLE }

    override fun hideProgressBar() { dataBinding.progressBar.visibility = View.GONE }

    override fun showError(errorType: CropperErrorType?) {
        when (errorType) {
            CropperErrorType.CROP_ERROR -> Toast.makeText(
                this,
                ScannerConstants.cropError,
                Toast.LENGTH_LONG
            ).show()
        }
    }

    override fun getFragmentFrame(): FrameLayout? {
        return filterOptionContainer
    }
}