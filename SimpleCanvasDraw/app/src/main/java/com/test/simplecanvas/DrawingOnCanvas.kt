package com.test.simplecanvas

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View

class DrawingOnCanvas : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        init()
    }

    private fun init()
    {
        val canvasView = CanvasView(this)
        canvasView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
        setContentView(canvasView)
    }
}