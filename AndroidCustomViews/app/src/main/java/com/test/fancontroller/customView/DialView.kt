package com.test.fancontroller.customView

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import com.test.fancontroller.R

class DialView : View {

    companion object {
        private const val SELECTION_COUNT = 4
    }

    private var mWidth = 0f
    private var mHeight = 0f
    private var mTextPaint: Paint? = null
    private var mDialPaint : Paint? = null
    private var mRectPaint : Paint? = null
    private var mRadius = 0f
    private var mActiveSelection = 0
    private var mFanOffColor = Color.GRAY
    private var mFanOnColor = Color.CYAN


    private val mTempLabel = StringBuffer(8)
    private val mTempResult = FloatArray(2)

    constructor(context: Context) : super(context)
    {
        init(context,null)
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    {
        init(context,attrs)
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)
    {
        init(context,attrs)
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int ) : super(context, attrs, defStyleAttr, defStyleRes)
    {
        init(context,attrs)
    }

    private fun init(context: Context?, attrs: AttributeSet?)
    {
        mFanOffColor = Color.GRAY
        mFanOnColor = Color.CYAN

        setColorFromAttrs(attrs)

        mTextPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mTextPaint?.color = Color.BLACK
        mTextPaint?.style = Paint.Style.FILL_AND_STROKE
        mTextPaint?.textAlign = Paint.Align.CENTER
        mTextPaint?.textSize = 64f
        mDialPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mDialPaint?.color = mFanOffColor
        mActiveSelection = 0

        mRectPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mRectPaint?.color = Color.BLACK
        mRectPaint?.style = Paint.Style.STROKE

        setOnClickListener {
            mActiveSelection = (mActiveSelection + 1) % SELECTION_COUNT;
            // Set dial background color to green if selection is >= 1.
            if (mActiveSelection >= 1) {
                mDialPaint?.color = mFanOnColor
            } else {
                mDialPaint?.color = mFanOffColor
            }
            invalidate()
        }


    }

    private fun setColorFromAttrs(attrs: AttributeSet?)
    {
        if(attrs != null)
        {
            val typedArray = context.obtainStyledAttributes(attrs, R.styleable.DialView,0,0)
            mFanOnColor = typedArray.getColor(R.styleable.DialView_fanOnColor,mFanOnColor)
            mFanOffColor = typedArray.getColor(R.styleable.DialView_fanOffColor,mFanOffColor)
            typedArray.recycle()
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        mWidth = w.toFloat()
        mHeight = h.toFloat()
        mRadius = (Math.min(mWidth, mHeight) / 2 * 0.8).toFloat()
    }

    private fun computeXYForPosition(pos: Int, radius: Float): FloatArray? {
        val result = mTempResult
        val startAngle = Math.PI * (9 / 8.0) // Angles are in radians.
        val angle = startAngle + pos * (Math.PI / 4)
        result[0] = (radius * Math.cos(angle)).toFloat() + mWidth / 2
        result[1] = (radius * Math.sin(angle)).toFloat() + mHeight / 2
        return result
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawCircle(mWidth/2, mHeight/2,mRadius, mDialPaint!!)
        //drawOutsideBorder(canvas)
        drawLabels(canvas)

    }

    private fun drawOutsideBorder(canvas: Canvas) {
        val left = (mWidth / 2) - mRadius
        val top = (mHeight / 2) - mRadius
        val right = 2 * mRadius + left
        val bottom = 2 * mRadius + top
        val rect = RectF(left, top, right, bottom)
        canvas.drawRect(rect, mRectPaint!!)
        canvas.drawText("Hello", mWidth / 2, mHeight / 2, mTextPaint!!)
    }

    private fun drawLabels(canvas: Canvas)
    {
        val labelRadius = mRadius + 20
        val label = mTempLabel
        for (i in 0 until SELECTION_COUNT) {
            val xyData = computeXYForPosition(i, labelRadius)
            val x = xyData!![0]
            val y = xyData!![1]
            label.setLength(0)
            label.append(i)
            canvas.drawText(label, 0, label.length, x, y, mTextPaint!!)
        }
        // Draw the indicator mark.
        // Draw the indicator mark.
        val markerRadius = mRadius - 35
        val xyData = computeXYForPosition(
            mActiveSelection,
            markerRadius
        )
        val x = xyData!![0]
        val y = xyData!![1]
        canvas.drawCircle(x, y, 20f, mTextPaint!!)

    }




}