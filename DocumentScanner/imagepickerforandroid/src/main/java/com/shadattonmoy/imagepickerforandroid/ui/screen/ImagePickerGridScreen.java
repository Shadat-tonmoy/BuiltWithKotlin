package com.shadattonmoy.imagepickerforandroid.ui.screen;

import com.shadattonmoy.imagepickerforandroid.model.ImageFile;

public interface ImagePickerGridScreen {

    interface Listener
    {
        void onImageFileClicked(int position, ImageFile imageFile);

        void onDoneButtonClicked();

        void onSelectAllButtonClicked();

    }
}
