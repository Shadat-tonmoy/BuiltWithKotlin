package com.stcodesapp.documentscanner.ui.main

import android.content.Intent
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.FrameLayout
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.shadattonmoy.imagepickerforandroid.ImagePickerForAndroid
import com.stcodesapp.documentscanner.R
import com.stcodesapp.documentscanner.base.BaseActivity
import com.stcodesapp.documentscanner.constants.RequestCode
import com.stcodesapp.documentscanner.database.entities.Document
import com.stcodesapp.documentscanner.databinding.ActivityMainBinding
import com.stcodesapp.documentscanner.helpers.PermissionHelper
import com.stcodesapp.documentscanner.ui.adapters.DocumentListAdapter
import com.stcodesapp.documentscanner.ui.adapters.DocumentPageAdapter
import com.stcodesapp.documentscanner.ui.dialogs.ImageCopyProgressDialog
import com.stcodesapp.documentscanner.ui.documentPages.DocumentPagesActivity
import com.stcodesapp.documentscanner.ui.helpers.ActivityNavigator
import com.stcodesapp.documentscanner.ui.helpers.FragmentFrameWrapper
import com.stcodesapp.documentscanner.ui.helpers.FragmentNavigator
import com.stcodesapp.documentscanner.ui.helpers.showToast
import com.stcodesapp.documentscanner.ui.home.HomeFragment
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.view.*
import kotlinx.android.synthetic.main.document_pages_layout.*
import javax.inject.Inject

class MainActivity : BaseActivity(), ImagePickerForAndroid.SingleImageSelectionListener, ImagePickerForAndroid.BatchImageSelectionListener, DocumentListAdapter.Listener, FragmentFrameWrapper
{
    @Inject lateinit var activityNavigator: ActivityNavigator
    @Inject lateinit var fragmentNavigator: FragmentNavigator
    @Inject lateinit var dataBinding : ActivityMainBinding
    @Inject lateinit var viewModel: MainViewModel
    @Inject lateinit var permissionHelper: PermissionHelper
    lateinit var adapter : DocumentListAdapter


    companion object{
        private const val TAG = "MainActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        activityComponent.inject(this)
        initUI()
//        observeDocumentListLiveData()
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
        /*dataBinding.root.galleryMenu.setOnClickListener { openImagePicker() }
        adapter = DocumentListAdapter(this,this)
        documentList.layoutManager = LinearLayoutManager(this)
        documentList.adapter = adapter*/
        fragmentNavigator.loadHomeFragment()
        bottomNavigationView.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.home_menu -> {
                    fragmentNavigator.loadHomeFragment()
                    true
                }

                R.id.saved_file_menu -> {
                    fragmentNavigator.loadSavedFilesFragment()
                    true
                }

                R.id.more_menu -> {
                    fragmentNavigator.loadMoreFragment()
                    true
                }
                else -> {
                    false
                }
            }
        }
    }

    override fun onBackPressed()
    {
        super.onBackPressed()
        highlightBottomNavMenu()

    }

    private fun highlightBottomNavMenu()
    {
        when
        {
            fragmentNavigator.isHomeFragmentLoaded() -> bottomNavigationView.menu.findItem(R.id.home_menu).isChecked = true
            fragmentNavigator.isSavedFilesFragmentLoaded() -> bottomNavigationView.menu.findItem(R.id.saved_file_menu).isChecked = true
            fragmentNavigator.isMoreFragmentLoaded() -> bottomNavigationView.menu.findItem(R.id.more_menu).isChecked = true
        }

    }

    private fun openImagePicker()
    {
        /*if(!permissionHelper.isWriteStoragePermissionGranted()) return
        dataBinding.root.menu.toggle(true)
        val imagePickerForAndroid = ImagePickerForAndroid.Builder(this)
            .batchMode(true)
            .batchImageSelectionListener (this)
            .singleImageSelectionListener (this )
            .navigationIcon(R.drawable.back_white)
            .build()
        imagePickerForAndroid.openImagePicker()*/
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
            if(it != null && it.isNotEmpty())
            {
                adapter.setDocuments(it)
            }
        })
    }

    override fun onItemClick(document: Document)
    {

    }

    override fun getFragmentFrame(): FrameLayout? {
        return fragmentContainer
    }


}