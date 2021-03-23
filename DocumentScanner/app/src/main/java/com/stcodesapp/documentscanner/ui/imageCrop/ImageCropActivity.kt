package com.stcodesapp.documentscanner.ui.imageCrop

import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
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
import kotlinx.android.synthetic.main.activity_image_crop.*
import javax.inject.Inject

class ImageCropActivity : BaseActivity(), FragmentFrameWrapper
{
    companion object{
        private const val TAG = "ImageCropActivity"
        init {
            System.loadLibrary("native-lib")
        }
    }

    @Inject lateinit var viewModel : CropImageViewModel
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

        setContentView(R.layout.activity_image_crop)

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
            showFilterFragment()
        }

        paperEffectButton.setOnClickListener {
            showPaperEffectFragment()
        }

        imageEnrichButton.setOnClickListener {
            showImageEffectFragment()
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

    private fun applyPaperEffect(blockSize: Int, c : Double)
    {
        val currentPosition = viewPager.currentItem
        val currentFragment = supportFragmentManager.findFragmentByTag("f$currentPosition")
        if(currentFragment != null && currentFragment is CropImageSingleItemFragment)
        {
            if(viewModel.originalImageBitmap != null)
            {
                currentFragment.applyPaperEffect(blockSize,c,viewModel.originalImageBitmap!!)
                val currentImage = viewPagerAdapter.getDocumentPageAt(currentPosition)
                if(currentImage != null)
                {
                    viewModel.savePaperEffectFilterInfo(currentImage, PaperEffectFilter(blockSize,c))
                }
            }
        }
    }

    private fun setBrightnessOfCurrentImage(brightnessValue : Int, contrastValue : Float)
    {
        val currentPosition = viewPager.currentItem
        val currentFragment = supportFragmentManager.findFragmentByTag("f$currentPosition")
        if(currentFragment != null && currentFragment is CropImageSingleItemFragment)
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
        val currentPosition = viewPager.currentItem
        val currentFragment = supportFragmentManager.findFragmentByTag("f$currentPosition")
        if(currentFragment != null && currentFragment is CropImageSingleItemFragment)
        {
            currentFragment.applyFilter(filter, viewModel.originalImageBitmap)
            val currentImage = viewPagerAdapter.getDocumentPageAt(currentPosition)
            if(currentImage != null)
            {
                viewModel.saveCurrentImageFilterInfo(currentImage, filter)
            }

        }
    }

    private fun updateCurrentImage()
    {
        val currentPosition = viewPager.currentItem
        val currentFragment = supportFragmentManager.findFragmentByTag("f$currentPosition")
        if(currentFragment != null && currentFragment is CropImageSingleItemFragment)
        {
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

    private val imageLoadListener = object : CropImageSingleItemFragment.ImageLoadListener{
        override fun onImageBitmapLoaded(imageBitmap: Bitmap)
        {
            Log.e(TAG, "onImageBitmapLoaded: setting")
            viewModel.originalImageBitmap = imageBitmap
        }

    }


}