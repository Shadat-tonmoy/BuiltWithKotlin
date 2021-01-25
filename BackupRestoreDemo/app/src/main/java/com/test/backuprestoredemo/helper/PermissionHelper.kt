package com.test.backuprestoredemo.helper

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity

class PermissionHelper(var context: AppCompatActivity)
{
    companion object{
        const val REQUEST_STORAGE_READ_PERMISSION = 0
        const val REQUEST_STORAGE_WRITE_PERMISSION = 1
        const val REQUEST_CAMERA_PERMISSION = 2
        const val REQUEST_CODE_READ_CONTACT = 3
        const val REQUEST_FINE_LOCATION = 4
        const val REQUEST_GET_ACCOUNT_PERMISSION = 5
    }
    fun isContactPermissionGranted() : Boolean
    {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
            && context.checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            context.requestPermissions(arrayOf(Manifest.permission.READ_CONTACTS), REQUEST_CODE_READ_CONTACT)
            false
        } else true
    }

    fun isReadStoragePermissionGranted(requestCode:Int = REQUEST_STORAGE_READ_PERMISSION) : Boolean
    {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
            && context.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            context.requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), requestCode)
            false
        } else true
    }

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

    fun isLocationPermissionGranted() : Boolean
    {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
            && context.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            context.requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), REQUEST_FINE_LOCATION)
            false
        } else true
    }

    fun isAccountPermissionGranted() : Boolean
    {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
            && context.checkSelfPermission(Manifest.permission.GET_ACCOUNTS) != PackageManager.PERMISSION_GRANTED) {
            context.requestPermissions(arrayOf(Manifest.permission.GET_ACCOUNTS), REQUEST_GET_ACCOUNT_PERMISSION)
            false
        } else true
    }
}