package com.shadattonmoy.imagepickerforandroid.ui.screenView;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.shadattonmoy.imagepickerforandroid.R;
import com.shadattonmoy.imagepickerforandroid.constants.ImagePickerType;
import com.shadattonmoy.imagepickerforandroid.ui.adapters.ImagePickerListAdapter;
import com.shadattonmoy.imagepickerforandroid.ui.screen.ImagePickerFolderContainerScreen;
import com.shadattonmoy.imagepickerforandroid.ui.screen.ImagePickerListScreen;

public class ImagePickerFolderContainerScreenView extends BaseScreenView<ImagePickerFolderContainerScreen.Listener>
{
    private FrameLayout fragmentContainer;


    public ImagePickerFolderContainerScreenView(LayoutInflater layoutInflater, @Nullable ViewGroup parent, ImagePickerType imagePickerType)
    {
        setRootView(layoutInflater.inflate(R.layout.image_picker_folder_container_layout, parent, false));
        inflateUIElements();
        initUserInteractions();
    }

    @Override
    public void initUserInteractions()
    {

    }

    @Override
    public void inflateUIElements()
    {
        fragmentContainer = findViewById(R.id.container);
    }

    public void onCreateOptionMenu(Menu menu)
    {
        /*MenuItem searchMenu = menu.findItem(R.id.search_menu);
        SearchView searchView = (SearchView) searchMenu.getActionView();
        this.searchView = searchView;
        customizeSearchView(searchView);*/

    }

    public FrameLayout getFragmentContainer() {
        return fragmentContainer;

    }
    public int getFragmentContainerID() {
        return R.id.container;
    }
}
