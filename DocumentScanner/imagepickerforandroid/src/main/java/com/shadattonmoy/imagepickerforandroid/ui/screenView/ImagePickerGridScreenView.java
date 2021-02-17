package com.shadattonmoy.imagepickerforandroid.ui.screenView;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.shadattonmoy.imagepickerforandroid.R;
import com.shadattonmoy.imagepickerforandroid.constants.EventType;
import com.shadattonmoy.imagepickerforandroid.ui.adapters.ImagePickerGridAdapter;
import com.shadattonmoy.imagepickerforandroid.ui.screen.ImagePickerGridScreen;

public class ImagePickerGridScreenView extends BaseScreenView<ImagePickerGridScreen.Listener>
{

    private RecyclerView imageGrid;
    private ImagePickerGridAdapter imagePickerGridAdapter;
    private TextView doneButton, selectAllButton, selectedText;
    private LinearLayout buttonPanel;
    private RelativeLayout loadingView;
    private FloatingActionButton forwardButton;

    public ImagePickerGridScreenView(LayoutInflater layoutInflater, @Nullable ViewGroup parent)
    {
        setRootView(layoutInflater.inflate(R.layout.file_picker_grid_layout,parent,false));
        this.imagePickerGridAdapter = new ImagePickerGridAdapter(getContext());
        inflateUIElements();
        initUserInteractions();
    }


    @Override
    public void initUserInteractions()
    {
        setClickListener(doneButton, EventType.DONE_BUTTON_CLICKED);
        setClickListener(selectAllButton,EventType.SELECT_ALL_BUTTON_CLICKED);
        setClickListener(forwardButton,EventType.DONE_BUTTON_CLICKED);


    }

    private void setClickListener(View view, final EventType eventType)
    {
        view.setOnClickListener(v -> {
            for(ImagePickerGridScreen.Listener listener:getListeners())
            {
                switch (eventType)
                {
                    case SELECT_ALL_BUTTON_CLICKED:
                        listener.onSelectAllButtonClicked();
                        break;
                    case DONE_BUTTON_CLICKED:
                        listener.onDoneButtonClicked();
                        break;
                }
            }
        });
    }

    @Override
    public void inflateUIElements()
    {
        imageGrid = findViewById(R.id.image_grid_view);
        doneButton = findViewById(R.id.done_button);
        forwardButton = findViewById(R.id.forward_fab);
        selectAllButton = findViewById(R.id.select_all_button);
        selectedText = findViewById(R.id.total_selected_text);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getContext(),2);
        imageGrid.setLayoutManager(layoutManager);
        imageGrid.setAdapter(imagePickerGridAdapter);
        buttonPanel = findViewById(R.id.button_panel);
        loadingView = findViewById(R.id.loading_view);

    }

    public RecyclerView getImageGrid() {
        return imageGrid;
    }

    public ImagePickerGridAdapter getImagePickerGridAdapter() {
        return imagePickerGridAdapter;
    }

    public TextView getDoneButton() {
        return doneButton;
    }

    public TextView getSelectAllButton() {
        return selectAllButton;
    }

    public TextView getSelectedText() {
        return selectedText;
    }

    public LinearLayout getButtonPanel() {
        return buttonPanel;
    }

    public RelativeLayout getLoadingView() {
        return loadingView;
    }
}
