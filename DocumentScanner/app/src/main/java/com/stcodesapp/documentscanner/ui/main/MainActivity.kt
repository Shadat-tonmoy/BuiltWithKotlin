package com.stcodesapp.documentscanner.ui.main

import android.content.Intent
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.Observer
import com.shadattonmoy.imagepickerforandroid.ImagePickerForAndroid
import com.stcodesapp.documentscanner.R
import com.stcodesapp.documentscanner.base.BaseActivity
import com.stcodesapp.documentscanner.constants.RequestCode
import com.stcodesapp.documentscanner.databinding.ActivityMainBinding
import com.stcodesapp.documentscanner.helpers.PermissionHelper
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
    @Inject lateinit var permissionHelper: PermissionHelper

    companion object{
        private const val TAG = "MainActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        activityComponent.inject(this)
        initUI()
        observeDocumentListLiveData()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(grantResults.isNotEmpty() && grantResults[0] == PERMISSION_GRANTED)
        {
            when(requestCode)
            {
                RequestCode.REQUEST_STORAGE_WRITE_PERMISSION -> openImagePicker()
                RequestCode.REQUEST_CAMERA_PERMISSION -> activityNavigator.openCameraToCreateDocument()
            }
        }
    }

    private fun initUI()
    {
        setContentView(dataBinding.root)
        dataBinding.root.galleryMenu.setOnClickListener { openImagePicker() }
    }

    private fun openImagePicker()
    {
        if(!permissionHelper.isWriteStoragePermissionGranted()) return
        dataBinding.root.menu.toggle(true)
        val imagePickerForAndroid = ImagePickerForAndroid.Builder(this)
            .batchMode(true)
            .batchImageSelectionListener (this)
            .singleImageSelectionListener (this )
            .navigationIcon(R.drawable.back_white)
            .build()
        imagePickerForAndroid.openImagePicker()
    }

    override fun onSingleImageSelected(selectedImage: String?)
    {
        val intent = Intent(this,DocumentPagesActivity::class.java)
        startActivity(intent)

    }

    override fun onBatchImageSelected(selectedImages: MutableList<String>?)
    {
        if(selectedImages!=null && selectedImages.size>0)
        {
            copySelectedImages(selectedImages)
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
                if(it.size == totalImage)
                {
                    progressDialog.hideDialog()
                    activityNavigator.toDocumentPagesScreen(it[0].docId)

                }
            }
        })
    }

    private fun observeDocumentListLiveData()
    {
        viewModel.fetchDocumentListLiveData().observe(this, Observer {
            Log.e(TAG, "observeDocumentListLiveData: Data : $it")
        })
    }


}