package com.test.simplecanvas

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity()
{

    companion object{
        private const val OFFSET = 120
        private const val MULTIPLIER = 100
    }


    private var mCanvas : Canvas? = null
    private val mPaint = Paint()
    private val mPaintText = Paint(Paint.ANTI_ALIAS_FLAG)
    private var mBitmap : Bitmap? = null
    private var mImageView: ImageView? = null
    private val mRect: Rect = Rect()
    private val mBounds: Rect = Rect()
    private var mOffset = OFFSET
    private var mColorBackground = 0
    private var mColorRectangle = 0
    private var mColorAccent = 0



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        init()
    }

    private fun init()
    {
        imageView.setOnClickListener {
            drawOnCanvas(it)
        }

        mColorBackground = ResourcesCompat.getColor(resources,
                R.color.colorBackground, null)
        mColorRectangle = ResourcesCompat.getColor(resources,
                R.color.colorRectangle, null)
        mColorAccent = ResourcesCompat.getColor(resources,
                R.color.colorAccent, null)

        mPaint.color = mColorBackground

        mPaintText.color = ResourcesCompat.getColor(resources,
                R.color.colorPrimaryDark, null);
        mPaintText.textSize = 70F

        mImageView = findViewById(R.id.imageView)

    }

    private fun drawOnCanvas(view : View)
    {
        val vWidth = view.width
        val vHeight = view.height
        val halfWidth = vWidth / 2
        val halfHeight = vHeight / 2
        if(mOffset == OFFSET)
        {
            mBitmap = Bitmap.createBitmap(vWidth, vHeight, Bitmap.Config.ARGB_8888)
            mImageView?.setImageBitmap(mBitmap)
            mCanvas = Canvas(mBitmap!!)
            mCanvas?.drawColor(mColorBackground)
            mCanvas?.drawText(getString(R.string.keep_tapping),100F,100F,mPaintText)
            mOffset += OFFSET
            view.invalidate()

        }
        else
        {
            if(mOffset < halfWidth && mOffset < halfHeight)
            {

            }
            else
            {

            }
        }

    }
}