package com.shadattonmoy.imagepickerforandroid.ui.screenView;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.Spinner;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.tabs.TabLayout;
import com.shadattonmoy.imagepickerforandroid.R;
import com.shadattonmoy.imagepickerforandroid.constants.ImagePickerType;
import com.shadattonmoy.imagepickerforandroid.constants.ImagePickerTags;
import com.shadattonmoy.imagepickerforandroid.controller.ImagePickerFolderContainerController;
import com.shadattonmoy.imagepickerforandroid.ui.adapters.ViewPagerAdapter;
import com.shadattonmoy.imagepickerforandroid.ui.fragments.ImagePickerFolderContainerFragment;
import com.shadattonmoy.imagepickerforandroid.ui.fragments.ImagePickerGridFragment;
import com.shadattonmoy.imagepickerforandroid.ui.fragments.ImagePickerListFragment;
import com.shadattonmoy.imagepickerforandroid.ui.screen.ImagePickerActivityScreen;

public class ImagePickerActivityScreenView extends BaseScreenView<ImagePickerActivityScreen.Listener> implements ImagePickerActivityScreen
{


    private MaterialToolbar toolbar;
    private FrameLayout fragmentContainer;
    private Spinner fileOptionSpinner;
    private ImagePickerType imagePickerType;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private ImagePickerGridFragment allImageFragment;
    private ImagePickerFolderContainerFragment imageFolderFragment;
    private ViewPagerAdapter viewPagerAdapter;

    public ImagePickerActivityScreenView(LayoutInflater layoutInflater, @Nullable ViewGroup parent, FragmentManager fragmentManager)
    {
        viewPagerAdapter = new ViewPagerAdapter(fragmentManager);
        setRootView(layoutInflater.inflate(R.layout.image_picker_layout, parent, false));
        inflateUIElements();
        initUserInteractions();
    }


    @Override
    public void initUserInteractions()
    {
        fileOptionSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                for(Listener listener:getListeners())
                {
                    listener.onSpinnerItemSelected(position);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent)
            {

            }
        });

    }

    @Override
    public void inflateUIElements()
    {
        toolbar = findViewById(R.id.image_picker_toolbar);
        fragmentContainer = findViewById(R.id.fragment_container);
        toolbar.setTitle(getContext().getResources().getString(R.string.pick_image));
        toolbar.setNavigationIcon(R.drawable.back_white);
        fileOptionSpinner = findViewById(R.id.toolbar_spinner);
        tabLayout = findViewById(R.id.image_picker_tab_layout);
        viewPager = findViewById(R.id.image_picker_view_pager);
        setupTabLayout();

    }

    private void setupTabLayout()
    {
        Bundle allImageArgs = new Bundle();
        allImageArgs.putSerializable(ImagePickerTags.IMAGE_PICKER_TYPE,ImagePickerType.ALL_IMAGE_LIST);
        allImageArgs.putBoolean(ImagePickerTags.BATCH_MODE_ENABLED,true);
        allImageFragment = ImagePickerGridFragment.newInstance(allImageArgs);
        imageFolderFragment = ImagePickerFolderContainerFragment.newInstance(new Bundle());
        viewPagerAdapter.addFragment(allImageFragment,getContext().getResources().getString(R.string.all_images));
        viewPagerAdapter.addFragment(imageFolderFragment,getContext().getResources().getString(R.string.folders));
        viewPager.setAdapter(viewPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
        viewPager.setOffscreenPageLimit(10);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                for(Listener listener : getListeners())
                {
                    listener.onTabSelected(tab.getPosition());
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    public MaterialToolbar getToolbar() {
        return toolbar;
    }

    public void initToolbarAction()
    {
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for(ImagePickerActivityScreen.Listener listener:getListeners())
                    listener.onBackButtonClicked();
            }
        });
    }

    public FrameLayout getFragmentContainer() {
        return fragmentContainer;
    }

    public int getFragmentContainerID() {
        return R.id.fragment_container;
    }

    public Spinner getFileOptionSpinner() {
        return fileOptionSpinner;
    }

    public void setFilePickerType(ImagePickerType imagePickerType) {
        this.imagePickerType = imagePickerType;
    }
}
