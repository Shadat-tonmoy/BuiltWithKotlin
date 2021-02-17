package com.shadattonmoy.imagepickerforandroid.ui.adapters;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.shadattonmoy.imagepickerforandroid.R;
import com.shadattonmoy.imagepickerforandroid.constants.Constants;
import com.shadattonmoy.imagepickerforandroid.constants.ImagePickerTags;
import com.shadattonmoy.imagepickerforandroid.model.ImageFile;

import static com.shadattonmoy.imagepickerforandroid.tasks.UtilityTask.isAndroidX;


public class ImagePickerGridAdapter extends RecyclerView.Adapter<ImagePickerGridAdapter.ImagePickerGridViewHolder>
{

    public interface Listener{
        void onImageFileClicked(int position, ImageFile imageFile);
    }

    private static final String TAG = "ImagePickerGridAdapter";
    private Context context;
    private List<ImageFile> imageFiles, selectedImageFiles;
    private Map<Integer, Boolean> selectedPositions;
    private Map<Integer, Integer> selectionIds;
    private boolean selectMultiple = false;
    private int selectionCount = 1;
    private Listener listener;

    public ImagePickerGridAdapter(Context context) {
        this.context = context;
        this.imageFiles = new ArrayList<>();
        this.selectedImageFiles = new ArrayList<>();
        this.selectedPositions = new HashMap<>();
        this.selectionIds= new HashMap<>();
    }

