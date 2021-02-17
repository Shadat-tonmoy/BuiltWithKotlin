package com.shadattonmoy.imagepickerforandroid;

import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;

import com.shadattonmoy.imagepickerforandroid.constants.ImagePickerTags;
import com.shadattonmoy.imagepickerforandroid.model.ImageFile;
import com.shadattonmoy.imagepickerforandroid.ui.actvities.ImagePickerActivity;

import java.util.List;

import static com.shadattonmoy.imagepickerforandroid.constants.Constants.INVALID;

public class ImagePickerForAndroid
{

    public interface BatchImageSelectionListener
    {
        void onBatchImageSelected(List<ImageFile> selectedImageList);
    }

    public interface SingleImageSelectionListener
    {
        void onSingleImageSelected(ImageFile selectedImage);
    }

    private static final String TAG = "ImagePickerForAndroid";
    private int toolbarColor = INVALID;
    private int statusBarColor = INVALID;
    private int navigationIcon = INVALID;
    private boolean isBatchModeEnabled = false;
    private BatchImageSelectionListener batchImageSelectionListener;
    private SingleImageSelectionListener singleImageSelectionListener;
    private Context context;

    public void openImagePicker()
    {
        Intent intent = new Intent(context, ImagePickerActivity.class);
        intent.putExtra(ImagePickerTags.TOOLBAR_COLOR,toolbarColor);
        intent.putExtra(ImagePickerTags.STATUS_BAR_COLOR,statusBarColor);
        intent.putExtra(ImagePickerTags.NAVIGATION_ICON,navigationIcon);
        intent.putExtra(ImagePickerTags.BATCH_MODE_ENABLED,isBatchModeEnabled);
        ImagePickerActivity.setImagePickerForAndroid(this);
        context.startActivity(intent);
    }

    private ImagePickerForAndroid(Builder builder)
    {
        this.toolbarColor = builder.toolbarColor;
        this.statusBarColor = builder.statusBarColor;
        this.navigationIcon = builder.navigationIcon;
        this.isBatchModeEnabled = builder.isBatchModeEnabled;
        this.batchImageSelectionListener = builder.batchImageSelectionListener;
        this.singleImageSelectionListener = builder.singleImageSelectionListener;
        this.context = builder.context;
    }


    public static class Builder
    {
        private int toolbarColor,statusBarColor,navigationIcon;
        private boolean isBatchModeEnabled;
        private BatchImageSelectionListener batchImageSelectionListener;
        private SingleImageSelectionListener singleImageSelectionListener;
        private Context context;

        public Builder(Context context)
        {
            this.context = context;
            toolbarColor = INVALID;
            statusBarColor = INVALID;
            navigationIcon = R.drawable.back_white;
            isBatchModeEnabled = false;
        }

        public Builder toolbarColor(int toolbarColor)
        {
            this.toolbarColor = toolbarColor;
            return this;
        }

        public Builder statusBarColor(int statusBarColor)
        {
            this.statusBarColor = statusBarColor;
            return this;
        }

        public Builder navigationIcon(int navigationIcon)
        {
            this.navigationIcon = navigationIcon;
            return this;
        }

        public Builder batchMode(boolean value)
        {
            this.isBatchModeEnabled = value;
            return this;
        }

        public Builder batchImageSelectionListener(@NonNull BatchImageSelectionListener listener)
        {
            this.batchImageSelectionListener = listener;
            return this;
        }

        public Builder singleImageSelectionListener(@NonNull SingleImageSelectionListener listener)
        {
            this.singleImageSelectionListener = listener;
            return this;
        }

        public ImagePickerForAndroid build()
        {
            return new ImagePickerForAndroid(this);
        }
    }


    public void onImageListSelected(List<ImageFile> selectedImageList)
    {
        if(batchImageSelectionListener !=null)
            batchImageSelectionListener.onBatchImageSelected(selectedImageList);
    }

    public void onSingleImageSelected(ImageFile selectedImage)
    {
        if(singleImageSelectionListener !=null)
            singleImageSelectionListener.onSingleImageSelected(selectedImage);
    }
}
