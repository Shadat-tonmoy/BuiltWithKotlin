package com.shadattonmoy.imagepickerforandroid.tasks;

import android.app.Activity;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.util.Log;

import androidx.annotation.NonNull;

import com.shadattonmoy.imagepickerforandroid.constants.Constants;
import com.shadattonmoy.imagepickerforandroid.constants.ImagePickerType;
import com.shadattonmoy.imagepickerforandroid.helpers.FileHelper;
import com.shadattonmoy.imagepickerforandroid.model.ImageFile;
import com.shadattonmoy.imagepickerforandroid.model.ImageFolder;

import java.io.File;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.shadattonmoy.imagepickerforandroid.tasks.UtilityTask.isAndroidX;

public class ImageFileFetchingTask extends AsyncTask<String, Void, List<ImageFile>>
{
    private static final String TAG = "ImageFileFetchingTask";
    private Activity activity;
    private RecentImageFileFetchListener recentImageFileFetchListener;
    private AllImageFileFetchListener allImageFileFetchListener;
    private ImagePickerType imagePickerType;

    public interface AllImageFileFetchListener{
        void onAllImageFileFetched(List<ImageFile> imageFiles);
    }

    public interface RecentImageFileFetchListener{
        void onRecentImageFileFetched(List<ImageFile> imageFiles);
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
    protected List<ImageFile> doInBackground(String... arguments)
    {
        if(imagePickerType == ImagePickerType.RECENT_IMAGE_LIST || imagePickerType == ImagePickerType.ALL_IMAGE_LIST)
        {
            return getImagePaths(imagePickerType);
        }
        else if(imagePickerType == ImagePickerType.ALL_IMAGE_FROM_FOLDER)
        {
            String folderPath = arguments[0];
            return getImagesFromFolder(folderPath);
        }
        return null;
    }

