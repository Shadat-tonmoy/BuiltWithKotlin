package com.stcodesapp.documentscanner.ui.imageCrop

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
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
            if(serializedImage.isCropped)
            {
                cropImageFromSavedValue(cropImageView.cropPolygonByRation)
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
        val dstBitmap = Bitmap.createBitmap(420,596, Bitmap.Config.ARGB_8888)
        getWarpedImage(srcBitmap, dstBitmap,cropImageView.cropPolygonByRation)
        cropImageView.setImageBitmap(dstBitmap,false)
        viewModel.saveImageCropData(dstBitmap)
        cropImageView.isShowCropOverlay = false
        setCroppedFlag(true)
    }

    fun cropImageFromSavedValue(cropPolygon: Polygon)
    {
        val srcBitmap = cropImageView.bitmap
        val dstBitmap = Bitmap.createBitmap(420,596, Bitmap.Config.ARGB_8888)
        getWarpedImage(srcBitmap, dstBitmap,cropPolygon)
        cropImageView.setImageBitmap(dstBitmap,false)
        cropImageView.isShowCropOverlay = false
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

    fun getImageBitmap() : Bitmap
    {
        return cropImageView.bitmap
    }
}