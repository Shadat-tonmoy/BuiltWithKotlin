package com.stcodesapp.documentscanner.ui.main

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.Observer
import com.shadattonmoy.imagepickerforandroid.ImagePickerForAndroid
import com.stcodesapp.documentscanner.R
import com.stcodesapp.documentscanner.base.BaseActivity
import com.stcodesapp.documentscanner.databinding.ActivityMainBinding
import com.stcodesapp.documentscanner.ui.dialogs.ImageCopyProgressDialog
import com.stcodesapp.documentscanner.ui.documentPages.DocumentPagesActivity
import com.stcodesapp.documentscanner.ui.helpers.ActivityNavigator
import com.stcodesapp.documentscanner.ui.helpers.showToast
import kotlinx.android.synthetic.main.activity_main.view.*
import javax.inject.Inject

class MainActivity : BaseActivity(), ImagePickerForAndroid.SingleImageSelectionListener, ImagePickerForAndroid.BatchImageSelectionListener
{
    @Inject lateinit var activityNavigator: ActivityNavigator
    @Inject lateinit var dataBinding : ActivityMainBinding
    @Inject lateinit var viewModel: MainViewModel

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
        dataBinding.root.menu.toggle(true)
        val imagePickerForAndroid = ImagePickerForAndroid.Builder(this)
            .batchMode(true)
            .batchImageSelectionListener (this)
            .singleImageSelectionListener (this )
            .navigationIcon(R.drawable.back_white)
            .build()
        imagePickerForAndroid.openImagePicker()
    }

    private fun getImagePickerColor() : Int
    {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) resources.getColor(
            R.color.colorPrimary,theme) else resources.getColor(
            R.color.colorPrimary
        )
    }

    override fun onSingleImageSelected(selectedImage: String?)
    {
        val intent = Intent(this,DocumentPagesActivity::class.java)
        startActivity(intent)

    }

    override fun onBatchImageSelected(selectedImages: MutableList<String>?)
    {
        Log.e(TAG, "onBatchImageSelected: SelectedImages $selectedImages")
        if(selectedImages!=null && selectedImages.size>0)
        {
            copySelectedImages(selectedImages)
//            val intent = Intent(this,DocumentPagesActivity::class.java)
//            startActivity(intent)
        }
        else this.showToast("No image selected!")
    }

    private fun copySelectedImages(selectedImages: MutableList<String>)
    {
        val progressDialog = ImageCopyProgressDialog(this)
        progressDialog.showDialog()
        val totalImage = selectedImages.size
        viewModel.copySelectedImages(selectedImages).observe(this, Observer {
            if(it!=null && it.isNotEmpty())
            {
                progressDialog.updateMessage("Processing image (${it.size}/$totalImage)")
                if(it.size == totalImage) progressDialog.hideDialog()
            }
        })
    }


}