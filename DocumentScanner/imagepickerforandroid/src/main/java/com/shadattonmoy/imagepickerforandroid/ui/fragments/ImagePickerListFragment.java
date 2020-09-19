package com.shadattonmoy.imagepickerforandroid.ui.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.shadattonmoy.imagepickerforandroid.constants.ImagePickerType;
import com.shadattonmoy.imagepickerforandroid.constants.ImagePickerTags;
import com.shadattonmoy.imagepickerforandroid.controller.ImagePickerListController;
import com.shadattonmoy.imagepickerforandroid.ui.adapters.ImagePickerListAdapter;
import com.shadattonmoy.imagepickerforandroid.ui.screenView.ImagePickerListScreenView;

public class ImagePickerListFragment extends Fragment
{

    private ImagePickerListScreenView screenView;
    private ImagePickerListController controller;
    private ImagePickerListAdapter.Listener listItemListener;
    private Activity activity;

    public static ImagePickerListFragment newInstance(Bundle args, ImagePickerListAdapter.Listener listener)
    {
        ImagePickerListFragment fragment = new ImagePickerListFragment();
        fragment.setArguments(args);
        fragment.setListItemListener(listener);
        return fragment;
    }

    @Override
    public void onAttach(Context context)
    {
        super.onAttach(context);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(false);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        activity = requireActivity();
        ImagePickerType imagePickerType = getImagePickerType();
        screenView = new ImagePickerListScreenView(activity.getLayoutInflater(),container, imagePickerType);
        controller = new ImagePickerListController(activity);
        controller.bindView(screenView);
        controller.onCreate();
        if(getArguments()!=null) controller.bindArguments(getArguments());
        screenView.getImagePickerListAdapter().setListener(listItemListener);
        return screenView.getRootView();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        /*inflater.inflate(R.menu.pdf_file_picker_menu,menu);
        screenView.onCreateOptionMenu(menu);
        screenView.getSearchView().setOnQueryTextListener(controller);*/
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        /*controller.onOptionMenuClicked(item);*/
        return true;
    }

    @Override
    public void onStart() {
        super.onStart();
        controller.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        controller.onStop();
    }

    private ImagePickerType getImagePickerType()
    {
        ImagePickerType imagePickerType = ImagePickerType.FOLDER_LIST_FOR_IMAGE;
        if(getArguments()!=null)
        {
            imagePickerType = (ImagePickerType) getArguments().getSerializable(ImagePickerTags.IMAGE_PICKER_TYPE);
            if(imagePickerType == ImagePickerType.FILE_LIST_FOR_PDF)
                setHasOptionsMenu(true);
            else setHasOptionsMenu(false);
        }
        return imagePickerType;
    }

    public void setListItemListener(ImagePickerListAdapter.Listener listItemListener) {
        this.listItemListener = listItemListener;
    }
}
