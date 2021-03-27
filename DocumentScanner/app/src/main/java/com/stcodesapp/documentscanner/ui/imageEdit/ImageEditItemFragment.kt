package com.stcodesapp.documentscanner.ui.imageEdit

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import com.google.gson.Gson
import com.stcodesapp.documentscanner.R
import com.stcodesapp.documentscanner.base.BaseFragment
import com.stcodesapp.documentscanner.constants.ConstValues.Companion.A4_PAPER_HEIGHT
import com.stcodesapp.documentscanner.constants.ConstValues.Companion.A4_PAPER_WIDTH
import com.stcodesapp.documentscanner.constants.Tags
import com.stcodesapp.documentscanner.database.entities.Image
import com.stcodesapp.documentscanner.helpers.getPolygonFromCropAreaJson
import com.stcodesapp.documentscanner.helpers.isValidPolygon
import com.stcodesapp.documentscanner.models.CustomFilter
import com.stcodesapp.documentscanner.models.Filter
import com.stcodesapp.documentscanner.models.PaperEffectFilter
import com.stcodesapp.documentscanner.scanner.getPaperEffectImage
import com.stcodesapp.documentscanner.scanner.getWarpedImage
import com.stcodesapp.documentscanner.scanner.getCustomBrightnessAndContrastImage
import com.theartofdev.edmodo.cropper.CropImageView
import com.theartofdev.edmodo.cropper.Polygon
import kotlinx.android.synthetic.main.image_edit_item_fragment.*
import java.io.File
import javax.inject.Inject

class ImageEditItemFragment : BaseFragment() {

    companion object {
        private const val TAG = "CropImageSingleItemFrag"
        fun newInstance(image : Image, imagePosition : Int) : ImageEditItemFragment
        {
            val fragment = ImageEditItemFragment()
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

    interface ImageLoadListener{
        fun onImageBitmapLoaded(imageBitmap: Bitmap)
    }

    @Inject lateinit var viewModel: ImageEditItemViewModel
    var listener : Listener? = null
    var imageLoadListener : ImageLoadListener? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        activityComponent.inject(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
    {
        return inflater.inflate(R.layout.image_edit_item_fragment, container, false)
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
            viewModel.chosenImageId = serializedImage.id
            cropImageView.setImageUriAsync(Uri.fromFile(File(serializedImage.path)))
            cropImageView.setOnSetImageUriCompleteListener { view, uri, error ->
                setSavedValue(serializedImage)
                imageLoadListener?.onImageBitmapLoaded(view.bitmap)
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
        Log.e(TAG, "setSavedValue: called")
        setSavedCropArea(serializedImage)
        setSavedFilter(serializedImage)
        setSavedRotation()
        //applySavedCustomFilter(serializedImage)
        //applySavedPaperEffect(serializedImage)
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
                cropImageFromSavedValue(polygon)
            }
        }
        cropImageView.guidelines = CropImageView.Guidelines.OFF
    }

    private fun setSavedFilter(serializedImage: Image)
    {

        val savedFilterName = serializedImage.filterName
        if (!savedFilterName.isNullOrEmpty())
        {
            val srcBitmap = cropImageView.bitmap
            val dstBitmap = srcBitmap.copy(srcBitmap.config, true)
            viewModel.applySavedFilter(serializedImage,srcBitmap,dstBitmap)
            cropImageView.setImageBitmap(dstBitmap,false)
            cropImageView.isShowCropOverlay = false
        }
    }



    fun applySavedCustomFilter(serializedImage: Image)
    {
        if(!serializedImage.customFilterJson.isNullOrEmpty())
        {
            val customFilter = Gson().fromJson(serializedImage.customFilterJson,CustomFilter::class.java)
            val srcBitmap = cropImageView.bitmap
            val dstBitmap = srcBitmap.copy(srcBitmap.config, true)
            getCustomBrightnessAndContrastImage(srcBitmap, dstBitmap, customFilter.brightnessValue, customFilter.contrastValue)
            cropImageView.setImageBitmap(dstBitmap, false)
            cropImageView.isShowCropOverlay = false
        }
    }


    fun applySavedPaperEffect(serializedImage: Image)
    {
        if(!serializedImage.paperEffectJson.isNullOrEmpty())
        {
            val paperEffect = Gson().fromJson(serializedImage.paperEffectJson,PaperEffectFilter::class.java)
            applyPaperEffect(paperEffect.blockSize,paperEffect.c,cropImageView.bitmap)
        }
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
        return cropImageView.cropPolygonByRation
    }

    fun rotateImage()
    {
        cropImageView.rotateImage(90)
        viewModel.updateImageRotationAngle(cropImageView.rotatedDegrees.toDouble())
    }

    fun cropImage()
    {
        val srcBitmap = cropImageView.bitmap
        val dstBitmap = Bitmap.createBitmap(A4_PAPER_WIDTH,A4_PAPER_HEIGHT, Bitmap.Config.ARGB_8888)
        getWarpedImage(srcBitmap, dstBitmap,cropImageView.cropPolygonByRation)
        cropImageView.setImageBitmap(dstBitmap,false)
        viewModel.saveImageCropData(dstBitmap)
        cropImageView.isShowCropOverlay = false
        setCroppedFlag(true)
    }

    fun applyFilter(filter : Filter)
    {
        viewModel.applyFilterToCurrentImage(filter).observe(viewLifecycleOwner,
            Observer {
                cropImageView.setImageBitmap(it,false)
                setSavedRotation()
            })
    }

    fun applyBrightnessAndContrast(brightnessValue: Int, contrastValue: Float, srcBitmap: Bitmap?) {
        if(srcBitmap != null)
        {
            viewModel.applyBrightnessAndContrast(brightnessValue,contrastValue,srcBitmap).observe(viewLifecycleOwner,
                Observer {
                    cropImageView.setImageBitmap(it, false)
                    cropImageView.isShowCropOverlay = false
                })
        }

    }

    fun applyPaperEffect(blockSize: Int, c: Double, originalBitmap: Bitmap) {
        val srcBitmap = originalBitmap
        val dstBitmap = srcBitmap.copy(srcBitmap.config,true)
        var finalBlockSize = blockSize
        var finalC = c
        if(finalBlockSize % 2 == 0 ) finalBlockSize += 1
        getPaperEffectImage(srcBitmap, dstBitmap,finalBlockSize,finalC)
        cropImageView.setImageBitmap(dstBitmap,false)
        cropImageView.isShowCropOverlay = false
    }

    fun cropImageFromSavedValue(cropPolygon: Polygon)
    {
        val srcBitmap = cropImageView.bitmap
        val dstBitmap = Bitmap.createBitmap(A4_PAPER_WIDTH,A4_PAPER_HEIGHT, Bitmap.Config.ARGB_8888)
        getWarpedImage(srcBitmap, dstBitmap,cropPolygon)
        cropImageView.setImageBitmap(dstBitmap,false)
        cropImageView.isShowCropOverlay = false
    }

    fun setCroppedFlag(flag : Boolean)
    {
        viewModel.updateImageCroppedFlag(flag)
    }

    fun isImageCropped() : Boolean
    {
        return viewModel.chosenImage?.isCropped == true
    }

    private fun setSavedRotation()
    {
        val savedRotationAngle = viewModel.chosenImage?.rotationAngle?.toInt()
        Log.e(TAG, "setSavedRotation: savedAngle : $savedRotationAngle")
        if(savedRotationAngle != null && savedRotationAngle > 0)
        {
             cropImageView.rotateImage(savedRotationAngle)
        }
    }

    fun getImageBitmap() : Bitmap?
    {
        return cropImageView.bitmap
    }
}