package com.shadattonmoy.imagepickerforandroid.helpers;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import androidx.core.app.ActivityCompat;


import com.shadattonmoy.imagepickerforandroid.constants.RequestCode;


public class AppPermissionHelper {

    private Activity activity;
    private static final String WRITE_EXTERNAL_STORAGE = Manifest.permission.WRITE_EXTERNAL_STORAGE;
    private static final String READ_CONTACT = Manifest.permission.READ_CONTACTS;
    private static final String READ_EXTERNAL_STORAGE = Manifest.permission.READ_EXTERNAL_STORAGE;
    private static final String ACCESS_CAMERA = Manifest.permission.CAMERA;


    public AppPermissionHelper(Activity activity) {
        this.activity = activity;
    }

    public static boolean hasWriteExternalStoragePermission(Activity activity)
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            if (activity.checkSelfPermission(WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
            {
                return true;
            }
            else
            {
                ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, RequestCode.WRITE_EXTERNAL_STORAGE_PERMISSION);
                return false;
            }
        }
        else
        {
            return true;
        }
    }

    public static boolean hasReadExternalStoragePermission(Activity activity)
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            if (activity.checkSelfPermission(READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
            {
                return true;
            }
            else
            {
                ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, RequestCode.READ_EXTERNAL_STORAGE_PERMISSION);
                return false;
            }
        }
        else
        {
            return true;
        }
    }


    public static boolean hasCameraAccessPermission(Activity activity)
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            if (activity.checkSelfPermission(ACCESS_CAMERA) == PackageManager.PERMISSION_GRANTED)
            {
                return true;
            }
            else
            {
                ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.CAMERA}, RequestCode.OPEN_CAMERA_PERMISSION);
                return false;
            }
        }
        else
        {
            return true;
        }
    }
}
