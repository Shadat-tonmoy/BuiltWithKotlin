package com.shadattonmoy.imagepickerforandroid.ui.screenView;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
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
import com.shadattonmoy.imagepickerforandroid.ui.screen.ImagePickerListScreen;

public class ImagePickerListScreenView extends BaseScreenView<ImagePickerListScreen.Listener>
{

    private RecyclerView folderList;
    private ImagePickerListAdapter imagePickerListAdapter;
    private LinearLayout loadingMessageView,noFileFoundView;
    private SearchView searchView;


    public ImagePickerListScreenView(LayoutInflater layoutInflater, @Nullable ViewGroup parent, ImagePickerType imagePickerType)
    {
        setRootView(layoutInflater.inflate(R.layout.file_picker_list_layout, parent, false));
        this.imagePickerListAdapter = new ImagePickerListAdapter(getContext(),imagePickerType);
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
        folderList = findViewById(R.id.folder_list);
        loadingMessageView = findViewById(R.id.loading_message_container);
        noFileFoundView = findViewById(R.id.no_file_found_view);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        folderList.setLayoutManager(layoutManager);
        folderList.setItemAnimator(new DefaultItemAnimator());
        folderList.setAdapter(imagePickerListAdapter);
    }

    public void onCreateOptionMenu(Menu menu)
    {
//        MenuItem searchMenu = menu.findItem(R.id.search_menu);
//        SearchView searchView = (SearchView) searchMenu.getActionView();
//        this.searchView = searchView;
//        customizeSearchView(searchView);

    }

    private void customizeSearchView(SearchView searchView)
    {
        try
        {
            searchView.setQueryHint("Search Here");
            int searchPlateId = searchView.getContext().getResources().getIdentifier("android:id/search_plate", null, null);
            int searchButtonId = searchView.getContext().getResources().getIdentifier("android:id/search_button", null, null);
            int searchCloseButtonId = searchView.getContext().getResources().getIdentifier("android:id/search_close_btn", null, null);
            View searchPlate = searchView.findViewById(searchPlateId);
            ImageView searchButton = searchView.findViewById(searchButtonId);
            ImageView searchCloseButton = searchView.findViewById(searchCloseButtonId);
            searchButton.setImageDrawable(getContext().getResources().getDrawable(R.drawable.search_white));
            searchCloseButton.setImageDrawable(getContext().getResources().getDrawable(R.drawable.close_white));
            if (searchPlate!=null)
            {
                int searchTextId = searchPlate.getContext().getResources().getIdentifier("android:id/search_src_text", null, null);
                TextView searchText = searchPlate.findViewById(searchTextId);
                if (searchText!=null)
                {
                    searchText.setTextColor(Color.WHITE);
                    searchText.setHintTextColor(Color.WHITE);
                }
            }
        }
        catch (Exception e)
        {

        }

    }

    public RecyclerView getFolderList() {
        return folderList;
    }

    public ImagePickerListAdapter getImagePickerListAdapter() {
        return imagePickerListAdapter;
    }

    public LinearLayout getLoadingMessageView() {
        return loadingMessageView;
    }

    public SearchView getSearchView() {
        return searchView;
    }

    public LinearLayout getNoFileFoundView() {
        return noFileFoundView;
    }
}
