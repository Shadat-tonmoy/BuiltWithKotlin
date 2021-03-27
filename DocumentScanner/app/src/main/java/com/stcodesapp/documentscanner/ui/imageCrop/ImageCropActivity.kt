package com.stcodesapp.documentscanner.ui.imageCrop

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.stcodesapp.documentscanner.R
import com.stcodesapp.documentscanner.ui.helpers.showToast
import com.stcodesapp.documentscanner.ui.imageEdit.ImageEditItemFragment
import kotlinx.android.synthetic.main.activity_image_edit.*

class ImageCropActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image_crop)
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
}