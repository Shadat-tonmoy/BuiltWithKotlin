package com.stcodesapp.documentscanner.ui.helpers

import android.app.Activity
import android.content.Intent

class ActivityNavigator(private val activity:Activity)
{

    fun closeScreen()
    {
        activity.finish()
    }

}