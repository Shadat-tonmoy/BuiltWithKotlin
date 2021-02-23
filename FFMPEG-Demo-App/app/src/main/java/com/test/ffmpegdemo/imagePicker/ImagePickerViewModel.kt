package com.test.ffmpegdemo.imagePicker

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel

class ImagePickerViewModel(context : Application) : AndroidViewModel(context)
{
    var chosenFileUri : Uri? = null
}