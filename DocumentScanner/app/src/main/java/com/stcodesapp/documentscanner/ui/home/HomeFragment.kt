package com.stcodesapp.documentscanner.ui.home

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.shadattonmoy.imagepickerforandroid.ImagePickerForAndroid
import com.shadattonmoy.imagepickerforandroid.model.ImageFile
import com.stcodesapp.documentscanner.R
import com.stcodesapp.documentscanner.base.BaseFragment
import com.stcodesapp.documentscanner.constants.RequestCode
import com.stcodesapp.documentscanner.database.entities.Document
import com.stcodesapp.documentscanner.databinding.HomeLayoutBinding
import com.stcodesapp.documentscanner.helpers.PermissionHelper
import com.stcodesapp.documentscanner.ui.adapters.DocumentListAdapter
import com.stcodesapp.documentscanner.ui.dialogs.ImageCopyProgressDialog
import com.stcodesapp.documentscanner.ui.helpers.ActivityNavigator
import com.stcodesapp.documentscanner.ui.helpers.showToast
import kotlinx.android.synthetic.main.home_layout.*
import kotlinx.android.synthetic.main.home_layout.view.*
import org.opencv.android.OpenCVLoader
import javax.inject.Inject

class HomeFragment : BaseFragment(), ImagePickerForAndroid.SingleImageSelectionListener, ImagePickerForAndroid.BatchImageSelectionListener, DocumentListAdapter.Listener
{

    @Inject lateinit var viewModel : HomeViewModel
    @Inject lateinit var activityNavigator: ActivityNavigator
    @Inject lateinit var dataBinding : HomeLayoutBinding
    private lateinit var adapter : DocumentListAdapter
    @Inject lateinit var permissionHelper: PermissionHelper

    companion object
    {
        fun newInstance(): HomeFragment {
            val args = Bundle()
            val fragment = HomeFragment()
            fragment.arguments = args
            return fragment
        }

        private const val TAG = "HomeFragment"
    }

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        if(OpenCVLoader.initDebug())
        {
            Log.e(TAG, "onCreate: OpenCVLoadedSuccess")
        }
        else
        {
            Log.e(TAG, "onCreate: OpenCVLoadedFailed")
        }
    }

    override fun onAttach(context: Context)
    {
        super.onAttach(context)
        activityComponent.inject(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
    {
        return dataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUI()
        observeDocumentListLiveData()

    }

    private fun initUI()
    {
        dataBinding.root.galleryMenu.setOnClickListener { openImagePicker() }
        adapter = DocumentListAdapter(requireContext(),this)
        documentList.layoutManager = LinearLayoutManager(requireContext())
        documentList.adapter = adapter
    }

    private fun openImagePicker()
    {
        if(!permissionHelper.isWriteStoragePermissionGranted()) return
        dataBinding.root.menu.toggle(true)
        val imagePickerForAndroid = ImagePickerForAndroid.Builder(requireContext())
            .batchMode(true)
            .batchImageSelectionListener (this)
            .singleImageSelectionListener (this )
            .navigationIcon(R.drawable.back_white)
            .build()
        imagePickerForAndroid.openImagePicker()
    }

    override fun onSingleImageSelected(selectedImage: ImageFile?)
    {
        /*val intent = Intent(this, DocumentPagesActivity::class.java)
        startActivity(intent)*/

    }

    override fun onBatchImageSelected(selectedImages: MutableList<ImageFile>?)
    {
        if(selectedImages!=null && selectedImages.size>0)
        {
            copySelectedImages(selectedImages)
        }
        else context?.showToast("No image selected!")
    }

    private fun copySelectedImages(selectedImages: MutableList<ImageFile>)
    {
        val progressDialog = ImageCopyProgressDialog(requireContext())
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

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
    }

    override fun onStop() {
        super.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)
        {
            when(requestCode)
            {
                RequestCode.REQUEST_STORAGE_WRITE_PERMISSION -> openImagePicker()
                RequestCode.REQUEST_CAMERA_PERMISSION -> activityNavigator.openCameraToCreateDocument()
            }
        }
    }

    private fun observeDocumentListLiveData()
    {
        viewModel.fetchDocumentListLiveData().observe(viewLifecycleOwner, Observer {
            // Log.e("TAG", "observeDocumentListLiveData: Docs : $it")
            if(it != null && it.isNotEmpty())
            {
                adapter.setDocuments(it)
            }
        })
    }

    override fun onItemClick(document: Document)
    {
        activityNavigator.toDocumentPagesScreen(document.id)
    }
}