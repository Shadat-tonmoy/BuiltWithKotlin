package com.stcodesapp.documentscanner.ui.imageCrop

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.stcodesapp.documentscanner.R
import com.stcodesapp.documentscanner.base.BaseFragment
import com.stcodesapp.documentscanner.constants.Tags
import com.stcodesapp.documentscanner.database.entities.Image
import com.stcodesapp.documentscanner.helpers.getPolygonFromCropAreaJson
import com.stcodesapp.documentscanner.helpers.isValidPolygon
import com.theartofdev.edmodo.cropper.CropImageView
import kotlinx.android.synthetic.main.image_edit_item_fragment.*
import java.io.File
import javax.inject.Inject

class ImageCropItemFragment : BaseFragment() {

    companion object {
        fun newInstance(image : Image, imagePosition : Int, showOriginalImage : Boolean) : ImageCropItemFragment
        {
            val fragment = ImageCropItemFragment()
            val args = Bundle()
            args.putSerializable(Tags.SERIALIZED_IMAGE,image)
            args.putInt(Tags.IMAGE_POSITION,imagePosition)
            args.putBoolean(Tags.SHOW_ORIGINAL_IMAGE,showOriginalImage)
            fragment.arguments = args
            return fragment
        }

        private const val TAG = "ImageCropItemFragment"
    }

    @Inject lateinit var viewModel: ImageCropItemViewModel

    override fun onAttach(context: Context) {
        super.onAttach(context)
        activityComponent.inject(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
    {
        return inflater.inflate(R.layout.image_crop_item_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?)
    {
        super.onViewCreated(view, savedInstanceState)
        initUI()
    }

    private fun initUI()
    {
        viewModel.bindValueFromArgument(arguments)
        val chosenImage = viewModel.chosenImage
        if(chosenImage != null)
        {
            cropImageView.setImageUriAsync(Uri.fromFile(File(chosenImage.path)))
            cropImageView.setOnSetImageUriCompleteListener { view, uri, error ->
                setSavedValue(chosenImage)
            }

            /*cropImageView.setOnCropWindowChangedListener {
                saveUpdatedCropArea()
            }*/
        }
    }

    private fun setSavedValue(serializedImage: Image)
    {
        setSavedCropArea(serializedImage)
        if(!viewModel.showOriginalImage)
        {
            //applySavedCustomFilter(serializedImage)
            //applySavedPaperEffect(serializedImage)
        }


    }

    private fun setSavedCropArea(serializedImage: Image)
    {
        val savedCropArea = serializedImage.originalCropArea
        if (!savedCropArea.isNullOrEmpty())
        {
            val polygon = getPolygonFromCropAreaJson(savedCropArea)
            Log.e(TAG, "setSavedCropArea: savedCropArea : $savedCropArea")
            if(isValidPolygon(polygon))
            {
                cropImageView.cropPolygon = polygon
            }
        }
        cropImageView.guidelines = CropImageView.Guidelines.OFF
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
    }

}