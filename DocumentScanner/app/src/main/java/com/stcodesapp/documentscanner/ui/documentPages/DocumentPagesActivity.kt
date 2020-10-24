package com.stcodesapp.documentscanner.ui.documentPages

import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import com.stcodesapp.documentscanner.base.BaseActivity
import com.stcodesapp.documentscanner.constants.Tags
import com.stcodesapp.documentscanner.database.entities.Image
import com.stcodesapp.documentscanner.databinding.DocumentPagesLayoutBinding
import com.stcodesapp.documentscanner.ui.adapters.DocumentPageAdapter
import com.stcodesapp.documentscanner.ui.imageEdit.ImagePreviewActivity
import kotlinx.android.synthetic.main.document_pages_layout.*
import javax.inject.Inject

class DocumentPagesActivity : BaseActivity()
{

    @Inject lateinit var dataBinding : DocumentPagesLayoutBinding
    @Inject lateinit var viewModel: DocumentPagesViewModel
    lateinit var adapter : DocumentPageAdapter


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
        adapter = DocumentPageAdapter(this){onDocumentPageClicked(it)}
        documentPagesList.layoutManager = GridLayoutManager(this,2)
        documentPagesList.adapter = adapter
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

    private fun onDocumentPageClicked(documentPage : Image)
    {
        val intent = Intent(this,ImagePreviewActivity::class.java)
        intent.putExtra(Tags.IMAGE_PATH,documentPage.path)
        startActivity(intent)
    }
}