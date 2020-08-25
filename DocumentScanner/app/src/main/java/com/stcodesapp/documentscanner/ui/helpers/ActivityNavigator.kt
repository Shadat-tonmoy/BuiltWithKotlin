package com.stcodesapp.documentscanner.ui.helpers

import android.app.Activity
import android.content.Intent
import com.stcodesapp.documentscanner.camera.CameraActivity

class ActivityNavigator(private val activity:Activity)
{

    fun closeScreen()
    {
        activity.finish()
    }

    fun openCameraToCreateDocument()
    {
        val intent = Intent(activity,CameraActivity::class.java)
        activity.startActivity(intent)
    }

}