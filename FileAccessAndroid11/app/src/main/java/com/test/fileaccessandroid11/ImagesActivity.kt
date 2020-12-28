package com.test.fileaccessandroid11

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.GridLayoutManager
import kotlinx.android.synthetic.main.activity_images.*

class ImagesActivity : AppCompatActivity()
{
    companion object{
        private const val TAG = "ImagesActivity"
    }
    private lateinit var adapter : ImageAdapter

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_images)
        initUI()
        fetchImageList()
    }

    private fun initUI()
    {
        adapter = ImageAdapter(this)
        imageListView.layoutManager = GridLayoutManager(this,2)
        imageListView.adapter = adapter
    }

    private fun fetchImageList()
    {
        val permissionHelper = PermissionHelper(this)
        if(permissionHelper.isReadStoragePermissionGranted())
        {
            val task = ImageFileFetchingTask(this, object : ImageFileFetchingTask.Listener{
                override fun onImageListFetched(imageURIList: List<Image>) {
                    Log.e(TAG, "onImageListFetched: $imageURIList")
                    adapter.setImages(imageURIList)
                }

            })
            task.fetchImages()
        }
    }
}