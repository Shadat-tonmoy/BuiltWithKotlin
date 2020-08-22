package com.stcodesapp.documentscanner

import android.content.res.Resources
import android.os.Build
import android.os.Bundle
import com.shadattonmoy.imagepickerforandroid.ImagePickerForAndroid
import com.stcodesapp.documentscanner.base.BaseActivity
import com.stcodesapp.documentscanner.databinding.ActivityMainBinding
import com.stcodesapp.documentscanner.ui.helpers.ActivityNavigator
import kotlinx.android.synthetic.main.activity_camera.view.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.view.*
import javax.inject.Inject

class MainActivity : BaseActivity(), ImagePickerForAndroid.SingleImageSelectionListener, ImagePickerForAndroid.BatchImageSelectionListener
{
    @Inject lateinit var activityNavigator: ActivityNavigator
    @Inject lateinit var dataBinding : ActivityMainBinding

    companion object{
        private const val TAG = "MainActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        activityComponent.inject(this)
        initUI()
    }

    private fun initUI()
    {
        setContentView(dataBinding.root)
        dataBinding.root.galleryMenu.setOnClickListener { openImagePicker() }
    }

    private fun openImagePicker()
    {
        val imagePickerForAndroid = ImagePickerForAndroid.Builder(this)
            .batchMode(true)
            .batchImageSelectionListener { this }
            .singleImageSelectionListener { this }
            .toolbarColor(getImagePickerColor())
            .statusBarColor(getImagePickerColor())
            .navigationIcon(R.drawable.back_white)
            .build()
        imagePickerForAndroid.openImagePicker()
    }

    private fun getImagePickerColor() : Int
    {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) resources.getColor(R.color.colorPrimary,theme) else resources.getColor(R.color.colorPrimary)
    }

    override fun onSingleImageSelected(p0: String?) {

    }

    override fun onBatchImageSelected(p0: MutableList<String>?) {

    }


}