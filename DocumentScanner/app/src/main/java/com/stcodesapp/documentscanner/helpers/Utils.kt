package com.stcodesapp.documentscanner.helpers

import java.io.File

fun getFileNameFromPath(path:String) : String
{
    return File(path).name
}