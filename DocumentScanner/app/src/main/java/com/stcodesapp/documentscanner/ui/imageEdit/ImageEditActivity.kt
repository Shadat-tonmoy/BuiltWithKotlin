package com.stcodesapp.documentscanner.ui.imageEdit

import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import androidx.core.view.doOnLayout
import androidx.lifecycle.Observer
import androidx.viewpager2.widget.ViewPager2
import com.stcodesapp.documentscanner.R
import com.stcodesapp.documentscanner.base.BaseActivity
import com.stcodesapp.documentscanner.constants.Tags
import com.stcodesapp.documentscanner.database.entities.Image
import com.stcodesapp.documentscanner.models.CustomFilter
import com.stcodesapp.documentscanner.models.Filter
import com.stcodesapp.documentscanner.models.PaperEffectFilter
import com.stcodesapp.documentscanner.ui.adapters.ImageViewPagerAdapter
import com.stcodesapp.documentscanner.ui.filterOption.FilterOptionFragment
import com.stcodesapp.documentscanner.ui.helpers.*
import com.stcodesapp.documentscanner.ui.imageEffect.ImageEffectFragment
import com.stcodesapp.documentscanner.ui.paperEffect.PaperEffectFragment
import kotlinx.android.synthetic.main.activity_image_edit.*
import javax.inject.Inject

class ImageEditActivity : BaseActivity(), FragmentFrameWrapper
{
    companion object{
        private const val TAG = "ImageCropActivity"
        init {
            System.loadLibrary("native-lib")
        }
    }

