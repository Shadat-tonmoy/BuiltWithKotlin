package com.stcodesapp.documentscanner.ui.imageCrop

import android.os.Bundle
import androidx.core.view.doOnLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Observer
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.stcodesapp.documentscanner.R
import com.stcodesapp.documentscanner.base.BaseActivity
import com.stcodesapp.documentscanner.constants.Tags
import com.stcodesapp.documentscanner.database.entities.Image
import com.stcodesapp.documentscanner.ui.adapters.ImageViewPagerAdapter
import com.stcodesapp.documentscanner.ui.helpers.ActivityNavigator
import com.stcodesapp.documentscanner.ui.helpers.DialogHelper
import com.stcodesapp.documentscanner.ui.helpers.showToast
import kotlinx.android.synthetic.main.activity_image_crop.*
import javax.inject.Inject

class ImageCropActivity : BaseActivity()
{
    companion object{
        private const val TAG = "ImageCropActivity"
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
        viewPagerAdapter.setDocumentPages(it)
        val selectedPosition = intent.getIntExtra(Tags.IMAGE_POSITION,-1)
        if(selectedPosition > 0)
        {
            viewPager.doOnLayout {
                viewPager.setCurrentItem(selectedPosition,false)
                intent.putExtra(Tags.IMAGE_POSITION,-1)
            }
        }

    }

    private fun initClickListener()
    {
        cropButton.setOnClickListener {


        }

        rotateButton.setOnClickListener {
            rotateCurrentImage()
        }

        deleteButton.setOnClickListener {
            showDeleteImageWarning()

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
        if(chosenImagePosition > 0)
        {
            val imageAtPosition = viewPagerAdapter.getDocumentPageAt(chosenImagePosition)
            if(imageAtPosition != null)
            {
                viewModel.deleteImage(imageAtPosition).observe(this, Observer {
                    if(it != null && it > 0)
                    {
                        //viewPagerAdapter.notifyItemRemoved(viewModel.chosenImagePosition)
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

}