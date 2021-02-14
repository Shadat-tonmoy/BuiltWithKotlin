package com.test.simplecanvas

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import kotlinx.android.synthetic.main.activity_draw_shape_activtiy.*

class DrawShapeActivity : AppCompatActivity()
{
    companion object{
        private const val TAG = "DrawShapeActivity"
    }
    var rotationAngle = 0F


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_draw_shape_activtiy)
        init()
    }


    private fun init()
    {
        drawRectButton.setOnClickListener {
            drawRect()
        }

        drawPathButton.setOnClickListener {
            drawPath()
        }

        rotatePathButton.setOnClickListener {
            rotatePath()
        }

    }

    private fun drawRect()
    {
        shapeDrawingCanvas.drawRect()
    }

    private fun drawPath()
    {
        shapeDrawingCanvas.drawPath()
    }

    private fun rotatePath()
    {
        rotationAngle += 90
        rotationAngle %= 360
        Log.e(TAG, "rotatePath: rotationAngle : $rotationAngle")
        shapeDrawingCanvas.rotatePath(90F)
    }
}