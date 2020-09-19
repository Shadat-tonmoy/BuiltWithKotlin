package com.shadattonmoy.imagepickerforandroid.controller;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.shadattonmoy.imagepickerforandroid.ImagePickerForAndroid;
import com.shadattonmoy.imagepickerforandroid.constants.ImagePickerType;
import com.shadattonmoy.imagepickerforandroid.constants.ImagePickerTags;
import com.shadattonmoy.imagepickerforandroid.tasks.uiUpdateTasks.ImagePickerUIUpdateTask;
import com.shadattonmoy.imagepickerforandroid.ui.fragments.ImagePickerGridFragment;
import com.shadattonmoy.imagepickerforandroid.ui.fragments.ImagePickerListFragment;
import com.shadattonmoy.imagepickerforandroid.ui.screen.ImagePickerActivityScreen;
import com.shadattonmoy.imagepickerforandroid.ui.screenView.ImagePickerActivityScreenView;

import java.util.List;

public class ImagePickerActivityController implements ImagePickerActivityScreen.Listener
{

    private static final String TAG = "ImagePickerActivityCont";
    private Activity activity;
    private ImagePickerActivityScreenView screenView;
    private ImagePickerUIUpdateTask uiUpdateTask;
    private boolean firstStart = true, isBatchModeEnabled = false;
    private ImagePickerType imagePickerType = ImagePickerType.FOLDER_LIST_FOR_IMAGE;
    private Bundle arguments;
    private ImagePickerForAndroid imagePickerForAndroid;

    public ImagePickerActivityController(Activity activity)
    {
        this.activity = activity;
        this.uiUpdateTask = new ImagePickerUIUpdateTask(activity);
    }

    public void bindView(ImagePickerActivityScreenView screenView)
    {
        this.screenView = screenView;
        uiUpdateTask.bindView(this.screenView);
    }

    public void onCreate(Intent extras, ImagePickerType imagePickerType)
    {
        this.imagePickerType = imagePickerType;
        this.isBatchModeEnabled = extras.getBooleanExtra(ImagePickerTags.BATCH_MODE_ENABLED,false);
        arguments.putSerializable(ImagePickerTags.IMAGE_PICKER_TYPE, imagePickerType);
        arguments.putBoolean(ImagePickerTags.BATCH_MODE_ENABLED, isBatchModeEnabled);
//        uiUpdateTask.setToolbarSpinner();
//        openImagePickerGridFragment();
    }

    public void setupToolbar(Intent toolbarProperties)
    {
        uiUpdateTask.setupToolbar(toolbarProperties);

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
    public void onBackButtonClicked()
    {
        uiUpdateTask.onBackButtonClicked();

    }

    public void onImageListSelected(List<String> selectedImageList)
    {
        imagePickerForAndroid.onImageListSelected(selectedImageList);
        activity.finish();
    }

    public void onSingleImageSelected(String selectedImage)
    {
        imagePickerForAndroid.onSingleImageSelected(selectedImage);
        activity.finish();
    }

    @Override
    public void onTabSelected(int position)
    {
        Log.e(TAG, "onTabSelected: Position "+position);
        if(position==1)
            uiUpdateTask.handleAllButtonVisibility(false);
        else uiUpdateTask.handleAllButtonVisibility(true);
    }

    @Override
    public void onSpinnerItemSelected(int position)
    {

        if(!firstStart)
        {
            loadFragmentForSpinner(position);
        }
        if(firstStart)
        {
            firstStart = false;
        }
    }

    private void loadFragmentForSpinner(int position)
    {
        /*switch (position)
        {
            case 0:
                openImagePickerGridFragment();
                break;
            case 1:
                openImagePickerListFragment();
                break;
        }*/
    }

    private void openImagePickerListFragment()
    {
        /*Bundle args = new Bundle();
        args.putSerializable(ImagePickerTags.IMAGE_PICKER_TYPE,imagePickerType);
        if(arguments!=null)
        {
            args.putBoolean(ImagePickerTags.BATCH_MODE_ENABLED,arguments.getBoolean(ImagePickerTags.BATCH_MODE_ENABLED,false));
        }
        uiUpdateTask
                .replaceFragment(ImagePickerListFragment.newInstance(args), ImagePickerTags.IMAGE_PICKER_LIST_FRAGMENT);*/
    }

    private void openImagePickerGridFragment()
    {
        uiUpdateTask
                .replaceFragment(ImagePickerGridFragment.newInstance(arguments), ImagePickerTags.IMAGE_PICKER_LIST_FRAGMENT);
    }

    public void bindSelectionBundle(Bundle bundle)
    {
        this.arguments = bundle;
    }

    public void initToolbarAction()
    {
        screenView.initToolbarAction();
    }

    public void setImagePickerForAndroid(ImagePickerForAndroid imagePickerForAndroid) {
        this.imagePickerForAndroid = imagePickerForAndroid;
    }
}
