package com.shadattonmoy.imagepickerforandroid.tasks;

import android.os.Build;

import com.shadattonmoy.imagepickerforandroid.BuildConfig;
import com.shadattonmoy.imagepickerforandroid.constants.Constants;

public class UtilityTask
{
    public static String trimString(String input)
    {
        if(input.length()> Constants.MAX_LENGTH_OF_STRING_TO_TRIM)
            input = input.substring(0,Constants.MAX_LENGTH_OF_STRING_TO_TRIM)+Constants.DOTS;
        return input;
    }

    public static boolean isAndroidX()
    {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q;
    }
}
