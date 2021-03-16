//
// Created by shadat on 2/28/21.
//
#include<opencv2/highgui.hpp>
#include<opencv2/imgcodecs.hpp>
#include<opencv2/imgproc.hpp>
#include<iostream>
using namespace std;
using namespace cv;


Mat preprocessImage(Mat inputImage)
{
    Mat grayScaleImage, blurredImage,edges, dilatedImage, erodedImage;

    //convert image to grayscale
    cvtColor(inputImage,grayScaleImage,COLOR_BGR2GRAY);

    //apply gaussian blur to gray scale image
    GaussianBlur(grayScaleImage,blurredImage,Size(3,3),5);

    //apply canny edge detection
    // the value of threshold1 and threshold2 determine the number of edges. The higher the value the larger the number
    //of detected edges are.
    Canny(blurredImage,edges,100,200);

    //apply dilation to canny edge detection. Make the edges more thick.Dilation is useful for better edge detection. In the output of
    //canny edge detection the there can be gaps between edges. Dilation removes these gaps and make the edges smoother.
    Mat dilationKernal = getStructuringElement(MORPH_RECT,Size(3,3));
    dilate(edges, dilatedImage, dilationKernal);
    erode(dilatedImage,erodedImage,dilationKernal);

    return erodedImage;

}

vector<Point> getContours(Mat imageToFindContour, Mat imageToDrawContour)
{
    vector<vector<Point>> contours;

    //opencv function to find contours from dilated image
    findContours(imageToFindContour,contours, RETR_EXTERNAL,CHAIN_APPROX_SIMPLE);

    //opencv function to draw contours on an image
    //drawContours(imageToDrawContour,contours,-1,Scalar(0,0,255),5);


    vector<vector<Point>> contourPolygons(contours.size());
    //vector<Rect> boundRect(contours.size());
    vector<Point> biggestContour(contours.size());
    //filter out contour with certain area, say > 1000
    int maxArea = 0;
    for(int i=0; i<contours.size(); i++)
    {
        int area = contourArea(contours[i]);
        if(area > 1000)
        {

            float peri = arcLength(contours[i],true);
            // find the polygon points of the bounding box of the contours
            approxPolyDP(contours[i],contourPolygons[i],0.01 * peri, true);


            cout << "Length of polygon : " << contourPolygons[i].size() << " Area : "<< area <<endl;

            //finding the bounding rectangle of the contour polygon
            //boundRect[i] = boundingRect(contourPolygons[i]);

            if(area > maxArea && contourPolygons[i].size() == 4)
            {
                //drawContours(imageToDrawContour,contourPolygons,i,Scalar(0,0,255),5);
                biggestContour = {contourPolygons[i][0],contourPolygons[i][1],contourPolygons[i][2],contourPolygons[i][3]};
                maxArea = area;

            }

            //drawContours(imageToDrawContour,contours,i,Scalar(0,0,255),5);
            //drawContours(imageToDrawContour,contourPolygons,i,Scalar(0,0,255),5);
            //rectangle(imageToDrawContour,boundRect[i].tl(),boundRect[i].br(),Scalar(0,255,0),3);



        }
    }
    //drawContours(imageToDrawContour,biggestContour,-1,Scalar(0,0,255),5);
    return biggestContour;
}

void drawPoints(Mat imageToDrawPoints, vector<Point> points, Scalar color)
{
    for(int i=0; i<points.size(); i++)
    {
        circle(imageToDrawPoints, points[i], 15, color, FILLED);
        putText(imageToDrawPoints,to_string(i), points[i], FONT_HERSHEY_PLAIN, 2, color, 2);
    }
}

vector<Point> reorderPoints(vector<Point> points)
{
    vector<Point> newPoints;
    vector<int> sumPoints, subPoints;

    for (int i = 0; i< 4; i++)
    {
        sumPoints.push_back(points[i].x + points[i].y);
        subPoints.push_back(points[i].x - points[i].y);
    }

    newPoints.push_back(points[min_element(sumPoints.begin(), sumPoints.end()) - sumPoints.begin()]);
    newPoints.push_back(points[max_element(subPoints.begin(), subPoints.end()) - subPoints.begin()]);


    newPoints.push_back(points[min_element(subPoints.begin(), subPoints.end()) - subPoints.begin()]);
    newPoints.push_back(points[max_element(sumPoints.begin(), sumPoints.end()) - sumPoints.begin()]);

    return newPoints;
}

