package com.test.simplecanvas

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity()
{

    companion object{
        private const val OFFSET = 60
        private const val MULTIPLIER = 100
    }


    private var mCanvas : Canvas? = null
    private val mPaint = Paint()
    private val mPaintText = Paint(Paint.UNDERLINE_TEXT_FLAG)
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

    override fun onCreateOptionsMenu(menu: Menu?): Boolean
    {
        menuInflater.inflate(R.menu.main_menu,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId)
        {
            R.id.draw_on_canvas -> openDrawingOnCanvasScreen()
            R.id.draw_shape -> openDrawShapeScreen()
        }
        return super.onOptionsItemSelected(item)
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
        mPaintText.isAntiAlias = true

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
                mPaint.color = mColorRectangle - MULTIPLIER * mOffset
                mRect.set(mOffset,mOffset,vWidth - mOffset, vHeight - mOffset)
                mCanvas?.drawRect(mRect,mPaint)
                mOffset += OFFSET
                view.invalidate()

            }
            else
            {
                mPaint.color = mColorAccent
                mCanvas!!.drawCircle(halfWidth.toFloat(), halfHeight.toFloat(), halfWidth / 3.toFloat(), mPaint)
                val text = getString(R.string.done)
                // Get bounding box for text to calculate where to draw it.
                mPaintText.getTextBounds(text, 0, text.length, mBounds)
                // Calculate x and y for text so it's centered.
                val x = halfWidth - mBounds.centerX()
                val y = halfHeight - mBounds.centerY()
                mCanvas!!.drawText(text, x.toFloat(), y.toFloat(), mPaintText)
                view.invalidate()
            }
        }

    }

    private fun openDrawingOnCanvasScreen()
    {
        val intent = Intent(this,DrawingOnCanvas::class.java)
        startActivity(intent)
    }

    private fun openDrawShapeScreen()
    {
        val intent = Intent(this,DrawShapeActivity::class.java)
        startActivity(intent)
    }
}