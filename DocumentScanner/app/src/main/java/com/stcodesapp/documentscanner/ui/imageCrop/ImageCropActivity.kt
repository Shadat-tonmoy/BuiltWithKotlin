package com.stcodesapp.documentscanner.ui.imageCrop

import android.os.Bundle
import android.util.Log
import android.widget.FrameLayout
import android.widget.SeekBar
import androidx.core.view.doOnLayout
import androidx.lifecycle.Observer
import com.stcodesapp.documentscanner.R
import com.stcodesapp.documentscanner.base.BaseActivity
import com.stcodesapp.documentscanner.constants.Tags
import com.stcodesapp.documentscanner.database.entities.Image
import com.stcodesapp.documentscanner.scanner.getFilteredImage
import com.stcodesapp.documentscanner.scanner.updateBrightnessOfImage
import com.stcodesapp.documentscanner.ui.adapters.ImageViewPagerAdapter
import com.stcodesapp.documentscanner.ui.helpers.*
import com.stcodesapp.documentscanner.ui.imageEffect.ImageEffectFragment
import com.stcodesapp.documentscanner.ui.paperEffect.PaperEffectFragment
import kotlinx.android.synthetic.main.activity_image_crop.*
import kotlinx.android.synthetic.main.crop_image_single_item_fragment.*
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
            showImageEffectFragment()
        }

        paperEffectButton.setOnClickListener {
            showPaperEffectFragment()
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

    private fun saveCurrentImage()
    {
        val currentPosition = viewPager.currentItem
        val currentFragment = supportFragmentManager.findFragmentByTag("f$currentPosition")
        if(currentFragment != null && currentFragment is CropImageSingleItemFragment)
        {
            currentFragment.saveUpdatedCropArea()
        }
    }

    private fun applyMagicFilter(blockSize: Int, c : Double)
    {
        val currentPosition = viewPager.currentItem
        val currentFragment = supportFragmentManager.findFragmentByTag("f$currentPosition")
        if(currentFragment != null && currentFragment is CropImageSingleItemFragment)
        {
            if(viewModel.originalImageBitmap == null) viewModel.originalImageBitmap = currentFragment.getImageBitmap()
            val srcBitmap = viewModel.originalImageBitmap
            val dstBitmap = srcBitmap!!.copy(srcBitmap.config,true)
            var finalBlockSize = blockSize
            var finalC = c
            if(finalBlockSize % 2 == 0 ) finalBlockSize += 1
            Log.e(TAG, "applyMagicFilter: with BlockSize : $finalBlockSize, C : $finalC")
            getFilteredImage(srcBitmap, dstBitmap,finalBlockSize,finalC)
            currentFragment.cropImageView.setImageBitmap(dstBitmap,false)
            cropImageView.isShowCropOverlay = false

        }
    }

    private fun setBrightnessOfCurrentImage(brightnessValue : Int)
    {
        val currentPosition = viewPager.currentItem
        val currentFragment = supportFragmentManager.findFragmentByTag("f$currentPosition")
        if(currentFragment != null && currentFragment is CropImageSingleItemFragment)
        {
            if(viewModel.originalImageBitmap == null) viewModel.originalImageBitmap = currentFragment.getImageBitmap()
            val srcBitmap = viewModel.originalImageBitmap
            val dstBitmap = srcBitmap!!.copy(srcBitmap.config,true)
            updateBrightnessOfImage(srcBitmap,dstBitmap,brightnessValue)
            currentFragment.cropImageView.setImageBitmap(dstBitmap,false)
            cropImageView.isShowCropOverlay = false
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

    override fun getFragmentFrame(): FrameLayout? {
        return filterOptionContainer
    }

    private val paperEffectListener = object  : PaperEffectFragment.Listener{

        override fun onTextColorSeekBarChanged(blockSize: Int, c : Int)
        {
            applyMagicFilter(blockSize,c.toDouble())

        }

        override fun onBackgroundSeekBarChanged(blockSize: Int, c : Int)
        {
            applyMagicFilter(blockSize,c.toDouble())
        }
    }

    private val imageEffectListener = object  : ImageEffectFragment.Listener{

        override fun onEffectValueChanged(brightness: Int, hue: Int, saturtion: Int)
        {
            setBrightnessOfCurrentImage(brightness)
        }
    }


}