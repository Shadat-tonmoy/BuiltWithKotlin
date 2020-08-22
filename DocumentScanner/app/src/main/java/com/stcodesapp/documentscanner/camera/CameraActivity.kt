package com.stcodesapp.documentscanner.camera

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.ImageFormat
import android.graphics.SurfaceTexture
import android.hardware.camera2.*
import android.media.Image
import android.media.ImageReader
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.HandlerThread
import android.util.DisplayMetrics
import android.util.Log
import android.util.Size
import android.util.SparseIntArray
import android.view.Surface
import android.view.TextureView
import android.widget.FrameLayout
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.core.app.ActivityCompat
import com.stcodesapp.documentscanner.R
import com.stcodesapp.documentscanner.constants.RequestCode.Companion.REQUEST_CAMERA_PERMISSION
import kotlinx.android.synthetic.main.activity_camera.*
import java.io.*
import java.util.*

class CameraActivity : AppCompatActivity() {
    companion object{
        private const val TAG = "MainActivity"
    }

    private val orientation = SparseIntArray()
    private lateinit var cameraID : String
    private lateinit var cameraDevice : CameraDevice
    private lateinit var cameraCaptureSession: CameraCaptureSession
    private lateinit var captureRequest: CaptureRequest
    private lateinit var captureRequestBuilder: CaptureRequest.Builder
    private lateinit var imageDimension: Size
    private lateinit var imageReader: ImageReader
    private lateinit var savedImageFile: File
    private var mBackgroundHandler: Handler? = null
    private var mBackgroundThread: HandlerThread? = null

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera)
        initOrientation()
        initClickListener()
    }

    override fun onResume() {
        super.onResume()
        startBackgroundThread()
        if (textureView.isAvailable) openCamera() else textureView.surfaceTextureListener =
            textureListener
    }

    override fun onPause() {
        stopBackgroundThread()
        super.onPause()
    }

    private fun initOrientation()
    {
        orientation.append(Surface.ROTATION_0,90)
        orientation.append(Surface.ROTATION_90,0)
        orientation.append(Surface.ROTATION_180,270)
        orientation.append(Surface.ROTATION_270,180)
    }

    private fun initClickListener()
    {
        textureView.surfaceTextureListener = textureListener
        openCameraButton.setOnClickListener { takePicture() }

    }

    private fun takePicture()
    {
        if (cameraDevice == null) return
        val manager =
            getSystemService(Context.CAMERA_SERVICE) as CameraManager
        try {
            val characteristics =
                manager.getCameraCharacteristics(cameraDevice.id)
            var jpegSizes: Array<Size>? = null
            if (characteristics != null) jpegSizes = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP)!!.getOutputSizes(
                ImageFormat.JPEG)

            //Capture image with custom size
            var width = 1280
            var height = 960
            /*if (jpegSizes != null && jpegSizes.size > 0) {
                width = jpegSizes[0].width
                height = jpegSizes[0].height
            }*/
            Log.e(TAG, "takePicture: Width : $width and Height : $height")
            val reader =
                ImageReader.newInstance(width, height, ImageFormat.JPEG, 1)
            val outputSurface: MutableList<Surface> =
                ArrayList(2)
            outputSurface.add(reader.surface)
            outputSurface.add(Surface(textureView.surfaceTexture))
            val captureBuilder =
                cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE)
            captureBuilder.addTarget(reader.surface)
            captureBuilder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO)

            //Check orientation base on device
            val rotation = windowManager.defaultDisplay.rotation
            captureBuilder.set(
                CaptureRequest.JPEG_ORIENTATION,
                orientation.get(rotation)
            )
            savedImageFile = File(
                Environment.getExternalStorageDirectory()
                    .toString() + "/" + UUID.randomUUID().toString() + ".jpg"
            )
            val readerListener: ImageReader.OnImageAvailableListener = object :
                ImageReader.OnImageAvailableListener {
                override fun onImageAvailable(imageReader: ImageReader) {
                    var image: Image? = null
                    try {
                        image = reader.acquireLatestImage()
                        val buffer = image.planes[0].buffer
                        val bytes = ByteArray(buffer.capacity())
                        buffer[bytes]
                        save(bytes)
                    } catch (e: FileNotFoundException) {
                        e.printStackTrace()
                    } catch (e: IOException) {
                        e.printStackTrace()
                    } finally {
                        run { image?.close() }
                    }
                }

                @Throws(IOException::class)
                private fun save(bytes: ByteArray) {
                    var outputStream: OutputStream? = null
                    try {
                        outputStream = FileOutputStream(savedImageFile)
                        outputStream!!.write(bytes)
                    } finally {
                        outputStream?.close()
                    }
                }
            }
            reader.setOnImageAvailableListener(readerListener, mBackgroundHandler)
            val captureListener: CameraCaptureSession.CaptureCallback = object : CameraCaptureSession.CaptureCallback() {
                override fun onCaptureCompleted(@NonNull session: CameraCaptureSession, @NonNull request: CaptureRequest, @NonNull result: TotalCaptureResult) {
                    super.onCaptureCompleted(session, request, result)
                    Toast.makeText(this@CameraActivity, "Saved $savedImageFile", Toast.LENGTH_SHORT).show()
                    createCameraPreview()
                    moveToPreviewScreen()
                }
            }

            cameraDevice.createCaptureSession(outputSurface, object : CameraCaptureSession.StateCallback()
            {
                override fun onConfigured(@NonNull cameraCaptureSession: CameraCaptureSession) {
                    try {
                        cameraCaptureSession.capture(
                            captureBuilder.build(),
                            captureListener,
                            mBackgroundHandler
                        )
                    } catch (e: CameraAccessException) {
                        e.printStackTrace()
                    }
                }

                override fun onConfigureFailed(@NonNull cameraCaptureSession: CameraCaptureSession) {}
            },
                mBackgroundHandler
            )
        } catch (e: CameraAccessException) {
            e.printStackTrace()
        }

    }

    private fun moveToPreviewScreen()
    {
        val intent = Intent(this,PreviewActivity::class.java)
        intent.putExtra("image",savedImageFile.absolutePath)
        startActivity(intent)

    }

    private fun openCamera()
    {
        val manager = getSystemService(Context.CAMERA_SERVICE) as CameraManager
        try {
            cameraID = manager.cameraIdList[0]
            val characteristics = manager.getCameraCharacteristics(cameraID)
            val map = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP)!!
            imageDimension = map.getOutputSizes(SurfaceTexture::class.java)[0]
            setAspectRatioTextureView(imageDimension.height,imageDimension.width)
            //Check realtime permission if run higher API 23
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
            {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE), REQUEST_CAMERA_PERMISSION)
                return
            }
            manager.openCamera(cameraID, stateCallback, null)
        } catch (e: CameraAccessException) {
            e.printStackTrace()
        }
    }

    private var stateCallback: CameraDevice.StateCallback = object : CameraDevice.StateCallback() {
        override fun onOpened(@NonNull camera: CameraDevice)
        {
            cameraDevice = camera
            createCameraPreview()
        }

        override fun onDisconnected(@NonNull cameraDevice: CameraDevice) {
            cameraDevice.close()
        }

        override fun onError(@NonNull cameraDevice: CameraDevice, i: Int) {
            var device: CameraDevice? = cameraDevice
            device!!.close()
            device = null
        }
    }

    private fun createCameraPreview()
    {
        try {
            val texture = textureView.surfaceTexture!!
            texture.setDefaultBufferSize(imageDimension.width, imageDimension.height)
            val surface = Surface(texture)
            captureRequestBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW)
            captureRequestBuilder.addTarget(surface)
            cameraDevice.createCaptureSession(
                Arrays.asList(surface),
                object : CameraCaptureSession.StateCallback() {
                    override fun onConfigured(@NonNull session: CameraCaptureSession)
                    {
                        if (cameraDevice == null) return
                        cameraCaptureSession = session
                        updatePreview()
                    }

                    override fun onConfigureFailed(@NonNull cameraCaptureSession: CameraCaptureSession) {
                        Toast.makeText(this@CameraActivity, "Changed", Toast.LENGTH_SHORT).show()
                    }
                },
                null
            )
        } catch (e: CameraAccessException) {
            e.printStackTrace()
        }
    }

    private fun updatePreview() {
        if (cameraDevice == null) Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show()
        captureRequestBuilder.set(
            CaptureRequest.CONTROL_MODE,
            CaptureRequest.CONTROL_MODE_AUTO
        )
        try {
            cameraCaptureSession.setRepeatingRequest(
                captureRequestBuilder.build(),
                null,
                mBackgroundHandler
            )
        } catch (e: CameraAccessException) {
            e.printStackTrace()
        }
    }


    private fun stopBackgroundThread()
    {
        mBackgroundThread?.quitSafely()
        try {
            mBackgroundThread?.join()
            mBackgroundThread = null
            mBackgroundHandler = null
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
    }

    private fun startBackgroundThread()
    {
        mBackgroundThread = HandlerThread("Camera Background")
        mBackgroundThread?.start()
        mBackgroundThread?.let { mBackgroundHandler = Handler(it.looper) }

    }

    private var textureListener: TextureView.SurfaceTextureListener = object :
        TextureView.SurfaceTextureListener {
        override fun onSurfaceTextureAvailable(surfaceTexture: SurfaceTexture, i: Int, i1: Int) { openCamera() }

        override fun onSurfaceTextureSizeChanged(surfaceTexture: SurfaceTexture, i: Int, i1: Int) {}

        override fun onSurfaceTextureDestroyed(surfaceTexture: SurfaceTexture): Boolean { return false }

        override fun onSurfaceTextureUpdated(surfaceTexture: SurfaceTexture) {}
    }

    private fun setAspectRatioTextureView(ResolutionWidth: Int, ResolutionHeight: Int)
    {
        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        val displayHeight = displayMetrics.heightPixels
        val displayWidth = displayMetrics.widthPixels
        Log.e(TAG, "setAspectRatioTextureView: displayWidth : $displayWidth, displayHeight : $displayHeight")

        if (ResolutionWidth > ResolutionHeight)
        {
            val newWidth: Int = displayWidth
            val newHeight: Int = ((displayWidth * ResolutionWidth)/ResolutionHeight)
            updateTextureViewSize(newWidth, newHeight)
        }
        else
        {
            val newWidth: Int = displayWidth
            val newHeight: Int = ((displayWidth * ResolutionHeight)/ResolutionWidth);
            updateTextureViewSize(newWidth, newHeight)
        }
    }

    private fun updateTextureViewSize(viewWidth: Int, viewHeight: Int) {
        Log.e(TAG, "TextureView Width : $viewWidth TextureView Height : $viewHeight")
        textureView.layoutParams = FrameLayout.LayoutParams(viewWidth, viewHeight)
    }
}