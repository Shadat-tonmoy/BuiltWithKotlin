package com.shadattonmoy.imagepickerforandroid.ui.actvities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;


import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.shadattonmoy.imagepickerforandroid.ImagePickerForAndroid;
import com.shadattonmoy.imagepickerforandroid.R;
import com.shadattonmoy.imagepickerforandroid.constants.ImagePickerType;
import com.shadattonmoy.imagepickerforandroid.constants.ImagePickerTags;
import com.shadattonmoy.imagepickerforandroid.controller.ImagePickerActivityController;
import com.shadattonmoy.imagepickerforandroid.ui.fragments.ImagePickerGridFragment;
import com.shadattonmoy.imagepickerforandroid.ui.fragments.ImagePickerListFragment;
import com.shadattonmoy.imagepickerforandroid.ui.screenView.ImagePickerActivityScreenView;

public class ImagePickerActivity extends AppCompatActivity
{
    private static final String TAG = "ImagePickerActivity";

    private ImagePickerActivityController controller;
    private Activity activity;
    private ImagePickerActivityScreenView screenView;
    private static ImagePickerForAndroid imagePickerForAndroid;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    private void init()
    {
        activity = this;
        ImagePickerType imagePickerType = getFilePickerType();
        screenView = new ImagePickerActivityScreenView(activity.getLayoutInflater(),null,getSupportFragmentManager());
        controller = new ImagePickerActivityController(activity);
        controller.bindView(screenView);
        setActionBar();
        setContentView(screenView.getRootView());
        initSelectionBundle();
        controller.onCreate(getIntent(),imagePickerType);
        controller.setupToolbar(getIntent());
        controller.setImagePickerForAndroid(imagePickerForAndroid);


    }

    private void setActionBar()
    {
        setSupportActionBar(screenView.getToolbar());
        /*ActionBar actionBar = getSupportActionBar();
        if(actionBar!=null)
        {
            actionBar.hide();
        }
        else
        {
            setSupportActionBar(screenView.getToolbar());
        }*/
    }

    private void initSelectionBundle()
    {
        Bundle bundle = getIntent().getExtras();
        if(bundle!=null)
        {
            controller.bindSelectionBundle(bundle);
        }
        else
        {
            controller.bindSelectionBundle(new Bundle());
        }
    }

    private ImagePickerType getFilePickerType()
    {
        Intent intent = getIntent();
        ImagePickerType imagePickerType = ImagePickerType.FOLDER_LIST_FOR_IMAGE;
        if(intent!=null && intent.getExtras()!=null && intent.hasExtra(ImagePickerTags.IMAGE_PICKER_TYPE))
        {
            imagePickerType = (ImagePickerType) intent.getExtras().get(ImagePickerTags.IMAGE_PICKER_TYPE);
        }
        return imagePickerType;
    }

    @Override
    protected void onStart() {
        super.onStart();
        controller.onStart();
    }

    @Override
    protected void onStop() {
        controller.onStop();
        super.onStop();
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        controller.initToolbarAction();

    }

    /*@Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.image_picker_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        Log.e(TAG, "onOptionsItemSelected: Called for "+item);
        return super.onOptionsItemSelected(item);
    }*/

    public ImagePickerActivityScreenView getScreenView() {
        return screenView;
    }

    public ImagePickerActivityController getController() {
        return controller;
    }

    public static void setImagePickerForAndroid(ImagePickerForAndroid imagePickerForAndroid)
    {
        ImagePickerActivity.imagePickerForAndroid = imagePickerForAndroid;
    }
}
