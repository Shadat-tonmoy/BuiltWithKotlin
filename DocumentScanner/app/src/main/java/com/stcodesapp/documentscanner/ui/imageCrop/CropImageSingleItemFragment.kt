package com.stcodesapp.documentscanner.ui.imageCrop

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import com.stcodesapp.documentscanner.R
import com.stcodesapp.documentscanner.base.BaseFragment
import com.stcodesapp.documentscanner.constants.Tags
import com.stcodesapp.documentscanner.database.entities.Image
import com.stcodesapp.documentscanner.helpers.getPolygonFromCropAreaJson
import com.stcodesapp.documentscanner.helpers.isValidPolygon
import com.stcodesapp.documentscanner.scanner.getWarpedImage
import com.stcodesapp.documentscanner.ui.helpers.DialogHelper
import com.theartofdev.edmodo.cropper.CropImageView
import com.theartofdev.edmodo.cropper.Polygon
import kotlinx.android.synthetic.main.crop_image_single_item_fragment.*
import java.io.File
import javax.inject.Inject

class CropImageSingleItemFragment : BaseFragment() {

    companion object {
        private const val TAG = "CropImageSingleItemFrag"
        fun newInstance(image : Image, imagePosition : Int) : CropImageSingleItemFragment
        {
            val fragment = CropImageSingleItemFragment()
            val args = Bundle()
            args.putSerializable(Tags.SERIALIZED_IMAGE,image)
            args.putInt(Tags.IMAGE_POSITION,imagePosition)
            fragment.arguments = args
            return fragment
        }
    }

    interface Listener{
        fun onItemDeleted(position : Int)
    }

    @Inject lateinit var viewModel: CropImageSingleItemViewModel
    var listener : Listener? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        activityComponent.inject(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
    {
        return inflater.inflate(R.layout.crop_image_single_item_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?)
    {
        super.onViewCreated(view, savedInstanceState)
        initUI()

    }

    private fun initUI()
    {
        val serializedImage = arguments?.getSerializable(Tags.SERIALIZED_IMAGE) as Image?
        if(serializedImage != null)
        {
            viewModel.chosenImage = serializedImage
            cropImageView.setImageUriAsync(Uri.fromFile(File(serializedImage.path)))
            cropImageView.setOnSetImageUriCompleteListener { view, uri, error ->
                setSavedValue(serializedImage)
            }

            cropImageView.setOnCropWindowChangedListener {
                saveUpdatedCropArea()
            }
        }
        val imagePosition = arguments?.getInt(Tags.IMAGE_POSITION)
        viewModel.chosenImagePosition = imagePosition ?: -1
    }

    private fun setSavedValue(serializedImage: Image)
    {
        setSavedCropArea(serializedImage)
        setSavedRotation()
    }

    private fun setSavedCropArea(serializedImage: Image)
    {
        val savedCropArea = serializedImage.cropArea
        if (!savedCropArea.isNullOrEmpty())
        {
            val polygon = getPolygonFromCropAreaJson(savedCropArea)
            if(isValidPolygon(polygon))
            {
                cropImageView.cropPolygon = polygon
            }
        }
        cropImageView.guidelines = CropImageView.Guidelines.OFF
    }

    fun saveUpdatedCropArea()
    {
        messageTextView.visibility = View.VISIBLE
        viewModel.updateImageCropPolygon(getCropPolygon()).observe(viewLifecycleOwner, Observer {
            messageTextView.visibility = View.GONE
        })
    }

    fun getCropPolygon() : Polygon
    {
        return cropImageView.cropPolygon
    }

    fun rotateImage()
    {
        cropImageView.rotateImage(90)
        viewModel.updateImageRotationAngle(cropImageView.rotatedDegrees.toDouble())
    }

    fun cropImage()
    {
        val srcBitmap = cropImageView.bitmap
        val dstBitmap = Bitmap.createBitmap(420,596, Bitmap.Config.ARGB_8888) //srcBitmap.copy(srcBitmap.config,true)
        Log.e(TAG, "grayScaleCurrentImage: BitmapWidth : ${srcBitmap.width} BitmapHeight : ${srcBitmap.height}")
        Log.e(TAG, "grayScaleCurrentImage: cropPolygon : ${cropImageView.cropPolygon}")
        Log.e(TAG, "grayScaleCurrentImage: imageViewWidth : ${cropImageView.width}, imageViewHeight : ${cropImageView.height}")
        Log.e(TAG, "grayScaleCurrentImage: overlayWidth : ${cropImageView.getmCropOverlayView().width}, overlayHeight : ${cropImageView.getmCropOverlayView().width}")
        getWarpedImage(srcBitmap, dstBitmap,cropImageView.cropPolygonByRation)
        cropImageView.setImageBitmap(dstBitmap,false)
        cropImageView.isShowCropOverlay = false
        setCroppedFlag(true)
    }

    fun setCroppedFlag(flag : Boolean)
    {
        viewModel.updateImageCroppedFlag(flag)
    }

    private fun setSavedRotation()
    {
        val savedRotationAngle = viewModel.chosenImage?.rotationAngle?.toInt()
        if(savedRotationAngle != null && savedRotationAngle > 0)
        {
             cropImageView.rotateImage(savedRotationAngle)
        }
    }
}