package com.shadattonmoy.imagepickerforandroid.controller;

import android.app.Activity;
import android.os.Bundle;

import com.shadattonmoy.imagepickerforandroid.constants.ImagePickerType;
import com.shadattonmoy.imagepickerforandroid.constants.SortingType;
import com.shadattonmoy.imagepickerforandroid.constants.ImagePickerTags;
import com.shadattonmoy.imagepickerforandroid.tasks.uiUpdateTasks.ImagePickerFolderContainerUIUpdateTask;
import com.shadattonmoy.imagepickerforandroid.ui.adapters.ImagePickerListAdapter;
import com.shadattonmoy.imagepickerforandroid.ui.fragments.ImagePickerListFragment;
import com.shadattonmoy.imagepickerforandroid.ui.screen.ImagePickerFolderContainerScreen;
import com.shadattonmoy.imagepickerforandroid.ui.screenView.ImagePickerFolderContainerScreenView;

public class ImagePickerFolderContainerController implements ImagePickerFolderContainerScreen.Listener, ImagePickerListAdapter.Listener
{
    Activity activity;
    private ImagePickerFolderContainerUIUpdateTask uiUpdateTask;
    private ImagePickerFolderContainerScreenView screenView;
    private ImagePickerType imagePickerType = ImagePickerType.FOLDER_LIST_FOR_IMAGE;
    private Bundle arguments;
    private boolean titleAsc = true, sizeAsc = true, lastModifiedAsc = true,isAscending = false, forAppending = false, isBatchModeEnabled = false;
    private SortingType sortingType  = SortingType.FILE_LAST_MODIFIED_TIME;

    public ImagePickerFolderContainerController(Activity activity)
    {
        this.activity=activity;
        this.uiUpdateTask = new ImagePickerFolderContainerUIUpdateTask(activity);
    }

    public void bindView(ImagePickerFolderContainerScreenView screenView)
    {
        this.screenView = screenView;
        this.uiUpdateTask.bindView(screenView);
    }

    public void onCreate()
    {
        loadImageFolderList();
    }

    private void loadImageFolderList()
    {
        Bundle imageFolderAgrs = new Bundle();
        imageFolderAgrs.putSerializable(ImagePickerTags.IMAGE_PICKER_TYPE,ImagePickerType.FOLDER_LIST_FOR_IMAGE);
        imageFolderAgrs.putBoolean(ImagePickerTags.BATCH_MODE_ENABLED,true);
        ImagePickerListFragment imageFolderFragment = ImagePickerListFragment.newInstance(imageFolderAgrs,this);
        uiUpdateTask.replaceFragment(imageFolderFragment, ImagePickerTags.IMAGE_FOLDER_LIST);

    }



    public void onStart()
    {
        screenView.registerListener(this);
    }

    public void onStop()
    {
        screenView.unregisterListener(this);
    }


    @Override
    public void onFilePickerListItemClicked(String filePickerItemPath, ImagePickerType imagePickerType)
    {
        loadAllImageForFolder(filePickerItemPath);
    }

    private void loadAllImageForFolder(String folderPath)
    {
        Bundle args = new Bundle();
        args.putString(ImagePickerTags.FOLDER_PATH_TAG,folderPath);
        args.putBoolean(ImagePickerTags.BATCH_MODE_ENABLED,true);
        uiUpdateTask.loadAllImageListForFolder(args);

    }
}
