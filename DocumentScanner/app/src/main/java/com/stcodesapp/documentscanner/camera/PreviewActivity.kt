package com.stcodesapp.documentscanner.camera

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.Glide
import com.stcodesapp.documentscanner.R
import kotlinx.android.synthetic.main.activity_preview.*

class PreviewActivity : AppCompatActivity()
{
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_preview)
        showPreview()

    }

    private fun showPreview()
    {
        if(intent.hasExtra("image"))
        {
            val imagePath = intent.getStringExtra("image")
            Glide.with(this)
                .load(imagePath)
                .into(imagePreview)
        }
    }
}