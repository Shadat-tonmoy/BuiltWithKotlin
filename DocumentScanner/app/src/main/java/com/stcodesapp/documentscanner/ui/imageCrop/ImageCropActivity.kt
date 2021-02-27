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

}