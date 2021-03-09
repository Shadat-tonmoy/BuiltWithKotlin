//
// Created by shadat on 2/28/21.
//
#include <jni.h>
#include "documentScanner.hpp"
#include "android/bitmap.h"
#include "android/log.h"
#include "opencv2/core.hpp"

void bitmapToMat (JNIEnv * env, jobject bitmap, Mat& dst, jboolean needUnPremultiplyAlpha);
void matToBitmap (JNIEnv * env, Mat& src, jobject bitmap, jboolean needUnPremultiplyAlpha);


extern "C" JNIEXPORT void JNICALL
Java_com_stcodesapp_documentscanner_scanner_ScanHelperKt_getGrayscaleImage(JNIEnv *env, jobject, jobject inputImage, jobject outputImage)
{
    Mat srcImage;
    bitmapToMat(env,inputImage,srcImage,false);
    Mat processedImage = preprocessImage(srcImage);
    matToBitmap(env,processedImage,outputImage,false);
}

extern "C" JNIEXPORT void JNICALL
Java_com_stcodesapp_documentscanner_scanner_ScanHelperKt_getWarpedImage(JNIEnv *env, jobject, jobject inputImage, jobject outputImage, jobject polygon)
{

    jclass polygonClass = env->GetObjectClass(polygon);

    jfieldID topLeftXId = env->GetFieldID(polygonClass, "topLeftX", "F");
    jfieldID topLeftYId = env->GetFieldID(polygonClass, "topLeftY", "F");

    jfieldID topRightXId = env->GetFieldID(polygonClass, "topRightX", "F");
    jfieldID topRightYId = env->GetFieldID(polygonClass, "topRightY", "F");

    jfieldID bottomLeftXId = env->GetFieldID(polygonClass, "bottomLeftX", "F");
    jfieldID bottomLeftYId = env->GetFieldID(polygonClass, "bottomLeftY", "F");

    jfieldID bottomRightXId = env->GetFieldID(polygonClass, "bottomRightX", "F");
    jfieldID bottomRightYId = env->GetFieldID(polygonClass, "bottomRightY", "F");

    jfloat topLeftX = env->GetFloatField(polygon,topLeftXId);
    jfloat topLeftY = env->GetFloatField(polygon,topLeftYId);
    jfloat topRightX = env->GetFloatField(polygon,topRightXId);
    jfloat topRightY = env->GetFloatField(polygon,topRightYId);
    jfloat bottomLeftX = env->GetFloatField(polygon,bottomLeftXId);
    jfloat bottomLeftY = env->GetFloatField(polygon,bottomLeftYId);
    jfloat bottomRightX = env->GetFloatField(polygon,bottomRightXId);
    jfloat bottomRightY = env->GetFloatField(polygon,bottomRightYId);

    float a4PaperWidth = 420, a4PaperHeight = 596;
    vector<Point> srcPoints;
    srcPoints.push_back(Point2f(topLeftX,topLeftY));
    srcPoints.push_back(Point2f(topRightX,topRightY));
    srcPoints.push_back(Point2f(bottomLeftX,bottomLeftY));
    srcPoints.push_back(Point2f(bottomRightX,bottomRightY));

    Mat warpedImage;
    Mat inputImageMat;
    bitmapToMat(env,inputImage,inputImageMat, false);
    warpedImage = getWarpedImage(inputImageMat,srcPoints);
    matToBitmap(env,warpedImage,outputImage,false);

    __android_log_print(ANDROID_LOG_ERROR, "PolygonPoints", "FromC++ TopLeftX %f, TopLeftY %f , TopRightX %f, TopRightY %f,"
                                                            "BottomLeftX %f, BottomLeftY %f , BottomRightX %f, BottomRightY %f,"
                                                            " ", topLeftX, topLeftY,topRightX, topRightY,bottomLeftX, bottomLeftY,bottomRightX, bottomRightY);

    __android_log_print(ANDROID_LOG_ERROR, "WarpedImage", "FromC++ rows : %d, cols : %d",warpedImage.rows,warpedImage.cols);
}



extern "C" JNIEXPORT void JNICALL
Java_com_stcodesapp_documentscanner_scanner_ScanHelperKt_getFilteredImage(JNIEnv *env, jobject, jobject inputImage, jobject outputImage,jint blockSize, jdouble c)
{
    Mat filteredImage;
    Mat inputImageMat;
    bitmapToMat(env,inputImage,inputImageMat, false);
    filteredImage = applyAdaptiveThreshold(inputImageMat,blockSize,c);
    matToBitmap(env,filteredImage,outputImage,false);
}



