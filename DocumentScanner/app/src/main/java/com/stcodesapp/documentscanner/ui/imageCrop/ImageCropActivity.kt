package com.stcodesapp.documentscanner.ui.imageCrop

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Observer
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.stcodesapp.documentscanner.R
import com.stcodesapp.documentscanner.base.BaseActivity
import com.stcodesapp.documentscanner.database.entities.Image
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
    private lateinit var viewPagerAdapter: ViewPagerAdapter

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
        viewPagerAdapter = ViewPagerAdapter(this)
        viewPager.adapter = viewPagerAdapter
        fetchDocumentPages()
    }

    private fun fetchDocumentPages()
    {
        viewModel.fetchDocumentPages().observe(this, Observer {
            viewPagerAdapter.documentPages = it
            viewPagerAdapter.notifyDataSetChanged()
        })
    }

    override fun onBackPressed() {
        if (viewPager.currentItem == 0)
        {
            super.onBackPressed()
        }
        else
        {
            viewPager.currentItem = viewPager.currentItem - 1
        }
    }

    private inner class ViewPagerAdapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity)
    {
        var documentPages = listOf<Image>()
        override fun getItemCount(): Int = documentPages.size

        override fun createFragment(position: Int): Fragment
        {
            val currentDocPage = documentPages[position]
            return CropImageSingleItemFragment.newInstance(currentDocPage)
        }
    }
}