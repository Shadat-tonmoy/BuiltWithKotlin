package com.shadattonmoy.imagepickerforandroid.tasks.uiUpdateTasks;

import android.app.Activity;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.shadattonmoy.imagepickerforandroid.constants.ImagePickerTags;
import com.shadattonmoy.imagepickerforandroid.ui.actvities.ImagePickerActivity;
import com.shadattonmoy.imagepickerforandroid.ui.fragments.ImagePickerGridFragment;
import com.shadattonmoy.imagepickerforandroid.ui.screenView.ImagePickerFolderContainerScreenView;

public class ImagePickerFolderContainerUIUpdateTask
{
    private Activity activity;
    private ImagePickerFolderContainerScreenView screenView;

    public ImagePickerFolderContainerUIUpdateTask(Activity activity)
    {
        this.activity = activity;
    }

    public void bindView(ImagePickerFolderContainerScreenView screenView) {
        this.screenView= screenView;
    }


    public void replaceFragment(Fragment fragment, String fragmentTag)
    {
        FragmentManager fragmentManager = ((AppCompatActivity)activity).getSupportFragmentManager();

        fragmentManager.beginTransaction()
                .replace(screenView.getFragmentContainerID(),fragment,fragmentTag)
                .commitAllowingStateLoss();

    }

    public void loadAllImageListForFolder(Bundle argument)
    {
        ((AppCompatActivity)activity).getSupportFragmentManager()
                .beginTransaction()
                .replace(screenView.getFragmentContainerID(),
                        ImagePickerGridFragment.newInstance(argument), ImagePickerTags.IMAGE_PICKER_LIST_FRAGMENT)
                .addToBackStack(ImagePickerTags.IMAGE_PICKER_LIST_FRAGMENT)
                .commitAllowingStateLoss();
    }

    public void onBackButtonClicked()
    {
        FragmentManager fragmentManager = ((AppCompatActivity)activity).getSupportFragmentManager();
        if(fragmentManager.getBackStackEntryCount()==0)
            activity.finish();
        else fragmentManager.popBackStack();
    }
}