void bitmapToMat (JNIEnv * env, jobject bitmap, Mat& dst, jboolean needUnPremultiplyAlpha)
{
    AndroidBitmapInfo  info;
    void*              pixels = 0;

    try {
        CV_Assert( AndroidBitmap_getInfo(env, bitmap, &info) >= 0 );
        CV_Assert( info.format == ANDROID_BITMAP_FORMAT_RGBA_8888 ||
                   info.format == ANDROID_BITMAP_FORMAT_RGB_565 );
        CV_Assert( AndroidBitmap_lockPixels(env, bitmap, &pixels) >= 0 );
        CV_Assert( pixels );
        dst.create(info.height, info.width, CV_8UC4);
        if( info.format == ANDROID_BITMAP_FORMAT_RGBA_8888 )
        {
            Mat tmp(info.height, info.width, CV_8UC4, pixels);
            if(needUnPremultiplyAlpha) cvtColor(tmp, dst, COLOR_mRGBA2RGBA);
            else tmp.copyTo(dst);
        } else {
            // info.format == ANDROID_BITMAP_FORMAT_RGB_565
            Mat tmp(info.height, info.width, CV_8UC2, pixels);
            cvtColor(tmp, dst, COLOR_BGR5652RGBA);
        }
        AndroidBitmap_unlockPixels(env, bitmap);
        return;
    } catch(const cv::Exception& e) {
        AndroidBitmap_unlockPixels(env, bitmap);
        jclass je = env->FindClass("org/opencv/core/CvException");
        if(!je) je = env->FindClass("java/lang/Exception");
        env->ThrowNew(je, e.what());
        return;
    } catch (...) {
        AndroidBitmap_unlockPixels(env, bitmap);
        jclass je = env->FindClass("java/lang/Exception");
        env->ThrowNew(je, "Unknown exception in JNI code {nBitmapToMat}");
        return;
    }
}



void matToBitmap(JNIEnv * env,  Mat& src, jobject bitmap, jboolean needPremultiplyAlpha)
{
    AndroidBitmapInfo  info;
    void*              pixels = 0;

    try {
        CV_Assert( AndroidBitmap_getInfo(env, bitmap, &info) >= 0 );
        CV_Assert( info.format == ANDROID_BITMAP_FORMAT_RGBA_8888 ||
                   info.format == ANDROID_BITMAP_FORMAT_RGB_565 );
        CV_Assert( src.dims == 2 && info.height == (uint32_t)src.rows && info.width == (uint32_t)src.cols );
        CV_Assert( src.type() == CV_8UC1 || src.type() == CV_8UC3 || src.type() == CV_8UC4 );
        CV_Assert( AndroidBitmap_lockPixels(env, bitmap, &pixels) >= 0 );
        CV_Assert( pixels );
        if( info.format == ANDROID_BITMAP_FORMAT_RGBA_8888 )
        {
            Mat tmp(info.height, info.width, CV_8UC4, pixels);
            if(src.type() == CV_8UC1)
            {
                cvtColor(src, tmp, COLOR_GRAY2RGBA);
            } else if(src.type() == CV_8UC3){
                cvtColor(src, tmp, COLOR_RGB2RGBA);
            } else if(src.type() == CV_8UC4){
                if(needPremultiplyAlpha) cvtColor(src, tmp, COLOR_RGBA2mRGBA);
                else src.copyTo(tmp);
            }
        } else {
            // info.format == ANDROID_BITMAP_FORMAT_RGB_565
            Mat tmp(info.height, info.width, CV_8UC2, pixels);
            if(src.type() == CV_8UC1)
            {
                cvtColor(src, tmp, COLOR_GRAY2BGR565);
            } else if(src.type() == CV_8UC3){
                cvtColor(src, tmp, COLOR_RGB2BGR565);
            } else if(src.type() == CV_8UC4){
                cvtColor(src, tmp, COLOR_RGBA2BGR565);
            }
        }
        AndroidBitmap_unlockPixels(env, bitmap);
        return;
    } catch(const cv::Exception& e) {
        AndroidBitmap_unlockPixels(env, bitmap);
        jclass je = env->FindClass("org/opencv/core/CvException");
        if(!je) je = env->FindClass("java/lang/Exception");
        env->ThrowNew(je, e.what());
        return;
    } catch (...) {
        AndroidBitmap_unlockPixels(env, bitmap);
        jclass je = env->FindClass("java/lang/Exception");
        env->ThrowNew(je, "Unknown exception in JNI code {nMatToBitmap}");
        return;
    }
}