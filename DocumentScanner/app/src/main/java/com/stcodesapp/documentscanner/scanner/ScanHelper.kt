package com.stcodesapp.documentscanner.scanner

import android.graphics.Bitmap
import com.theartofdev.edmodo.cropper.Polygon

external fun getGrayscaleImage(inputImage : Bitmap, outputImage : Bitmap)

external fun getWarpedImage(inputImage : Bitmap, outputImage : Bitmap, polygon: Polygon)

external fun getFilteredImage(inputImage : Bitmap, outputImage : Bitmap, blockSize: Int, c : Double)

external fun updateBrightnessOfImage(inputImage : Bitmap, outputImage : Bitmap, brightnessValue: Int)