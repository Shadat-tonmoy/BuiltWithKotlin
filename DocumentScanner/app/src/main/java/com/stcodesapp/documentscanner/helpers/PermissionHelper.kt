package com.stcodesapp.documentscanner.helpers

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import com.stcodesapp.documentscanner.constants.RequestCode.Companion.REQUEST_CAMERA_PERMISSION
import com.stcodesapp.documentscanner.constants.RequestCode.Companion.REQUEST_STORAGE_WRITE_PERMISSION

class PermissionHelper(var context: AppCompatActivity)
{

    fun isWriteStoragePermissionGranted(requestCode:Int = REQUEST_STORAGE_WRITE_PERMISSION) : Boolean
    {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
            && context.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            context.requestPermissions(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), requestCode)
            false
        } else true
    }

    fun isCameraPermissionGranted() : Boolean
    {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
            && context.checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            context.requestPermissions(arrayOf(Manifest.permission.CAMERA), REQUEST_CAMERA_PERMISSION)
            false
        } else true
    }
}