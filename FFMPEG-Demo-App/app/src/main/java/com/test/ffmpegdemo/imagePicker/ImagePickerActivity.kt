package com.test.ffmpegdemo.imagePicker

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.documentfile.provider.DocumentFile
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.test.ffmpegdemo.R
import com.test.ffmpegdemo.helper.SharedPrefHelper
import kotlinx.android.synthetic.main.activity_image_picker.*
import kotlin.math.min


class ImagePickerActivity : AppCompatActivity()
{

    private val viewModel by lazy {
        ViewModelProvider(this).get(ImagePickerViewModel::class.java)
    }

    companion object{
        const val REQUEST_CODE_IMAGE = 12
        const val REQUEST_PERSISTABLE_STORAGE_PERMISSION = 13
        private const val TAG = "ImagePickerActivity"
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image_picker)
        init()
    }

    private fun init()
    {
        saveImageButton.setOnClickListener {
            saveImageToExternalStorage()
        }

        pickImageButton.setOnClickListener {
            pickImage()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.e(TAG, "onActivityResult: Called For ReqCode : $requestCode")
        if (resultCode == Activity.RESULT_OK)
        {
            when(requestCode)
            {
                REQUEST_CODE_IMAGE -> showImageInView(data)
                REQUEST_PERSISTABLE_STORAGE_PERMISSION -> grantPersistableStorageUriPermission(data)
            }
        }
        else
        {
            Log.e(TAG, "onActivityResult: GoogleSignInResult $resultCode")
        }
    }

    private fun showImageInView(data: Intent?) {
        if (data != null) {
            val chosenImageUri = data.data
            Log.e(TAG, "onActivityResult: chosenImageUri : $chosenImageUri")
            viewModel.chosenFileUri = chosenImageUri
            Glide.with(this)
                .load(chosenImageUri)
                .into(imageView)
        }
    }

    private fun pickImage()
    {
        startActivityForResult(Intent.createChooser(
                Intent(Intent.ACTION_GET_CONTENT)
                .setType("image/*"), "Choose Video"), REQUEST_CODE_IMAGE)
    }

    private fun saveImageToExternalStorage()
    {
        if(viewModel.chosenFileUri != null)
        {
            val sharedPrefHelper = SharedPrefHelper(this)
            val grantedUri = sharedPrefHelper.getPersistableStorageUri()
            if(grantedUri == null)
            {
                //invoke persistable storage uri permission
                showPersistableStoragePermissionDialog()
            }
            else
            {
                val grantedDir = DocumentFile.fromTreeUri(this, grantedUri)
                val fileType = contentResolver.getType(viewModel.chosenFileUri!!)
                if(grantedDir != null && fileType != null && fileType.startsWith("image/"))
                {
                    var imageDirectory : DocumentFile? = null
                    val allFiles = grantedDir.listFiles()
                    Log.e(TAG, "saveImageToExternalStorage: allFileSize : ${allFiles.size} grantedDir : ${grantedDir.name}")
                    for(file in allFiles)
                    {
                        Log.e(TAG, "saveImageToExternalStorage: allFiles : ${file.name}, isDir : ${file.isDirectory}")
                        if(file.isDirectory && file.name == "Image")
                        {
                            imageDirectory = file
                            break
                        }
                    }
                    if(imageDirectory == null)
                    {
                        imageDirectory = grantedDir.createDirectory("Image")
                    }
                    val imageFile = imageDirectory?.createFile(fileType,"image_${System.currentTimeMillis()}.png")

                    if(imageFile != null)
                    {
                        val inputStream = contentResolver.openInputStream(viewModel.chosenFileUri!!)
                        val outputStream = contentResolver.openOutputStream(imageFile.uri)

                        val maxBufferSize = 8 * 1024 * 1024
                        val bytesAvailable = inputStream!!.available()
                        val bufferSize = min(bytesAvailable, maxBufferSize)
                        val buffer = ByteArray(bufferSize)

                        var len = 0
                        while (inputStream.read(buffer).also {
                                len = it
                            } != -1) {
                            outputStream?.write(buffer, 0, len)
                        }
                        Log.e(TAG, "saveImageToExternalStorage: done. Saved at : ${imageFile.parentFile?.name}/${imageFile.name}")
                    }
                }
                //already have a persistable storage uri permission
            }


        }
    }

    private fun requestPersistableStorageUriPermission()
    {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE).apply {
            flags = Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION or
                    Intent.FLAG_GRANT_READ_URI_PERMISSION }
        startActivityForResult(intent, REQUEST_PERSISTABLE_STORAGE_PERMISSION)
    }

    private fun showPersistableStoragePermissionDialog()
    {
        AlertDialog.Builder(this)
            .setMessage("Enable persistable storage permission?")
            .setPositiveButton("Yes") { dialog, which ->
                requestPersistableStorageUriPermission()
                dialog.dismiss()
            }
            .setNegativeButton("No"){
                dialog, which ->
                dialog.dismiss()
            }
            .show()
    }

    private fun grantPersistableStorageUriPermission(data: Intent?)
    {
        if(data != null)
        {
            val grantedUri = data.data
            if(grantedUri != null)
            {
                contentResolver.takePersistableUriPermission(grantedUri, Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
                val grantedDir = DocumentFile.fromTreeUri(this, grantedUri)
                SharedPrefHelper(this).setPersistableStorageUri(grantedUri)
                saveImageToExternalStorage()
            }
        }


    }
}