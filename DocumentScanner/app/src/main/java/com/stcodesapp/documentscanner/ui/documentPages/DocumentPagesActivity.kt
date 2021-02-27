package com.stcodesapp.documentscanner.ui.documentPages

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import com.stcodesapp.documentscanner.base.BaseActivity
import com.stcodesapp.documentscanner.constants.Tags
import com.stcodesapp.documentscanner.database.entities.Image
import com.stcodesapp.documentscanner.databinding.DocumentPagesLayoutBinding
import com.stcodesapp.documentscanner.ui.adapters.DocumentPageAdapter
import com.stcodesapp.documentscanner.ui.dialogs.ImageToPDFNameDialog
import com.stcodesapp.documentscanner.ui.imageCrop.ImageCropActivity
import kotlinx.android.synthetic.main.document_pages_layout.*
import javax.inject.Inject


class DocumentPagesActivity : BaseActivity()
{

    @Inject lateinit var dataBinding : DocumentPagesLayoutBinding
    @Inject lateinit var viewModel: DocumentPagesViewModel
    lateinit var adapter : DocumentPageAdapter
    private val imageToPDFNameDialog : ImageToPDFNameDialog by lazy { ImageToPDFNameDialog(this, imageToPDFNameDialogListener) }

    companion object{
        private const val TAG = "DocumentPagesActivity"
        init {
            System.loadLibrary("NativeImageProcessor")
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityComponent.inject(this)
        viewModel.bindValueFromIntent(intent)
        initUI()
        observeDocumentPages()
    }

    private fun initUI()
    {
        setContentView(dataBinding.root)
        dataBinding.viewModel = viewModel
        saveFab.isIconAnimated = false
        adapter = DocumentPageAdapter(this){ image: Image, position: Int -> onDocumentPageClicked(image,position)}
        documentPagesList.layoutManager = GridLayoutManager(this,2)
        documentPagesList.adapter = adapter
        saveAsPDFMenu.setOnClickListener { showPDFNameDialog() }
        saveAsImageMenu
    }

    private fun showPDFNameDialog()
    {
        saveFab.close(true)
        imageToPDFNameDialog.showDialog()
    }

    private fun observeDocumentPages()
    {
        viewModel.fetchDocumentPages().observe(this, Observer {
            if(it!=null && it.isNotEmpty())
            {
                adapter.setDocumentPages(it)
                viewModel.selectedImages = it
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
        viewModel.createPDF(name).observe(this, Observer {
            imageToPDFNameDialog.updateProgress(it)
        })
    }

    private fun openOutputList()
    {
        val resultIntent = Intent()
        resultIntent.putExtra(Tags.SHOW_OUTPUT, true)
        setResult(Activity.RESULT_OK, resultIntent)
        finish()
    }



    private val imageToPDFNameDialogListener = object : ImageToPDFNameDialog.Listener{
        override fun onSaveButtonClicked(name: String) {createPDF(name)}
        override fun onShowOutputButtonClicked() {openOutputList()}
    }
}