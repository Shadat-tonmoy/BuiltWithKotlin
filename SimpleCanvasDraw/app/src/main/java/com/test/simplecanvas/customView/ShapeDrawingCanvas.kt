package com.test.simplecanvas.customView

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import androidx.core.content.res.ResourcesCompat
import com.test.simplecanvas.R
import kotlin.math.abs


class ShapeDrawingCanvas : View
{

    companion object{
        private const val TAG = "ShapeDrawingCanvas"
        private const val TOUCH_TOLERANCE = 4
        private const val RECT_STROKE_WIDTH = 25F

    }

    private var mBackgroundColor : Int = ResourcesCompat.getColor(resources,R.color.opaque_orange,null)

    private var mExtraBitmap : Bitmap? = null
    private var mExtraCanvas : Canvas? = null
    private var mRectPaint : Paint? = null
    private var mRect : Rect? = null
    private var mX = -1F
    private var mY = -1F
    private var mPath : Path? = null
    private var mPathBounds : RectF? = null
    private val polygonPoints = FloatArray(8)


    constructor(context: Context?) : super(context)
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

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int
    ) : super(context, attrs, defStyleAttr, defStyleRes)
    {
        init(context,attrs)
    }


    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        if(canvas != null)
        {
            canvas.drawBitmap(mExtraBitmap!!,0F,0F,null)
            //canvas.drawRect(mRect!!,mRectPaint!!)
        }

    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        mExtraBitmap = Bitmap.createBitmap(w,h,Bitmap.Config.ARGB_8888)
        mExtraCanvas = Canvas(mExtraBitmap!!)
        mExtraCanvas?.drawColor(mBackgroundColor)
    }

    private fun init(context: Context?, attrs: AttributeSet?)
    {
        mRectPaint = Paint()
        mRectPaint?.isAntiAlias = true
        mRectPaint?.style = Paint.Style.STROKE
        mRectPaint?.color = ResourcesCompat.getColor(resources,R.color.opaque_yellow,null)
        mRectPaint?.strokeWidth = RECT_STROKE_WIDTH

    }

    fun drawRect()
    {
        val offset = 50
        mRect = Rect(offset,offset,width-offset*5, height-offset*9)
        mExtraCanvas?.drawRect(mRect!!,mRectPaint!!)
        postInvalidate()
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean
    {
        if(event != null)
        {
            val x = event.x
            val y = event.y
            when(event.action)
            {
                MotionEvent.ACTION_UP -> {
                    touchStart(x,y)
                }
                MotionEvent.ACTION_MOVE -> {
                    Log.e(TAG, "onTouchEvent: actionMoved")
                    touchMove(x,y)
                    invalidate()
                }
                MotionEvent.ACTION_DOWN -> {
                    touchEnd(x,y)
                }

            }
        }

        return true
    }

    private fun touchStart(x : Float, y : Float)
    {
        Log.e(TAG, "touchStart: at x : $x, y : $y")
        mX = x
        mY = y

    }

    private fun touchMove(x : Float, y : Float)
    {
        val absDx = abs(x-mX)
        val absDy = abs(y-mY)
        if(mRect != null)
        {

            Log.e(TAG, "touchMove: withoutTolerance x : $x, y : $y rectLeft : ${mRect?.left}, rectRight : ${mRect?.right}, rectTop : ${mRect?.top}, rectBottom : ${mRect?.bottom}")
            when {
                isInsideRect(x,y) -> {
                    Log.e(TAG, "touchMove: insideRect" )
                    moveRect(x, y)
                }
                isLeftSide(x,y) -> {
                    Log.e(TAG, "touchMove: ResizeLeft")
                }
                isRightSide(x,y) -> {
                    resizeRectRight(x,y)
                    Log.e(TAG, "touchMove: ResizeRight")
                }
                isTopSide(x,y) -> {
                    Log.e(TAG, "touchMove: ResizeTop")
                }
                isBottomSide(x,y) -> {
                    Log.e(TAG, "touchMove: ResizeBottom")
                }
            }


            /*if(absDx > TOUCH_TOLERANCE || absDy > TOUCH_TOLERANCE)
            {
                Log.e(TAG, "touchMove: respectTolerance x : $x, y : $y rectLeft : ${mRect?.left}, rectRight : ${mRect?.right}, rectTop : ${mRect?.top}, rectBottom : ${mRect?.bottom}")
                when {
                    isInsideRect(x,y) -> {
                        Log.e(TAG, "touchMove: insideRect" )
                        moveRect(x, y)
                    }
                    isLeftSide(x,y) -> {
                        Log.e(TAG, "touchMove: ResizeLeft")
                    }
                    isRightSide(x,y) -> {
                        resizeRectRight(x,y)
                        Log.e(TAG, "touchMove: ResizeRight")
                    }
                    isTopSide(x,y) -> {
                        Log.e(TAG, "touchMove: ResizeTop")
                    }
                    isBottomSide(x,y) -> {
                        Log.e(TAG, "touchMove: ResizeBottom")
                    }
                }
            }
            else
            {
                Log.e(TAG, "touchMove: noTolerance x : $x, y : $y, rectLeft : ${mRect?.left}, rectRight : ${mRect?.right}, rectTop : ${mRect?.top}, rectBottom : ${mRect?.bottom}")
                when{
                    isLeftSide(x,y) -> {
                        Log.e(TAG, "touchMove: ResizeLeftNoTolerance")
                    }
                    isRightSide(x,y) -> {
                        resizeRectRight(x,y)
                        Log.e(TAG, "touchMove: ResizeRightNoTolerance")
                    }
                    isTopSide(x,y) -> {
                        Log.e(TAG, "touchMove: ResizeTopNoTolerance")
                    }
                    isBottomSide(x,y) -> {
                        Log.e(TAG, "touchMove: ResizeBottomNoTolerance")
                    }
                }
            }*/

        }

    }

    private fun moveRect(x: Float, y: Float)
    {
        val dx = x - mX
        val dy = y - mY

        val targetLeft = (mRect!!.left + dx).toInt()
        val targetRight = (mRect!!.right + dx).toInt()
        val targetTop = (mRect!!.top + dy).toInt()
        val targetBottom = (mRect!!.bottom + dy).toInt()

        if (targetTop > 0 && targetBottom < height && targetLeft > 0 && targetRight < width) {
            mRect!!.left = targetLeft
            mRect!!.right = targetRight
            mRect!!.top = targetTop
            mRect!!.bottom = targetBottom

            mExtraBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            mExtraCanvas = Canvas(mExtraBitmap!!)
            mExtraCanvas?.drawColor(mBackgroundColor)
            mExtraCanvas?.drawRect(mRect!!, mRectPaint!!)


            mX = x
            mY = y

        }
    }

    private fun resizeRectRight(x: Float, y: Float)
    {
        val dx = x - mX

        val targetRight = (mRect!!.right + dx).toInt()

        Log.e(TAG, "resizeRectRight: targetRight : $targetRight, width : $width")

        if (targetRight < width)
        {
            mRect!!.right = targetRight
            mExtraBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            mExtraCanvas = Canvas(mExtraBitmap!!)
            mExtraCanvas?.drawColor(mBackgroundColor)
            mExtraCanvas?.drawRect(mRect!!, mRectPaint!!)

            mX = x
            mY = y

        }
    }

    private fun touchEnd(x : Float, y : Float)
    {
        Log.e(TAG, "touchEnd: at x : $x, y : $y")
        mX = x
        mY = y

    }

    private fun isInsideRect(x : Float, y : Float) : Boolean
    {
        if(mRect != null)
        {
            return x > mRect!!.left && x < mRect!!.right && y > mRect!!.top && y < mRect!!.bottom
        }
        return false
    }

    private fun isLeftSide(x : Float, y : Float) : Boolean
    {
        if(mRect != null)
        {
            return x >= mRect!!.left &&  x <= mRect!!.left.toFloat() + RECT_STROKE_WIDTH
        }
        return false
    }

    private fun isRightSide(x : Float, y : Float) : Boolean
    {
        if(mRect != null)
        {
            return x >= mRect!!.right.toFloat() - RECT_STROKE_WIDTH &&  x <= mRect!!.right.toFloat() + RECT_STROKE_WIDTH
        }
        return false
    }

    private fun isTopSide(x : Float, y : Float) : Boolean
    {
        if(mRect != null)
        {
            return y == mRect!!.top.toFloat()
        }
        return false
    }

    private fun isBottomSide(x : Float, y : Float) : Boolean
    {
        if(mRect != null)
        {
            return y == mRect!!.bottom.toFloat()
        }
        return false
    }

    fun drawPath()
    {
        polygonPoints[0] = 150F
        polygonPoints[1] = 150F
        polygonPoints[2] = 475F
        polygonPoints[3] = 80F
        polygonPoints[4] = 400F
        polygonPoints[5] = 890F
        polygonPoints[6] = 100F
        polygonPoints[7] = 720F
        mPath = Path()
        mPath?.setLastPoint(polygonPoints[0], polygonPoints[1])
        mPath?.lineTo(polygonPoints[2], polygonPoints[3])
        mPath?.lineTo(polygonPoints[4], polygonPoints[5])
        mPath?.lineTo(polygonPoints[6], polygonPoints[7])
        mPath?.close()

        val bounds = RectF()
        mPath?.computeBounds(bounds, true)
        mPathBounds = bounds
        Log.e(TAG, "drawPath: pathBounds : $mPathBounds")
        mExtraCanvas?.drawPath(mPath!!,mRectPaint!!)
        postInvalidate()
    }

    fun rotatePath(angle: Float)
    {
        /*val x = 191.27714285714F
        val y = 564.22034285714F
        val path = Path()
        path.setLastPoint(150F, 150F)
        path.lineTo(475F, 80F)
        path.lineTo(400F, 890F)
        path.lineTo(100F, 720F)
        mExtraCanvas?.save()
        mExtraCanvas?.rotate(angle, x, y)
        mExtraCanvas?.save()
        //mExtraCanvas?.translate(x, y)
        mExtraCanvas?.drawPath(path, mRectPaint!!)
        mExtraCanvas?.restore()
        mExtraCanvas?.restore()*/
        var scaleX = 1F
        var scaleY = 1F
        var centerX = 1F
        var centerY = 1F
        Log.e(TAG, "rotatePath: pointsBeforeRotating : ${printPolygonPoints()}")
        val mMatrix = Matrix()
        val bounds = RectF()
        mPath?.computeBounds(bounds, true)
        Log.e(TAG, "rotatePath: scaleX : $scaleX, scaleY : $scaleY")
        centerX = bounds.centerX()
        centerY = bounds.centerY()
        mMatrix.postRotate(angle, centerX, centerY)
        Log.e(TAG, "rotatePath: pointsAfterRotating : ${printPolygonPoints() }")
        mPath?.transform(mMatrix)

        mPath?.computeBounds(bounds, true)
        Log.e(TAG, "rotatePath: pathBounds : $mPathBounds, bound : $bounds")
        if(mPathBounds == null)
        {
            mPathBounds = bounds
        }
        else
        {
            scaleX = bounds.width() / mPathBounds?.width()!!
            scaleY = bounds.height() / mPathBounds?.height()!!
            mPathBounds = bounds
        }
        mMatrix.postScale(scaleX,scaleY,bounds.centerX(),bounds.centerY())
        mMatrix.mapPoints(polygonPoints)
        mPath?.transform(mMatrix)


        mExtraBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        mExtraCanvas = Canvas(mExtraBitmap!!)
        mExtraCanvas?.drawColor(mBackgroundColor)
        mExtraCanvas?.drawPath(mPath!!, mRectPaint!!)

        postInvalidate()
    }

    fun printPolygonPoints() : String
    {
        var string = ""
        for(point in polygonPoints)
        {
            string += "$point,"
        }
        return string

    }

}