Mat warpImage(Mat inputImage, vector<Point> points, float width, float height)
{

    //4 corner points of the source image
    Point2f sourcePoints[4] = {points[0], points[1], points[2], points[3]};

    //4 corner points of the warped image
    Point2f destinationPoints[4] = {{0.0f,0.0f},{width,0.0f},{0.0f,height},{width,height}};

    //warp perspective is applied using a matrix. It will give us a matrix from the source and destination points
    Mat warpPerspectiveMatrix = getPerspectiveTransform(sourcePoints,destinationPoints);
    Mat warpImage;
    warpPerspective(inputImage, warpImage, warpPerspectiveMatrix, Point(width,height));
    return warpImage;
}

Mat getWarpedImage(Mat inputImage, vector<Point> docPoints)
{
    //Mat preprocessedImage = preprocessImage(inputImage);
    //vector<Point> initialPoints = getContours(preprocessedImage,inputImage);
    //vector<Point> docPoints = reorderPoints(initialPoints);
    float a4PaperWidth = 420, a4PaperHeight = 596;
    Mat warpedImage = warpImage(inputImage,docPoints,a4PaperWidth, a4PaperHeight);
    return warpedImage;
}

Mat removeNoise(Mat thresholdImage)
{
    vector<vector<Point>> contours;
    vector<Vec4i> hierarchy;
    int thresholdBlobArea = 10;

    //opencv function to find contours from dilated image
    findContours(thresholdImage,contours, hierarchy, RETR_TREE,CHAIN_APPROX_SIMPLE);

    if ( !contours.empty() && !hierarchy.empty() )
    {
        for ( int i=0; i<contours.size(); i++ )
        {
            int indexLevel = hierarchy[i][1];
            if ( indexLevel <= i )
            {
                int area = contourArea(contours[i]);
                if(area <= thresholdBlobArea)
                {
                    drawContours(thresholdImage,contours,-1,255,-1,1);
                }
                // random colour
                //Scalar colour( (rand()&255), (rand()&255), (rand()&255) );
                //drawContours( outImage, contours, i, colour );
            }
        }
    }
    return thresholdImage;
}

Mat applyAdaptiveThreshold(Mat warpedImage,int blockSize, double c)
{
    Mat grayScaleImage, thresholdImage, blurredImage,edges, dilatedImage, erodedImage;

    //convert image to grayscale
    cvtColor(warpedImage,grayScaleImage,COLOR_BGR2GRAY);

    adaptiveThreshold(grayScaleImage,thresholdImage,255,ADAPTIVE_THRESH_GAUSSIAN_C,THRESH_BINARY,blockSize,c);
    //thresholdImage = removeNoise(thresholdImage);
    //bitwise_not(thresholdImage, thresholdImage);
    //medianBlur(thresholdImage, thresholdImage, 3);
    return thresholdImage;
}

Mat setBrightnessAndContrast(Mat inputImage, int brightnessValue, float contrastValue)
{
    inputImage.convertTo(inputImage,-1,contrastValue,brightnessValue);
    return inputImage;
}

Mat getGrayscaleImage(Mat inputImage)
{
    Mat grayScaleImage;
    cvtColor(inputImage,grayScaleImage,COLOR_BGR2GRAY);
    return grayScaleImage;
}

Mat getBrightenImage(Mat inputImage, int brightnessValue)
{
    inputImage.convertTo(inputImage,-1,1,brightnessValue);
    return inputImage;
}

Mat getLightenImage(Mat inputImage, float contrastValue)
{
    inputImage.convertTo(inputImage,-1,contrastValue,0);
    return inputImage;
}





int main()
{
    string imagePath = "resources/paper.jpg";

    Mat inputImage = imread(imagePath);

    //resize original image to half width and height
    resize(inputImage,inputImage,Size(),0.5,0.5);

    imshow("Original Image",inputImage);

    Mat preprocessedImage = preprocessImage(inputImage);

    imshow("Pre-processed image",preprocessedImage);

    vector<Point> initialPoints = getContours(preprocessedImage,inputImage);

    //drawPoints(inputImage,initialPoints, Scalar(0,0,255));

    vector<Point> docPoints = reorderPoints(initialPoints);

    //drawPoints(inputImage,docPoints, Scalar(0,255,0));

    imshow("With Points",inputImage);

    float a4PaperWidth = 420, a4PaperHeight = 596;

    Mat warpedImage = warpImage(inputImage,docPoints,a4PaperWidth, a4PaperHeight);
    imshow("warped image",warpedImage);

    //pre-process image

    waitKey(0);

    return 0;
}