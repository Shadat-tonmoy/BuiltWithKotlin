package com.stcodesapp.documentscanner.ui.documentPages

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import com.stcodesapp.documentscanner.base.BaseActivity
import com.stcodesapp.documentscanner.constants.RequestCode
import com.stcodesapp.documentscanner.constants.RequestCode.Companion.REQUEST_PERSISTABLE_STORAGE_PERMISSION
import com.stcodesapp.documentscanner.constants.Tags
import com.stcodesapp.documentscanner.database.entities.Image
import com.stcodesapp.documentscanner.databinding.DocumentPagesLayoutBinding
import com.stcodesapp.documentscanner.helpers.CacheHelper
import com.stcodesapp.documentscanner.helpers.isAndroidX
import com.stcodesapp.documentscanner.models.ImageToPDFProgress
import com.stcodesapp.documentscanner.tasks.imageToPDF.ImageToPDFService
import com.stcodesapp.documentscanner.tasks.imageToPDF.ImageToPDFServiceHelper
import com.stcodesapp.documentscanner.ui.adapters.DocumentPageAdapter
import com.stcodesapp.documentscanner.ui.dialogs.ImageToPDFNameDialog
import com.stcodesapp.documentscanner.ui.helpers.DialogHelper
import com.stcodesapp.documentscanner.ui.imageCrop.ImageCropActivity
import kotlinx.android.synthetic.main.document_pages_layout.*
import javax.inject.Inject


class DocumentPagesActivity : BaseActivity()
{

    @Inject lateinit var dataBinding : DocumentPagesLayoutBinding
    @Inject lateinit var viewModel: DocumentPagesViewModel
    @Inject lateinit var cacheHelper: CacheHelper
    @Inject lateinit var dialogHelper: DialogHelper
    @Inject lateinit var serviceHelper: ImageToPDFServiceHelper

    lateinit var adapter : DocumentPageAdapter
    private val imageToPDFNameDialog : ImageToPDFNameDialog by lazy { ImageToPDFNameDialog(this, imageToPDFNameDialogListener) }

    companion object{
        private const val TAG = "DocumentPagesActivity"
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        init()
    }

    private fun init()
    {
        activityComponent.inject(this)
        viewModel.bindValueFromIntent(intent)
        serviceHelper.initService()
        initUI()
        observeDocumentPages()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == Activity.RESULT_OK)
        {
            when(requestCode)
            {
                REQUEST_PERSISTABLE_STORAGE_PERMISSION -> grantPersistableStorageUriPermission(data)
            }
        }

    }



    private fun initUI()
    {
        setContentView(dataBinding.root)
        dataBinding.viewModel = viewModel
        saveFab.isIconAnimated = false
        adapter = DocumentPageAdapter(this){ image: Image, position: Int -> onDocumentPageClicked(image,position)}
        documentPagesList.layoutManager = GridLayoutManager(this,2)
        documentPagesList.adapter = adapter
        saveAsPDFMenu.setOnClickListener {
            if(isAndroidX())
            {
                if(cacheHelper.getPersistableStorageUri()!=null)
                {
                    showPDFNameDialog()
                }
                else
                {
                    showPersistableStoragePermissionDialog()
                }
            }
        }
    }

    private fun showPDFNameDialog()
    {
        saveFab.close(true)
        imageToPDFNameDialog.showDialog()
    }

    private fun showPersistableStoragePermissionDialog()
    {
        dialogHelper.showAlertDialog("To save file in external storage the app needs your permission. Please choose a folder to proceed.",object :
            DialogHelper.AlertDialogListener {
            override fun onPositiveButtonClicked() { requestPersistableStorageUriPermission()}

            override fun onNegativeButtonClicked() {}
        },"Permission required!", "Grant Permission","Cancel")
    }

    private fun requestPersistableStorageUriPermission()
    {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE).apply {
            flags = Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION or
                    Intent.FLAG_GRANT_READ_URI_PERMISSION }
        startActivityForResult(intent, RequestCode.REQUEST_PERSISTABLE_STORAGE_PERMISSION)
    }

    private fun grantPersistableStorageUriPermission(data: Intent?)
    {
        if(data != null)
        {
            val grantedUri = data.data
            if(grantedUri != null)
            {
                contentResolver.takePersistableUriPermission(grantedUri, Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
                cacheHelper.setPersistableStorageUri(grantedUri)
                showPDFNameDialog()
            }
        }
    }

    private fun observeDocumentPages()
    {
        viewModel.fetchDocumentPages().observe(this, Observer {
            if(it!=null && it.isNotEmpty())
            {
                adapter.setDocumentPages(it)
                viewModel.selectedImages = it
            }
            else
            {
                finish()
            }
        })
    }

    private fun onDocumentPageClicked(documentPage : Image, position : Int)
    {
        val intent = Intent(this,ImageCropActivity::class.java)
        intent.putExtra(Tags.IMAGE_PATH,documentPage.path)
        intent.putExtra(Tags.IMAGE_ID,documentPage.id)
        intent.putExtra(Tags.DOCUMENT_ID,documentPage.docId)
        intent.putExtra(Tags.IMAGE_POSITION,position)
        startActivity(intent)
    }

    private fun createPDF(name : String)
    {
        serviceHelper.imageToPDFService
        serviceHelper.imageToPDFService?.createPDF(name,viewModel.selectedImages!!)
        serviceHelper.imageToPDFService?.listener =imageToPDFServiceListener
    }

    private fun openOutputList()
    {
        val resultIntent = Intent()
        resultIntent.putExtra(Tags.SHOW_OUTPUT, true)
        setResult(Activity.RESULT_OK, resultIntent)
        finish()
    }


    private val imageToPDFServiceListener = object : ImageToPDFService.Listener{
        override fun onImageToPDFProgressUpdate(progress: ImageToPDFProgress) {

            runOnUiThread { imageToPDFNameDialog.updateProgress(progress) }
        }

    }


    private val imageToPDFNameDialogListener = object : ImageToPDFNameDialog.Listener{
        override fun onSaveButtonClicked(name: String) {createPDF(name)}
        override fun onShowOutputButtonClicked() {openOutputList()}
    }

}