    @Override
    protected void onPostExecute(List<ImageFile> result) {
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

    public ArrayList<ImageFile> getImagePaths(ImagePickerType imagePickerType)
    {
        Uri uri;
        Cursor cursor;
        int dataColumnIndex = -1 , idColumnIndex = -1, displayNameColumnIndex = -1, dateModifiedColumnIndex = -1, folderColumnIndex = -1, relativePathIndex = -1;
        ArrayList<ImageFile> listOfAllImages = new ArrayList<>();
        uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

        String[] projection = { MediaStore.MediaColumns.DATA, MediaStore.MediaColumns.DATE_MODIFIED, MediaStore.MediaColumns.DISPLAY_NAME, MediaStore.MediaColumns._ID};
        if(isAndroidX())
        {
            projection = new String[]{MediaStore.MediaColumns.DATE_MODIFIED, MediaStore.MediaColumns.DISPLAY_NAME, MediaStore.MediaColumns._ID, MediaStore.MediaColumns.RELATIVE_PATH};
        }
        String limit = "";
        if(imagePickerType == ImagePickerType.RECENT_IMAGE_LIST)
            limit = "limit "+ Constants.RECENT_IMAGE_LIMIT;

        String sortOrder = MediaStore.MediaColumns.DATE_ADDED + " desc "+limit;

        cursor = activity.getContentResolver().query(uri, projection, null,
                null, sortOrder);

        if(cursor!=null)
        {
            if(isAndroidX())
            {
                idColumnIndex = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns._ID);
                displayNameColumnIndex = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DISPLAY_NAME);
                dateModifiedColumnIndex = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATE_MODIFIED);
                relativePathIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.RELATIVE_PATH);
            }
            else
            {
                dataColumnIndex = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
                displayNameColumnIndex = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DISPLAY_NAME);
                dateModifiedColumnIndex = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATE_MODIFIED);
            }

            while (cursor.moveToNext())
            {
                if(isAndroidX())
                {
                    long id = cursor.getLong(idColumnIndex);
                    String displayName = cursor.getString(displayNameColumnIndex);
                    String relativePath= cursor.getString(relativePathIndex);
                    Uri imageUri = Uri.withAppendedPath(uri,Long.toString(id));
                    long lastModified = cursor.getLong(dateModifiedColumnIndex);
                    ImageFile imageFile = new ImageFile(displayName,imageUri.toString(),relativePath,lastModified,imageUri);
                    Log.e(TAG, "getImagePaths: "+imageFile.toString());
                    listOfAllImages.add(imageFile);
                }
                else
                {
                    String absolutePathOfImage = cursor.getString(dataColumnIndex);
                    String displayName = cursor.getString(displayNameColumnIndex);
                    long lastModified = cursor.getLong(dateModifiedColumnIndex);
                    File file = new File(absolutePathOfImage);
                    Uri imageUri = Uri.fromFile(file);
                    ImageFile imageFile = new ImageFile(displayName,absolutePathOfImage,absolutePathOfImage,lastModified,imageUri);
                    listOfAllImages.add(imageFile);
                }
            }
            if(!cursor.isClosed())
                cursor.close();
        }
        return listOfAllImages;
    }


    public ArrayList<ImageFile> getImagesFromFolder(String folder)
    {
        Uri uri;
        Cursor cursor;
        int dataColumnIndex = -1 , idColumnIndex = -1, displayNameColumnIndex = -1, dateModifiedColumnIndex = -1, folderColumnIndex = -1, relativePathIndex = -1;
        ArrayList<ImageFile> listOfAllImages = new ArrayList<>();
        uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

        String[] projection = { MediaStore.MediaColumns.DATA, MediaStore.MediaColumns.DATE_MODIFIED, MediaStore.MediaColumns.DISPLAY_NAME, MediaStore.MediaColumns._ID};
        if(isAndroidX())
        {
            projection = new String[]{MediaStore.MediaColumns.DATE_MODIFIED, MediaStore.MediaColumns.DISPLAY_NAME, MediaStore.MediaColumns._ID, MediaStore.MediaColumns.RELATIVE_PATH};
        }
        String limit = "";
        if(imagePickerType == ImagePickerType.RECENT_IMAGE_LIST)
            limit = "limit "+ Constants.RECENT_IMAGE_LIMIT;

        String sortOrder = MediaStore.MediaColumns.DATE_ADDED + " desc "+limit;

        cursor = activity.getContentResolver().query(uri, projection, null,
                null, sortOrder);

        if(cursor!=null)
        {
            if(isAndroidX())
            {
                idColumnIndex = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns._ID);
                displayNameColumnIndex = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DISPLAY_NAME);
                dateModifiedColumnIndex = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATE_MODIFIED);
                relativePathIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.RELATIVE_PATH);
            }
            else
            {
                dataColumnIndex = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
                displayNameColumnIndex = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DISPLAY_NAME);
                dateModifiedColumnIndex = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATE_MODIFIED);
            }

            while (cursor.moveToNext())
            {
                if(isAndroidX())
                {
                    long id = cursor.getLong(idColumnIndex);
                    String displayName = cursor.getString(displayNameColumnIndex);
                    String relativePath= cursor.getString(relativePathIndex);
                    Uri imageUri = Uri.withAppendedPath(uri,Long.toString(id));
                    long lastModified = cursor.getLong(dateModifiedColumnIndex);
                    if(relativePath.equals(folder))
                    {
                        ImageFile imageFile = new ImageFile(displayName,imageUri.toString(),relativePath,lastModified,imageUri);
                        Log.e(TAG, "getImagePaths: "+imageFile.toString());
                        listOfAllImages.add(imageFile);
                    }
                }
                else
                {
                    String absolutePathOfImage = cursor.getString(dataColumnIndex);
                    String displayName = cursor.getString(displayNameColumnIndex);
                    long lastModified = cursor.getLong(dateModifiedColumnIndex);
                    String parentFolder = FileHelper.getFolderPathFromFilePath(new File(absolutePathOfImage));
                    File file = new File(absolutePathOfImage);
                    Uri imageUri = Uri.fromFile(file);
                    if(parentFolder.equals(folder))
                    {
                        ImageFile imageFile = new ImageFile(displayName,absolutePathOfImage,absolutePathOfImage,lastModified,imageUri);
                        listOfAllImages.add(imageFile);
                    }

                }
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
        int  dataColumnIndex,relativePathColumnIndex = -1, idColumnIndex = -1;
        ArrayList<ImageFolder> imageFolderList = new ArrayList<>();
        Map<String, Boolean> visitedFolders = new HashMap<>();
        uri = android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

        String[] projection = { MediaStore.MediaColumns.DATA};
        if(isAndroidX())
        {
            projection = new String[]{MediaStore.MediaColumns._ID, MediaStore.MediaColumns.DISPLAY_NAME, MediaStore.Images.Media.RELATIVE_PATH};
        }
        String sortOrder = MediaStore.MediaColumns.DATE_ADDED+" desc";

        cursor = activity.getContentResolver().query(uri, projection, null, null, sortOrder);
        if(cursor != null)
        {
            while (cursor.moveToNext())
            {
                if(isAndroidX())
                {
                    idColumnIndex = cursor.getColumnIndex(MediaStore.MediaColumns._ID);
                    relativePathColumnIndex = cursor.getColumnIndex(MediaStore.MediaColumns.RELATIVE_PATH);
                    String folderPath = cursor.getString(relativePathColumnIndex);
                    long id = cursor.getLong(idColumnIndex);
                    if(visitedFolders.get(folderPath)==null)
                    {
                        Uri firstImageUri = Uri.withAppendedPath(uri,Long.toString(id));
                        ImageFolder imageFolder = new ImageFolder(folderPath, firstImageUri.toString());
                        imageFolderList.add(imageFolder);
                        visitedFolders.put(folderPath,true);
                    }
                }
                else
                {
                    dataColumnIndex = cursor.getColumnIndex(MediaStore.MediaColumns.DATA);
                    String firstImageFilePath = cursor.getString(dataColumnIndex);
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
