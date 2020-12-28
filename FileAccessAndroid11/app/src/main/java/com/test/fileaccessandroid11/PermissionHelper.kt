package com.test.fileaccessandroid11

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity

class PermissionHelper(var context: AppCompatActivity)
{
    companion object{
        const val REQUEST_STORAGE_READ_PERMISSION =1
    }


    fun isReadStoragePermissionGranted(requestCode:Int = REQUEST_STORAGE_READ_PERMISSION) : Boolean
    {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
            && context.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            context.requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), requestCode)
            false
        } else true
    }
}