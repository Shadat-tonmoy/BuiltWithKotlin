package com.stcodesapp.documentscanner.ui.imageCrop

import android.net.Uri
import android.os.Bundle
import androidx.lifecycle.Observer
import com.stcodesapp.documentscanner.R
import com.stcodesapp.documentscanner.base.BaseActivity
import com.stcodesapp.documentscanner.database.entities.Image
import kotlinx.android.synthetic.main.image_edit_item_fragment.*
import java.io.File
import javax.inject.Inject

class ImageCropActivity : BaseActivity()
{

    @Inject lateinit var viewModel: ImageCropViewModel
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        activityComponent.inject(this)
        init()

    }

    private fun init()
    {
        setContentView(R.layout.activity_image_crop)
        initClickListener()
        viewModel.bindValueFromIntent(intent)
        viewModel.fetchChosenImageToReCrop().observe(this,chosenImageObserver)
    }

    private fun initClickListener()
    {

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
            cropImageView.setImageUriAsync(Uri.fromFile(File(it.path)))
        }
    }
}