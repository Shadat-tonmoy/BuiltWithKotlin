package com.stcodesapp.documentscanner.ui.imageCrop

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.stcodesapp.documentscanner.R
import com.stcodesapp.documentscanner.base.BaseFragment
import com.stcodesapp.documentscanner.constants.Tags
import com.stcodesapp.documentscanner.database.entities.Image
import com.stcodesapp.documentscanner.utils.BitmapUtil
import com.theartofdev.edmodo.cropper.CropImageView
import kotlinx.android.synthetic.main.crop_image_single_item_fragment.*
import kotlinx.android.synthetic.main.crop_image_single_item_fragment.cropImageView
import kotlinx.android.synthetic.main.image_preview_layout.*
import java.io.File

class CropImageSingleItemFragment : BaseFragment() {

    companion object {
        private const val TAG = "CropImageSingleItemFrag"
        fun newInstance(image : Image) : Fragment
        {
            val fragment = CropImageSingleItemFragment()
            val args = Bundle()
            args.putSerializable(Tags.SERIALIZED_IMAGE,image)
            fragment.arguments = args
            return fragment
        }
    }

    private lateinit var viewModel: CropImageSingleItemViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.crop_image_single_item_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val serializedImage = arguments?.getSerializable(Tags.SERIALIZED_IMAGE) as Image
        cropImageView.setImageUriAsync(Uri.fromFile(File(serializedImage.path)))
        cropImageView.guidelines = CropImageView.Guidelines.OFF

    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
//        viewModel = ViewModelProvider(this).get(CropImageSingleItemViewModel::class.java)
        // TODO: Use the ViewModel
    }

}