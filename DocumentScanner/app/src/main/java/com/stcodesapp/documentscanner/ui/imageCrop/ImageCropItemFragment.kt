package com.stcodesapp.documentscanner.ui.imageCrop

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.stcodesapp.documentscanner.R

class ImageCropItemFragment : Fragment() {

    companion object {
        fun newInstance() = ImageCropItemFragment()
    }

    private lateinit var viewModel: ImageCropItemFragementViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
    {
        return inflater.inflate(R.layout.image_crop_item_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(ImageCropItemFragementViewModel::class.java)
        // TODO: Use the ViewModel
    }

}