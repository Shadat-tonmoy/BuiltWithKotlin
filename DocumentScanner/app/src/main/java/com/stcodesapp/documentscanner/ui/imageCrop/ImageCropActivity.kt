package com.stcodesapp.documentscanner.ui.imageCrop

import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.view.doOnLayout
import androidx.lifecycle.Observer
import androidx.viewpager2.widget.ViewPager2
import com.stcodesapp.documentscanner.R
import com.stcodesapp.documentscanner.base.BaseActivity
import com.stcodesapp.documentscanner.constants.Tags
import com.stcodesapp.documentscanner.database.entities.Image
import com.stcodesapp.documentscanner.helpers.getPolygonFromCropAreaJson
import com.stcodesapp.documentscanner.helpers.isValidPolygon
import com.stcodesapp.documentscanner.ui.adapters.ImageViewPagerAdapter
import com.stcodesapp.documentscanner.ui.helpers.showToast
import com.stcodesapp.documentscanner.ui.imageEdit.ImageEditItemFragment
import com.theartofdev.edmodo.cropper.CropImageView
import kotlinx.android.synthetic.main.activity_image_crop.*
import kotlinx.android.synthetic.main.image_edit_item_fragment.*
import javax.inject.Inject

class ImageCropActivity : BaseActivity()
{

    companion object{
        private const val TAG = "ImageCropActivity"
    }

    @Inject lateinit var viewModel: ImageCropViewModel
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

        viewModel.bindValueFromIntent(intent)

        initUI()

        viewPagerAdapter = ImageViewPagerAdapter(this,imageLoadListener,true)

        viewPager.adapter = viewPagerAdapter

        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback()
        {
            override fun onPageSelected(position: Int)
            {
                super.onPageSelected(position)
                imagePositionChip.text = "${position+1}/${viewPagerAdapter.itemCount}"
            }
        })

        fetchImages()
    }

    private fun initUI()
    {
        initClickListener()

        /*cropImageView.setOnSetImageUriCompleteListener { view, uri, error ->
            if(viewModel.currentImage != null) setSavedValue(viewModel.currentImage!!)
        }*/
    }

    private fun initClickListener()
    {
        rotateLeftButton.setOnClickListener { rotateLeft() }
        rotateRightButton.setOnClickListener { rotateRight() }
        cropButton.setOnClickListener { cropImage() }
        doneButton.setOnClickListener { saveAndExit() }
        resetButton.setOnClickListener { reset() }

    }

    private fun rotateRight()
    {
        showToast("Rotate Right")
    }

    private fun rotateLeft()
    {
        showToast("Rotate Left")
    }

    private fun cropImage()
    {
        showToast("Crop Image")
    }

    private fun saveAndExit()
    {
        showToast("Save and exit")
    }

    private fun reset()
    {
        showToast("Reset")
    }


    private fun setSavedValue(image: Image)
    {
        setSavedCropArea(image)

    }

    private fun setSavedCropArea(image: Image)
    {
        val savedCropArea = image.cropArea
        Log.e(TAG, "setSavedCropArea: savedCropArea : $savedCropArea")
        if (!savedCropArea.isNullOrEmpty())
        {
            val polygon = getPolygonFromCropAreaJson(savedCropArea)
            if(isValidPolygon(polygon))
            {
                cropImageView.cropPolygon = polygon
            }
        }
        cropImageView.guidelines = CropImageView.Guidelines.OFF
    }

    private fun fetchImages()
    {
        if(viewModel.imageId > 0)
        {
            fetchSingleImage()
        }
        else if(viewModel.documentId > 0) fetchDocumentPages()

    }

    private fun fetchDocumentPages()
    {
        viewModel.fetchDocumentPages().observe(this, documentPagesObserver)
    }

    private fun fetchSingleImage()
    {
        viewModel.fetchChosenImageToReCrop().observe(this, chosenImageObserver)
    }

    private fun setPagesOnUI(it: List<Image>)
    {
        viewPagerAdapter.submitList(it)
        val selectedPosition = intent.getIntExtra(Tags.IMAGE_POSITION, -1)
        if (selectedPosition > 0)
        {
            viewPager.doOnLayout {
                viewPager.setCurrentItem(selectedPosition, false)
                intent.putExtra(Tags.IMAGE_POSITION, -1)
            }
        }
    }




    /*private fun rotateCurrentImage()
    {
        val currentPosition = viewPager.currentItem
        val currentFragment = supportFragmentManager.findFragmentByTag("f$currentPosition")
        if(currentFragment != null && currentFragment is ImageEditItemFragment)
        {
            currentFragment.rotateImage()
        }
    }

    private fun cropCurrentImage()
    {
        val currentPosition = viewPager.currentItem
        val currentFragment = supportFragmentManager.findFragmentByTag("f$currentPosition")
        if(currentFragment != null && currentFragment is ImageEditItemFragment)
        {
            if(currentFragment.isImageCropped())
            {
                showToast("Will show re-crop option")
            }
            else
            {
                currentFragment.cropImage()
            }
        }
    }*/

    private val chosenImageObserver = Observer<Image?> {
        if(it != null)
        {
            setPagesOnUI(listOf(it))

        }
    }

    private val documentPagesObserver = Observer<List<Image>> {
        setPagesOnUI(it)

    }

    private val imageLoadListener = object : ImageEditItemFragment.ImageLoadListener{
        override fun onImageBitmapLoaded(imageBitmap: Bitmap)
        {
            imagePositionChip.visibility = View.VISIBLE
        }
    }

}