package com.stcodesapp.documentscanner.ui.imageCrop

import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import androidx.core.view.doOnLayout
import androidx.lifecycle.Observer
import com.stcodesapp.documentscanner.R
import com.stcodesapp.documentscanner.base.BaseActivity
import com.stcodesapp.documentscanner.constants.Tags
import com.stcodesapp.documentscanner.database.entities.Image
import com.stcodesapp.documentscanner.scanner.getFilteredImage
import com.stcodesapp.documentscanner.scanner.getWarpedImage
import com.stcodesapp.documentscanner.ui.adapters.ImageViewPagerAdapter
import com.stcodesapp.documentscanner.ui.helpers.ActivityNavigator
import com.stcodesapp.documentscanner.ui.helpers.DialogHelper
import com.stcodesapp.documentscanner.ui.helpers.showToast
import kotlinx.android.synthetic.main.activity_image_crop.*
import kotlinx.android.synthetic.main.crop_image_single_item_fragment.*
import javax.inject.Inject

class ImageCropActivity : BaseActivity()
{
    companion object{
        private const val TAG = "ImageCropActivity"
        init {
            System.loadLibrary("native-lib")
        }
    }

    @Inject lateinit var viewModel : CropImageViewModel
    @Inject lateinit var activityNavigator: ActivityNavigator
    private lateinit var viewPagerAdapter: ImageViewPagerAdapter

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        init()
    }

    private fun init()
    {
        activityComponent.inject(this)

        setContentView(R.layout.activity_image_crop)

        initClickListener()

        viewModel.bindValueFromIntent(intent)

        viewPagerAdapter = ImageViewPagerAdapter(this)

        viewPager.adapter = viewPagerAdapter

        fetchDocumentPages()
    }

    private fun fetchDocumentPages()
    {
        viewModel.fetchDocumentPages().observe(this, documentPagesObserver)
    }

    private val documentPagesObserver = Observer<List<Image>> {
        if(it.isEmpty())
        {
            deleteDocAndExit()

        }
        else
        {
            setPagesOnUI(it)
        }

    }

    private fun deleteDocAndExit()
    {
        viewModel.deleteDoc().observe(this, Observer {
            if(it > 0) finish()
        })
    }

    private fun setPagesOnUI(it: List<Image>)
    {
        viewPagerAdapter.setDocumentPages(it)
        val selectedPosition = intent.getIntExtra(Tags.IMAGE_POSITION, -1)
        if (selectedPosition > 0)
        {
            viewPager.doOnLayout {
                viewPager.setCurrentItem(selectedPosition, false)
                intent.putExtra(Tags.IMAGE_POSITION, -1)
            }
        }
        val currentPosition = viewPager.currentItem
        if (currentPosition < viewPagerAdapter.itemCount)
        {
            val currentImage = it[currentPosition]
            setCurrentImageCropText(currentImage)
        }
    }

    private fun setCurrentImageCropText(currentImage: Image?)
    {
        if (currentImage != null)
        {
            //need not null check to work after deletion
            if (currentImage.isCropped) {
                cropButton.text = "Re-Crop"
            } else cropButton.text = "Crop"
        }
    }

    private fun initClickListener()
    {
        cropButton.setOnClickListener {
            cropCurrentImage()
        }

        rotateButton.setOnClickListener {
            rotateCurrentImage()
        }

        deleteButton.setOnClickListener {
            showDeleteImageWarning()

        }

        filterButton.setOnClickListener {
            applyMagicFilter()
        }

    }

    private fun showDeleteImageWarning()
    {
        val dialogHelper = DialogHelper(this)
        dialogHelper.showWarningDialog(getString(R.string.image_delete_warning_msg)) {onImageDeleteConfirmed()}
    }

    private fun onImageDeleteConfirmed()
    {
        val chosenImagePosition = viewPager.currentItem
        if(chosenImagePosition >= 0)
        {
            val imageAtPosition = viewPagerAdapter.getDocumentPageAt(chosenImagePosition)
            if(imageAtPosition != null)
            {
                viewModel.deleteImage(imageAtPosition).observe(this, Observer {
                    if(it != null && it > 0)
                    {
                        showToast("Image is removed!")
                    }
                })
            }
        }
    }

    private fun rotateCurrentImage()
    {
        val currentPosition = viewPager.currentItem
        val currentFragment = supportFragmentManager.findFragmentByTag("f$currentPosition")
        if(currentFragment != null && currentFragment is CropImageSingleItemFragment)
        {
            currentFragment.rotateImage()
        }
    }

    private fun cropCurrentImage()
    {
        val currentPosition = viewPager.currentItem
        val currentFragment = supportFragmentManager.findFragmentByTag("f$currentPosition")
        if(currentFragment != null && currentFragment is CropImageSingleItemFragment)
        {
            currentFragment.cropImage()
        }
    }

    private fun applyMagicFilter()
    {
        val currentPosition = viewPager.currentItem
        val currentFragment = supportFragmentManager.findFragmentByTag("f$currentPosition")
        if(currentFragment != null && currentFragment is CropImageSingleItemFragment)
        {
            val srcBitmap = currentFragment.cropImageView.bitmap
            val dstBitmap = srcBitmap.copy(srcBitmap.config,true)
            getFilteredImage(srcBitmap, dstBitmap)
            currentFragment.cropImageView.setImageBitmap(dstBitmap,false)
            cropImageView.isShowCropOverlay = false

        }
    }

}