package com.stcodesapp.documentscanner.scanner

import android.graphics.Bitmap
import com.theartofdev.edmodo.cropper.Polygon

external fun getGrayscaleImage(inputImage : Bitmap, outputImage : Bitmap)

external fun getBlackAndWhiteImage(inputImage : Bitmap, outputImage : Bitmap)

external fun getWarpedImage(inputImage : Bitmap, outputImage : Bitmap, polygon: Polygon)

external fun getFilteredImage(inputImage : Bitmap, outputImage : Bitmap, blockSize: Int, c : Double)

external fun updateBrightnessAndContrastOfImage(inputImage : Bitmap, outputImage : Bitmap, brightnessValue: Int, contrastValue : Float)

external fun getBrightenImage(inputImage : Bitmap, outputImage : Bitmap, brightnessValue: Int)

external fun getLightenImage(inputImage : Bitmap, outputImage : Bitmap, contrastValue: Float)