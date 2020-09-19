package com.shadattonmoy.imagepickerforandroid.controller;

import android.app.Activity;
import android.os.Bundle;

import java.util.List;

import com.shadattonmoy.imagepickerforandroid.constants.ImagePickerType;
import com.shadattonmoy.imagepickerforandroid.constants.SortingType;
import com.shadattonmoy.imagepickerforandroid.constants.ImagePickerTags;
import com.shadattonmoy.imagepickerforandroid.model.ImageFolder;
import com.shadattonmoy.imagepickerforandroid.tasks.ImageFileFetchingTask;
import com.shadattonmoy.imagepickerforandroid.tasks.uiUpdateTasks.ImagePickerListUIUpdateTask;
import com.shadattonmoy.imagepickerforandroid.ui.adapters.ImagePickerListAdapter;
import com.shadattonmoy.imagepickerforandroid.ui.screen.ImagePickerListScreen;
import com.shadattonmoy.imagepickerforandroid.ui.screenView.ImagePickerListScreenView;

public class ImagePickerListController implements ImagePickerListScreen.Listener
{
    Activity activity;
    private ImagePickerListUIUpdateTask uiUpdateTask;
    private ImagePickerListScreenView screenView;
    private ImagePickerType imagePickerType = ImagePickerType.FOLDER_LIST_FOR_IMAGE;
    private Bundle arguments;
    private boolean titleAsc = true, sizeAsc = true, lastModifiedAsc = true,isAscending = false, forAppending = false, isBatchModeEnabled = false;
    private SortingType sortingType  = SortingType.FILE_LAST_MODIFIED_TIME;

    public ImagePickerListController(Activity activity)
    {
        this.activity=activity;
        this.uiUpdateTask = new ImagePickerListUIUpdateTask(activity);
    }

    public void bindView(ImagePickerListScreenView screenView)
    {
        this.screenView = screenView;
        this.uiUpdateTask.bindView(screenView);
    }

    public void onCreate()
    {
        fetchListElement();
    }

    private void fetchListElement()
    {
        if(imagePickerType == ImagePickerType.FOLDER_LIST_FOR_IMAGE)
        {
            fetchImageFolders();
        }
        else if(imagePickerType == ImagePickerType.FILE_LIST_FOR_PDF)
        {
            fetchPDFFiles();
        }
    }

    public void onStart()
    {
        screenView.registerListener(this);
    }

    private void fetchImageFolders()
    {
        ImageFileFetchingTask imageFileFetchingTask = new ImageFileFetchingTask(activity, ImagePickerType.FOLDER_LIST_FOR_IMAGE);
        List<ImageFolder> allImageFolders = imageFileFetchingTask.getAllImageFolder();
        uiUpdateTask.bindImageFolders(allImageFolders);
    }

    public void bindArguments(Bundle arguments)
    {
        this.arguments = arguments;
        isBatchModeEnabled = arguments.getBoolean(ImagePickerTags.BATCH_MODE_ENABLED,false);
    }

    private void fetchPDFFiles()
    {

    }

    public void onStop()
    {
        screenView.unregisterListener(this);
    }

    /*@Override
    public void onFilePickerListItemClicked(String path, ImagePickerType imagePickerType)
    {

        switch (imagePickerType)
        {
            case FILE_LIST_FOR_PDF:
                gotoBatchImageProcessorForPDF(path);
                break;
            case FOLDER_LIST_FOR_IMAGE:
                loadAllImageForFolder(path);
                break;
        }

    }*/


    /*private void loadAllImageForFolder(String folderPath)
    {
        Bundle args = new Bundle();
        args.putString(ImagePickerTags.FOLDER_PATH_TAG,folderPath);
        args.putBoolean(ImagePickerTags.BATCH_MODE_ENABLED,isBatchModeEnabled);
        uiUpdateTask.loadAllImageListForFolder(args);

    }*/





    /*public void onOptionMenuClicked(MenuItem menu)
    {
        int menuId = menu.getItemId();
        switch (menuId)
        {
            case R.id.sort_menu:
                uiUpdateTask.showSortingOptionDialog(this,getSortingSelectionBundle());
                break;
            case R.id.sort_type_menu:
                toggleAscendingDescending(menu);
                break;
        }
    }*/

    /*private Bundle getSortingSelectionBundle()
    {
        Bundle bundle = new Bundle();
        bundle.putBoolean(Tags.TITLE_ASC,titleAsc);
        bundle.putBoolean(Tags.SIZE_ASC,sizeAsc);
        bundle.putBoolean(Tags.TIME_ASC,lastModifiedAsc);
        return bundle;
    }*/

    /*@Override
    public void onFileTitleOptionSelected()
    {
        sortingType = SortingType.FILE_TITLE;
        uiUpdateTask.sortNote(SortingType.FILE_TITLE,isAscending);

    }

    @Override
    public void onFileLastModifiedOptionSelected()
    {
        sortingType = SortingType.FILE_LAST_MODIFIED_TIME;
        uiUpdateTask.sortNote(SortingType.FILE_LAST_MODIFIED_TIME,isAscending);

    }

    @Override
    public void onFileSizeOptionSelected()
    {
        sortingType = SortingType.FILE_SIZE;
        uiUpdateTask.sortNote(SortingType.FILE_SIZE,isAscending);
    }

    private void toggleAscendingDescending(MenuItem menu)
    {
        isAscending = !isAscending;
        uiUpdateTask.toggleSortTypeIcon(isAscending,menu);
        switch (sortingType)
        {
            case FILE_SIZE:
                uiUpdateTask.sortNote(SortingType.FILE_SIZE,isAscending);
                break;
            case FILE_LAST_MODIFIED_TIME:
                uiUpdateTask.sortNote(SortingType.FILE_LAST_MODIFIED_TIME,isAscending);
                break;
            case FILE_TITLE:
                uiUpdateTask.sortNote(SortingType.FILE_TITLE,isAscending);
                break;
        }
    }*/
}
