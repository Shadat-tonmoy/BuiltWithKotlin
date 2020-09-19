package com.shadattonmoy.imagepickerforandroid.ui.screen;

public interface ImagePickerGridScreen {

    interface Listener
    {
        void onImageGridItemClicked(int position);

        void onDoneButtonClicked();

        void onSelectAllButtonClicked();

    }
}
