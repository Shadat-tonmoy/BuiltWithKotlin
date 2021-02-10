package com.test.simplecanvas

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.core.content.res.ResourcesCompat
import kotlin.math.abs

class CanvasView : View
{

    companion object{
        const val TOUCH_TOLERANCE = 4
    }
    private var mPaint : Paint? = null
    private var mPath : Path? = null
    private var mDrawColor : Int  = -1
    private var mBackgroundColor : Int = -1
    private var mExtraCanvas : Canvas? = null
    private var mExtraBitmap : Bitmap? = null
    private var mX = -1F
    private var mY = -1F



    constructor(context : Context) : super(context)
    {
        init(context,null)

    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    {
        init(context,attrs)
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)
    {

    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes)
    {

    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        if(canvas != null && mExtraBitmap != null)
        {
            canvas.drawBitmap(mExtraBitmap!!,0F,0F,null)
        }
    }

    private fun init(context: Context?, attrs: AttributeSet?)
    {
        mBackgroundColor = ResourcesCompat.getColor(resources,R.color.opaque_orange,null)
        mDrawColor = ResourcesCompat.getColor(resources,R.color.opaque_yellow,null)


        // Holds the path we are currently drawing.
        mPath = Path()
        // Set up the paint with which to draw.
        mPaint = Paint()
        mPaint?.color = mDrawColor
        // Smoothes out edges of what is drawn without affecting shape.
        mPaint?.isAntiAlias = true

        // Dithering affects how colors with higher-precision device
        // than the are down-sampled.
        mPaint?.isDither = true
        mPaint?.style = Paint.Style.STROKE
        mPaint?.strokeJoin = Paint.Join.ROUND
        mPaint?.strokeCap = Paint.Cap.ROUND
        mPaint?.strokeWidth = 12F
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        mExtraBitmap = Bitmap.createBitmap(width,height,Bitmap.Config.ARGB_8888)
        mExtraCanvas = Canvas(mExtraBitmap!!)
        mExtraCanvas?.drawColor(mBackgroundColor)

    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {

        if(event != null)
        {
            val x = event.x
            val y = event.y

            when(event.action)
            {
                MotionEvent.ACTION_DOWN -> touchStart(x,y)
                MotionEvent.ACTION_MOVE -> {
                    touchMove(x,y)
                    invalidate()
                }
                MotionEvent.ACTION_UP -> touchEnd(x,y)
            }
        }
        return true
    }

    private fun touchStart(x : Float, y : Float)
    {
        mPath?.moveTo(x,y)
        mX = x
        mY = y
    }

    private fun touchEnd(x : Float, y : Float)
    {
        mPath?.reset()

    }

    private fun touchMove(x : Float, y : Float)
    {
        val dx = abs(x - mX)
        val dy = abs(y - mY)

        if(dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE)
        {
            mPath?.quadTo(mX,mY,(x+mX)/2,(y+mY)/2)

            mX = x
            mY = y

            mExtraCanvas?.drawPath(mPath!!,mPaint!!)
        }

    }



}