    @Inject lateinit var viewModel : ImageEditViewModel
    @Inject lateinit var activityNavigator: ActivityNavigator
    @Inject lateinit var fragmentNavigator: FragmentNavigator
    private lateinit var viewPagerAdapter: ImageViewPagerAdapter

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        init()
    }

    private fun init()
    {
        activityComponent.inject(this)

        setContentView(R.layout.activity_image_edit)

        initClickListener()

        viewModel.bindValueFromIntent(intent)

        viewPagerAdapter = ImageViewPagerAdapter(this,imageLoadListener)

        viewPager.adapter = viewPagerAdapter

        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback()
        {
            override fun onPageSelected(position: Int)
            {
                super.onPageSelected(position)
                updateCurrentImage()
                imagePositionChip.text = "${position+1}/${viewPagerAdapter.itemCount}"
            }
        })
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

    private fun initClickListener()
    {
        /*cropButton.setOnClickListener {
            cropCurrentImage()
        }

        rotateButton.setOnClickListener {
            rotateCurrentImage()
        }*/

        deleteButton.setOnClickListener {
            showDeleteImageWarning()
        }

        filterButton.setOnClickListener {
            showFilterFragment()
        }

        paperEffectButton.setOnClickListener {
            showPaperEffectFragment()
        }

        imageEnrichButton.setOnClickListener {
            showImageEffectFragment()
        }

        reCropButton.setOnClickListener {
            openImageCropActivity()
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

    private fun applyPaperEffect(blockSize: Int, c : Double)
    {
        val currentFragment = getCurrentFragment()
        val currentImage = getCurrentImage()
        // should not use viewModel.originalImageBitmap. As it is always null now
        if(currentFragment != null && currentImage != null && viewModel.originalImageBitmap != null )
        {
            currentFragment.applyPaperEffect(blockSize,c,viewModel.originalImageBitmap!!)
            viewModel.savePaperEffectFilterInfo(currentImage, PaperEffectFilter(blockSize,c))
        }
    }

    private fun setBrightnessOfCurrentImage(brightnessValue : Int, contrastValue : Float)
    {
        val currentPosition = viewPager.currentItem
        val currentFragment = supportFragmentManager.findFragmentByTag("f$currentPosition")
        if(currentFragment != null && currentFragment is ImageEditItemFragment)
        {
            if(viewModel.originalImageBitmap != null)
            {
                currentFragment.applyBrightnessAndContrast(brightnessValue,contrastValue,viewModel.originalImageBitmap)
                val currentImage = viewPagerAdapter.getDocumentPageAt(currentPosition)
                if(currentImage != null)
                {
                    viewModel.saveCustomImageFilterInfo(currentImage, CustomFilter(brightnessValue,contrastValue))
                }
            }
        }
    }

    private fun showPaperEffectFragment()
    {
        fragmentNavigator.loadPaperEffectFragment(paperEffectListener)
    }

    private fun showImageEffectFragment()
    {
        fragmentNavigator.loadImageEffectFragment(imageEffectListener)
    }

    private fun showFilterFragment()
    {
        val chosenImagePosition = viewPager.currentItem
        if(chosenImagePosition >= 0)
        {
            val imageAtPosition = viewPagerAdapter.getDocumentPageAt(chosenImagePosition)
            if(imageAtPosition != null)
            {
                fragmentNavigator.loadFilterFragment(imageAtPosition,chosenImagePosition,imageFilterListener)
            }
        }
    }

    private fun applyFilterToCurrentImage(filter: Filter)
    {
        val currentFragment = getCurrentFragment()
        if(currentFragment != null)
        {
            currentFragment.applyFilter(filter)
            val currentImage = getCurrentImage()
            if(currentImage != null)
            {
                viewModel.saveCurrentImageFilterInfo(currentImage, filter)
            }
        }
    }

    private fun openImageCropActivity()
    {
        val currentImage = getCurrentImage()
        if(currentImage != null)
        {
            activityNavigator.openImageReCropScreen(currentImage.id, currentImage.docId)
        }
    }

    private fun isCurrentImageCropped() : Boolean
    {
        val currentImage = getCurrentImage()
        return currentImage?.isCropped == true
    }

    private fun getCurrentImage() : Image?
    {
        val currentPosition = viewPager.currentItem
        return viewPagerAdapter.getDocumentPageAt(currentPosition)
    }

    private fun getCurrentFragment() : ImageEditItemFragment?
    {
        val currentPosition = viewPager.currentItem
        val currentFragment = supportFragmentManager.findFragmentByTag("f$currentPosition")
        if(currentFragment != null && currentFragment is ImageEditItemFragment)
        {
            return currentFragment
        }
        return null
    }

    private fun updateCurrentImage()
    {
        val currentPosition = viewPager.currentItem
        val currentFragment = supportFragmentManager.findFragmentByTag("f$currentPosition")
        if(currentFragment != null && currentFragment is ImageEditItemFragment)
        {
            Log.e(TAG, "updateCurrentImage: called")
            viewModel.originalImageBitmap = currentFragment.getImageBitmap()
        }
    }

    override fun getFragmentFrame(): FrameLayout? {
        return filterOptionContainer
    }

    private val paperEffectListener = object  : PaperEffectFragment.Listener{

        override fun onTextColorSeekBarChanged(blockSize: Int, c : Int)
        {
            applyPaperEffect(blockSize,c.toDouble())

        }

        override fun onBackgroundSeekBarChanged(blockSize: Int, c : Int)
        {
            applyPaperEffect(blockSize,c.toDouble())
        }
    }

    private val imageEffectListener = object  : ImageEffectFragment.Listener{

        override fun onEffectValueChanged(brightness: Int, contrast : Float, hue: Int, saturtion: Int)
        {
            setBrightnessOfCurrentImage(brightness,contrast)
        }
    }

    private val imageFilterListener = object : FilterOptionFragment.Listener{
        override fun onFilterOptionClicked(filter: Filter)
        {
            Log.e(TAG, "onFilterOptionClicked: filterType : ${filter.type}")
            applyFilterToCurrentImage(filter)

        }
    }

    private val imageLoadListener = object : ImageEditItemFragment.ImageLoadListener{
        override fun onImageBitmapLoaded(imageBitmap: Bitmap)
        {
            imagePositionChip.visibility = View.VISIBLE
            reCropButton.visibility = if(isCurrentImageCropped())  View.VISIBLE else View.GONE
        }
    }


}