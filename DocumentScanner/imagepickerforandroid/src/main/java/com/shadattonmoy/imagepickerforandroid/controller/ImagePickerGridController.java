package com.shadattonmoy.imagepickerforandroid.controller;

import android.app.Activity;
import android.os.Bundle;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.List;

import com.shadattonmoy.imagepickerforandroid.R;
import com.shadattonmoy.imagepickerforandroid.constants.ImagePickerType;
import com.shadattonmoy.imagepickerforandroid.constants.ImagePickerTags;
import com.shadattonmoy.imagepickerforandroid.tasks.ImageFileFetchingTask;
import com.shadattonmoy.imagepickerforandroid.tasks.uiUpdateTasks.ImagePickerGridUIUpdateTask;
import com.shadattonmoy.imagepickerforandroid.ui.actvities.ImagePickerActivity;
import com.shadattonmoy.imagepickerforandroid.ui.screen.ImagePickerGridScreen;
import com.shadattonmoy.imagepickerforandroid.ui.screenView.ImagePickerGridScreenView;

public class ImagePickerGridController implements ImagePickerGridScreen.Listener, ImageFileFetchingTask.AllImageFileFetchListener
{

    Activity activity;
    ImagePickerGridUIUpdateTask uiUpdateTask;
    private ImagePickerGridScreenView screenView;
    private Bundle arguments;
    private boolean selectAll = false, isBatchModeEnabled = false;

    public ImagePickerGridController(Activity activity)
    {
        this.activity = activity;
        this.uiUpdateTask = new ImagePickerGridUIUpdateTask(activity);
    }

    public void bindView(ImagePickerGridScreenView screenView)
    {
        this.screenView = screenView;
        uiUpdateTask.bindView(screenView);
    }

    public void onStart()
    {
        screenView.registerListener(this);
        startFetchingImages();
    }

    public void onStop()
    {
        screenView.unregisterListener(this);
    }

    public void bindArguments(Bundle arguments)
    {
        this.arguments = arguments;
        isBatchModeEnabled = arguments.getBoolean(ImagePickerTags.BATCH_MODE_ENABLED,false);
        uiUpdateTask.bindSelectionBundle(arguments);
        uiUpdateTask.enableBatchMode(isBatchModeEnabled);
    }

    private void startFetchingImages()
    {
        List<String> allImagePaths = new ArrayList<>();
        String folderPath;
        if(arguments!=null )
        {
            folderPath = arguments.getString(ImagePickerTags.FOLDER_PATH_TAG);
            if(folderPath!=null)
            {
                fetchAllImageFromFolder(folderPath);
            }
            else
            {
                fetchAllImage();
            }
        }
        else
        {
            fetchAllImage();
        }

    }

    private void fetchAllImageFromFolder(String folderPath)
    {
        ImageFileFetchingTask imageFileFetchingTask = new ImageFileFetchingTask(activity, ImagePickerType.ALL_IMAGE_FROM_FOLDER);
        imageFileFetchingTask.setAllImageFileFetchListener(this);
        imageFileFetchingTask.execute(folderPath);
    }

    private void fetchAllImage()
    {
        ImageFileFetchingTask imageFileFetchingTask = new ImageFileFetchingTask(activity, ImagePickerType.ALL_IMAGE_LIST);
        imageFileFetchingTask.setAllImageFileFetchListener(this);
        imageFileFetchingTask.execute();
    }

    @Override
    public void onAllImageFileFetched(List<String> imagePaths)
    {
        uiUpdateTask.bindImages(imagePaths);
        uiUpdateTask.hideLoadingView();

    }

    @Override
    public void onImageGridItemClicked(int position)
    {
        if(isBatchModeEnabled)
            uiUpdateTask.setSelection(position);
        else
        {
            ((ImagePickerActivity)activity)
                    .getController()
                    .onSingleImageSelected(screenView.getImagePickerGridAdapter().getImageAtPosition(position));
        }


    }

    @Override
    public void onDoneButtonClicked()
    {
        ((ImagePickerActivity)activity)
                .getController()
                .onImageListSelected(screenView.getImagePickerGridAdapter().getSelectedImages());
    }

    @Override
    public void onSelectAllButtonClicked()
    {
        if(!selectAll)
            uiUpdateTask.selectAll();
        else uiUpdateTask.selectNone();
        selectAll = !selectAll;

    }

    public void bindSelectionBundle(Bundle bundle)
    {
        uiUpdateTask.bindSelectionBundle(bundle);
    }

    public void onOptionsItemSelected(MenuItem item)
    {
        if(item.getItemId() == R.id.select_all_menu)
        {
            onSelectAllButtonClicked();
            if(selectAll) item.setTitle("None");
            else item.setTitle("All");

        }


    }
}
