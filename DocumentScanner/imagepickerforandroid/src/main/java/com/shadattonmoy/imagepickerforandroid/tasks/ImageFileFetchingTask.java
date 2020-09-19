package com.shadattonmoy.imagepickerforandroid.tasks;

import android.app.Activity;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;

import androidx.annotation.NonNull;

import com.shadattonmoy.imagepickerforandroid.constants.Constants;
import com.shadattonmoy.imagepickerforandroid.constants.ImagePickerType;
import com.shadattonmoy.imagepickerforandroid.helpers.FileHelper;
import com.shadattonmoy.imagepickerforandroid.model.ImageFolder;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ImageFileFetchingTask extends AsyncTask<String, Void, List<String>>
{
    private Activity activity;
    private RecentImageFileFetchListener recentImageFileFetchListener;
    private AllImageFileFetchListener allImageFileFetchListener;
    private ImagePickerType imagePickerType;

    public interface AllImageFileFetchListener{
        void onAllImageFileFetched(List<String> imagePaths);
    }

    public interface RecentImageFileFetchListener{
        void onRecentImageFileFetched(List<String> imagePaths);
    }

    public ImageFileFetchingTask(Activity activity, ImagePickerType imagePickerType) {
        this.activity = activity;
        this.imagePickerType = imagePickerType;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected List<String> doInBackground(String... arguments)
    {
        if(imagePickerType == ImagePickerType.RECENT_IMAGE_LIST || imagePickerType == ImagePickerType.ALL_IMAGE_LIST)
        {
            return getImagePaths(imagePickerType);
        }
        else if(imagePickerType == ImagePickerType.ALL_IMAGE_FROM_FOLDER)
        {
            String folderPath = arguments[0];
            return getImagesByFolder(folderPath);
        }
        return null;
    }

    @Override
    protected void onPostExecute(List<String> result) {
        super.onPostExecute(result);
        if(imagePickerType == ImagePickerType.RECENT_IMAGE_LIST && recentImageFileFetchListener!=null)
        {
            recentImageFileFetchListener.onRecentImageFileFetched(result);
        }
        else
        {
            if( (imagePickerType == ImagePickerType.ALL_IMAGE_LIST || imagePickerType == ImagePickerType.ALL_IMAGE_FROM_FOLDER)
                    && allImageFileFetchListener!=null)
            {
                allImageFileFetchListener.onAllImageFileFetched(result);
            }
        }

    }

    public ArrayList<String> getImagePaths(ImagePickerType imagePickerType)
    {
        Uri uri;
        Cursor cursor;
        int columnIndexForImages, columnIndexForFolder;
        ArrayList<String> listOfAllImages = new ArrayList<String>();
        String absolutePathOfImage = null;
        uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

        String[] projection = { MediaStore.MediaColumns.DATA,
                MediaStore.MediaColumns.DATE_ADDED,
                MediaStore.Images.Media.BUCKET_DISPLAY_NAME };
        String limit = "";
        if(imagePickerType == ImagePickerType.RECENT_IMAGE_LIST)
            limit = "limit "+ Constants.RECENT_IMAGE_LIMIT;

        String sortOrder = MediaStore.MediaColumns.DATE_ADDED + " desc "+limit;

        cursor = activity.getContentResolver().query(uri, projection, null,
                null, sortOrder);

        if(cursor!=null)
        {
            columnIndexForImages = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);

            columnIndexForFolder = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME);

            while (cursor.moveToNext())
            {
                absolutePathOfImage = cursor.getString(columnIndexForImages);
                File imageFile = new File(absolutePathOfImage);
//            Logger.showLog("ImagePath",absolutePathOfImage+" length "+imageFile.length());
                if(imageFile.exists() && imageFile.length()>0)
                    listOfAllImages.add(absolutePathOfImage);
            }
            if(!cursor.isClosed())
                cursor.close();
        }



        return listOfAllImages;
    }

    public ArrayList<ImageFolder> getAllImageFolder()
    {
        Uri uri;
        Cursor cursor;
        int  columnIndexForFolder, columnIndexForImages;
        ArrayList<ImageFolder> imageFolderList = new ArrayList<>();
        Map<String, Boolean> visitedFolders = new HashMap<>();
        String absolutePathOfImage = null;
        uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

        String[] projection = { MediaStore.MediaColumns.DATA,
                MediaStore.Images.Media.BUCKET_DISPLAY_NAME };

        cursor = activity.getContentResolver().query(uri, projection, null,
                null, null);

        cursor = activity.getContentResolver().query(uri, projection, null, null, null);
        if(cursor != null)
        {
            while (cursor.moveToNext())
            {
                String firstImageFilePath = cursor.getString(cursor.getColumnIndex(projection[0]));
                String imageFolderPath = cursor.getString(cursor.getColumnIndex(projection[1]));

                File firstImage = new File(firstImageFilePath);
                String folderPath = FileHelper.getFolderPathFromFilePath(firstImage);
                if(visitedFolders.get(folderPath)==null)
                {
                    File file = new File(firstImageFilePath);
                    if (file.exists())
                    {
                        ImageFolder imageFolder = new ImageFolder(folderPath, firstImageFilePath);
                        imageFolderList.add(imageFolder);
                        visitedFolders.put(folderPath,true);
                    }
                }
            }
            cursor.close();
        }

        return imageFolderList;
    }


    public List<String> getImagesByFolder(@NonNull String folderPath){

        Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        String[] projection = {MediaStore.Images.Media.DATA};
        String selection = MediaStore.Images.Media.BUCKET_DISPLAY_NAME+" =?";
        String orderBy = MediaStore.Images.Media.DATE_ADDED+" DESC";
        File folder = new File(folderPath);
        String folderName = folder.getName();

        List<String> imagePaths = new ArrayList<>();

        Cursor cursor = activity.getContentResolver().query(uri, projection, selection,new String[]{folderName}, orderBy);

        if(cursor != null)
        {
            File file;
            while (cursor.moveToNext())
            {
                String path = cursor.getString(cursor.getColumnIndex(projection[0]));
                file = new File(path);
                String folderPathOfFile = file.getParent();
                if (file.exists() && !imagePaths.contains(path) && folderPathOfFile.equals(folderPath))
                {
                    imagePaths.add(path);
                }
            }
            cursor.close();
        }
        return imagePaths;
    }

    public void setRecentImageFileFetchListener(RecentImageFileFetchListener recentImageFileFetchListener) {
        this.recentImageFileFetchListener = recentImageFileFetchListener;
    }

    public void setAllImageFileFetchListener(AllImageFileFetchListener allImageFileFetchListener) {
        this.allImageFileFetchListener = allImageFileFetchListener;
    }
}
