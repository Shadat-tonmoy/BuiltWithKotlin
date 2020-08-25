package com.stcodesapp.documentscanner.ui.imageEdit

import android.os.Bundle
import androidx.recyclerview.widget.GridLayoutManager
import com.stcodesapp.documentscanner.R
import com.stcodesapp.documentscanner.base.BaseActivity
import com.stcodesapp.documentscanner.databinding.ImagePreviewLayoutBinding
import com.stcodesapp.documentscanner.ui.adapters.DocumentPageAdapter
import com.stcodesapp.documentscanner.ui.helpers.getDummyDocumentPages
import kotlinx.android.synthetic.main.document_pages_layout.*
import javax.inject.Inject

class ImagePreviewActivity : BaseActivity()
{

    @Inject lateinit var viewModel: ImagePreviewViewModel
    @Inject lateinit var dataBinding : ImagePreviewLayoutBinding

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        activityComponent.inject(this)
        initUI()
    }

    private fun initUI()
    {
        setContentView(dataBinding.root)
        dataBinding.viewModel = viewModel
        viewModel.setChosenImagePathFromIntent(intent)
    }
}