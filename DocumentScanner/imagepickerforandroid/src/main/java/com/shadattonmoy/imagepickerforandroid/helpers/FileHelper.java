package com.shadattonmoy.imagepickerforandroid.helpers;

import java.io.File;

public class FileHelper
{


    public static String getFolderPathFromFilePath(File file)
    {
        return file.getParent();
    }
}
