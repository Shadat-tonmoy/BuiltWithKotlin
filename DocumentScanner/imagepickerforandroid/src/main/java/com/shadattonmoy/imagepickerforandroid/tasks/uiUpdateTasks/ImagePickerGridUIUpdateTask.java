package com.shadattonmoy.imagepickerforandroid.tasks.uiUpdateTasks;

import android.app.Activity;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import com.shadattonmoy.imagepickerforandroid.R;
import com.shadattonmoy.imagepickerforandroid.ui.screenView.ImagePickerGridScreenView;

public class ImagePickerGridUIUpdateTask
{

    private Activity activity;
    private ImagePickerGridScreenView screenView;


    public ImagePickerGridUIUpdateTask(Activity activity) {
        this.activity = activity;
    }

    public void bindView(ImagePickerGridScreenView screenView)
    {
        this.screenView = screenView;
    }

    public void setSelection(int position)
    {
        screenView.getImagePickerGridAdapter().selectItemAtPosition(position);
        updateTotalSelectionText();
    }

    public void bindImages(List<String> imagePaths)
    {
        screenView.getImagePickerGridAdapter().bindImages(imagePaths);
    }

    public void selectAll() 
    {
        screenView.getImagePickerGridAdapter().selectAll();
        screenView.getSelectAllButton().setText(activity.getResources().getString(R.string.select_none));
        updateTotalSelectionText();
    }
    
    public void selectNone() 
    {
        screenView.getImagePickerGridAdapter().selectNone();
        screenView.getSelectAllButton().setText(activity.getResources().getString(R.string.select_all));
        updateTotalSelectionText();
    }
    
    private void updateTotalSelectionText()
    {
        int totalSelectedImage = screenView.getImagePickerGridAdapter().getTotalSelectedImages();
        TextView doneButton = screenView.getDoneButton();
        String selectedText = activity.getString(R.string.total_selected_image,totalSelectedImage);
        screenView.getSelectedText().setText(selectedText);
        if(totalSelectedImage>0)
        {
            doneButton.setTextColor(activity.getResources().getColor(R.color.blaze_hole));
            doneButton.setTypeface(null, Typeface.BOLD);
        }
        else
        {
            screenView.getDoneButton().setTextColor(activity.getResources().getColor(R.color.textDefault));
            doneButton.setTypeface(null, Typeface.NORMAL);
        }

    }

    public void moveToImageProcessingActivity()
    {
        int totalSelectedImage = screenView.getImagePickerGridAdapter().getTotalSelectedImages();
        if(totalSelectedImage==1)
        {
            moveToSingleProcessingActivity();
        }
        else if(totalSelectedImage>1)
        {
            moveToBatchProcessingActivity();
        }
        else Toast.makeText(activity, activity.getString(R.string.no_image_selected), Toast.LENGTH_SHORT).show();


    }

    public void moveToBatchProcessingActivity() 
    {
       /* Intent intent = new Intent(activity, BatchImageScanActivity.class);
        *//*intent.putStringArrayListExtra(AppConstants.BATCH_IMAGE_PATHS,
                (ArrayList<String>) screenView.getFilePickerGridAdapter().getSelectedImages());*//*
        activity.startActivity(intent);
        activity.finish();*/
    }

    public void moveToSingleProcessingActivity()
    {
        /*String selectedImagePath = screenView.getImagePickerGridAdapter().getSelectedImages().get(0);
        Intent intent = new Intent(activity, SingleImageScanActivity.class);
        intent.putExtra(AppConstants.CHOSEN_IMAGE_PATH, selectedImagePath);
        intent.putExtra(AppConstants.IS_OUTPUT_SAVED, true);
        intent.putExtra(AppConstants.CHOSEN_IMAGE_FROM, AppConstants.FROM_CAMERA);
        activity.startActivity(intent);
        activity.finish();*/
    }

    public void bindSelectionBundle(Bundle bundle)
    {
        screenView.getImagePickerGridAdapter().selectFromBundle(bundle);
        updateTotalSelectionText();
    }

    public void enableBatchMode(boolean isBatchModeEnabled)
    {
        screenView.getImagePickerGridAdapter().setSelectMultiple(isBatchModeEnabled);
        /*if(isBatchModeEnabled)
        {
            screenView.getButtonPanel().setVisibility(View.VISIBLE);
        }
        else
        {
            screenView.getButtonPanel().setVisibility(View.GONE);
        }*/

    }

    public void hideLoadingView()
    {
        screenView.getLoadingView().setVisibility(View.GONE);
    }
}