    @NonNull
    @Override
    public ImagePickerGridViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.file_picker_single_cell, parent, false);
        return new ImagePickerGridViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ImagePickerGridViewHolder viewHolder, int position)
    {
        ImageFile imageFile = imageFiles.get(position);
        if(imageFile != null)
        {
            Uri imageFileUri = null;
            if(isAndroidX())
            {
                imageFileUri = Uri.parse(imageFile.getPathString());
            }
            else
            {
                File file = new File(imageFile.getPathString());
                imageFileUri = Uri.fromFile(file);
            }
            Log.e(TAG, "onBindViewHolder: imageFileUri : "+imageFileUri );

            Glide.with(context)
                    .load(imageFileUri)
                    .into(viewHolder.imageView);
            viewHolder.imageTitle.setText(imageFile.getDisplayName());
            viewHolder.itemView.setOnClickListener(view -> {
                if(listener != null)
                {
                    listener.onImageFileClicked(position,imageFile);
                }
            });
            if(selectMultiple)
            {
                if(selectedPositions != null)
                {
                    Boolean selectionFlag = selectedPositions.get(position);
                    if(selectionFlag == null) selectionFlag = false;
                    viewHolder.selectionCountView.setVisibility(View.VISIBLE);
                    if(selectionFlag)
                    {
                        if(selectionIds!=null && selectionIds.get(position)!=null)
                        {

                            viewHolder.selectionCountView.setText(selectionIds.get(position)+ Constants.EMPTY_TEXT);
                            viewHolder.selectionCountView.setBackgroundResource(R.drawable.image_selection_background);
                        }
                    }
                    else
                    {
                        viewHolder.selectionCountView.setBackgroundResource(R.drawable.circle_background_trans);
                        viewHolder.selectionCountView.setText(Constants.EMPTY_TEXT);
                    }
                }

            }
            else
            {
                viewHolder.selectionCountView.setVisibility(View.GONE);
            }
        }


    }

    @Override
    public int getItemCount() {
        if(imageFiles !=null)
            return imageFiles.size();
        else return 0;
    }

    public void bindImages(List<ImageFile> imageFiles)
    {
        this.imageFiles = imageFiles;
        notifyDataSetChanged();
    }

    public void selectItemAtPosition(int position)
    {
        boolean currentSelection = true;
        Boolean selectionFlag = selectedPositions.get(position);
        if(selectionFlag !=null )
        {
            currentSelection = selectionFlag;
            currentSelection = !currentSelection;
        }
        selectedPositions.put(position,currentSelection);
        ImageFile imageFileAtPosition = imageFiles.get(position);
        if(currentSelection)
        {
            if(!selectedImageFiles.contains(imageFileAtPosition))
            {
                selectedImageFiles.add(imageFileAtPosition);
            }
            incrementSelectionCount(position);
        }
        else
        {
            if(selectedImageFiles.contains(imageFileAtPosition))
            {
                selectedImageFiles.remove(imageFileAtPosition);
            }
            decrementSelectionCount(position);
        }
        notifyDataSetChanged();
    }

    private void incrementSelectionCount(int position)
    {
        selectionIds.put(position,selectionCount++);
    }

    private void decrementSelectionCount(int position)
    {
        if(selectionIds!=null && selectionIds.get(position)!=null)
        {
            int selectionCountAtPosition = selectionIds.get(position);
            for(Map.Entry<Integer, Integer> entry : selectionIds.entrySet())
            {
                int key = entry.getKey();
                int value = entry.getValue();
                if(value > selectionCountAtPosition)
                {
                    selectionIds.put(key,value-1);
                }
            }
            selectionCount--;
        }
    }

    public List<ImageFile> getSelectedImageFiles() {
        return selectedImageFiles;
    }

    public ImageFile getImageAtPosition(int position)
    {
        return imageFiles.get(position);
    }

    public int getTotalSelectedImages()
    {
        if(selectedImageFiles ==null)
            return 0;
        else return selectedImageFiles.size();
    }

    public void selectAll()
    {
        selectedImageFiles = new ArrayList<>();
        selectionIds = new HashMap<>();
        for(int i = 0; i< imageFiles.size(); i++)
        {
            selectedImageFiles.add(imageFiles.get(i));
            selectedPositions.put(i,true);
            selectionIds.put(i,i+1);
            selectionCount = i+1;
        }
        selectionCount++;
        notifyDataSetChanged();

    }

    public void selectNone()
    {
        selectedImageFiles = new ArrayList<>();
        selectedPositions = new HashMap<>();
        selectionIds = new HashMap<>();
        resetSelectionCount();
        notifyDataSetChanged();
    }

    public void selectFromBundle(Bundle bundle)
    {
        ArrayList<ImageFile> selectedImages = (ArrayList<ImageFile>) bundle.getSerializable(ImagePickerTags.SELECTED_IMAGES);
        HashMap<Integer, Boolean> selectedPositions = (HashMap<Integer, Boolean>) bundle.getSerializable(ImagePickerTags.SELECTED_IMAGES_POSITIONS);
        HashMap<Integer, Integer> selectionIds = (HashMap<Integer, Integer>) bundle.getSerializable(ImagePickerTags.SELECTED_IMAGES_IDS);
        this.selectedImageFiles = selectedImages == null ? new ArrayList<>() : selectedImages;
        this.selectedPositions = selectedPositions == null ? new HashMap<>() : selectedPositions;
        this.selectionIds = selectionIds == null ? new HashMap<>() : selectionIds;
        if(selectedImages!=null)
            this.selectionCount = selectedImages.size()+1;
        /*resetSelectionCount();*/
        notifyDataSetChanged();
    }

    public void setSelectMultiple(boolean selectMultiple)
    {
        this.selectMultiple = selectMultiple;
        if(!selectMultiple)
            selectNone();
        resetSelectionCount();
    }

    public List<ImageFile> getImageFiles() {
        return imageFiles;
    }

    public void resetSelectionCount()
    {
        selectionCount = 1;
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    public class ImagePickerGridViewHolder extends RecyclerView.ViewHolder
    {
        ImageView imageView;
        TextView selectionCountView,imageTitle;

        public ImagePickerGridViewHolder(@NonNull View itemView)
        {
            super(itemView);
            imageView=  itemView.findViewById(R.id.image_view);
            selectionCountView =  itemView.findViewById(R.id.selection_count_view);
            imageTitle =  itemView.findViewById(R.id.image_title);

        }
    }


}
