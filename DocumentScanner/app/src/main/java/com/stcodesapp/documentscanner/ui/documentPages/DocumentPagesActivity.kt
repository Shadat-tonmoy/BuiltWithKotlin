package com.stcodesapp.documentscanner.ui.documentPages

import android.os.Bundle
import androidx.recyclerview.widget.GridLayoutManager
import com.stcodesapp.documentscanner.base.BaseActivity
import com.stcodesapp.documentscanner.databinding.DocumentPagesLayoutBinding
import com.stcodesapp.documentscanner.ui.adapters.DocumentPageAdapter
import com.stcodesapp.documentscanner.ui.helpers.getDummyDocumentPages
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
        initUI()
    }

    private fun initUI()
    {
        setContentView(dataBinding.root)
        dataBinding.viewModel = viewModel
        adapter = DocumentPageAdapter(this)
        documentPagesList.layoutManager = GridLayoutManager(this,2)
        documentPagesList.adapter = adapter
        adapter.setDocumentPages(getDummyDocumentPages())


    }
}