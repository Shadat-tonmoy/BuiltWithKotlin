package com.stcodesapp.documentscanner.ui.imageEdit

import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
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
import com.stcodesapp.documentscanner.models.CropArea
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
        cropButton.setOnClickListener { viewModel.saveCropArea(getCroppingArea())
            showCroppedImage() }

        rotateButton.setOnClickListener { frameLayout.rotation = viewModel.rotateBitmap(90.0f) }
    }

    private fun observeImageBitmap()
    {
        viewModel.getImageBitmapLiveData().observe(this, Observer {
            if(it != null)
            {
                Glide.with(this)
                    .load(it)
                    .into(dataBinding.imageView)
            }
        })
    }

    private fun showCroppedImage()
    {
        Glide.with(this)
            .load(if(viewModel.isCropped) croppedImage else viewModel.originalBitmap)
            .into(dataBinding.imageView)
        polygonView?.visibility = if(viewModel.isCropped) View.GONE else View.VISIBLE
    }

    private fun getCroppingArea() : CropArea
    {
        val p = getCropArea()
        return CropArea(p.x1, p.y1, p.x2, p.y2, p.x3, p.y3, p.x4, p.y4)
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

    override fun onInitialCroppingDone() {
        viewModel.applySavedFilter()
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