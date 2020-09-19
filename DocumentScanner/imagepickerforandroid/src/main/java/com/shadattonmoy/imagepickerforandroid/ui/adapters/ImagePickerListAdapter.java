package com.shadattonmoy.imagepickerforandroid.ui.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.shadattonmoy.imagepickerforandroid.R;
import com.shadattonmoy.imagepickerforandroid.constants.ImagePickerType;
import com.shadattonmoy.imagepickerforandroid.model.ImageFolder;
import com.shadattonmoy.imagepickerforandroid.tasks.UtilityTask;
import com.shadattonmoy.imagepickerforandroid.tasks.filteringTask.FileFilteringTask;
import com.shadattonmoy.imagepickerforandroid.tasks.filteringTask.FileSortingTask;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ImagePickerListAdapter extends RecyclerView.Adapter<ImagePickerListAdapter.ListViewHolder> implements FileFilteringTask.Listener, FileSortingTask.Listener {


    public interface Listener
    {
        void onFilePickerListItemClicked(String filePickerItemPath, ImagePickerType imagePickerType);
    }

    private List<ImageFolder> imageFolders;
    private List<String> pdfFilePaths, filteredPdfFilePaths;
    private Context context;
    private Listener listener;
    private ImagePickerType imagePickerType;
    private FileFilteringTask fileFilteringTask;
    private FileSortingTask fileSortingTask;


    public class ListViewHolder extends RecyclerView.ViewHolder {
        TextView folderTitle,folderPath;
        ConstraintLayout rootView;
        ImageView listItemIcon;

        public ListViewHolder(View view)
        {
            super(view);
            folderTitle =  view.findViewById(R.id.list_item_title);
            folderPath =  view.findViewById(R.id.list_item_path);
            rootView =  view.findViewById(R.id.root_view);
            listItemIcon =  view.findViewById(R.id.list_item_icon);
        }
    }


    public ImagePickerListAdapter(Context context, ImagePickerType imagePickerType) {
        this.context = context;
        this.imageFolders = new ArrayList<>();
        this.pdfFilePaths = new ArrayList<>();
        this.fileFilteringTask = new FileFilteringTask(filteredPdfFilePaths, pdfFilePaths);
        fileSortingTask = new FileSortingTask(getFilesFromPaths(pdfFilePaths),this);
        fileFilteringTask.setListener(this);
        this.imagePickerType = imagePickerType;
    }

    @NonNull
    @Override
    public ListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.file_picker_list_single_row, parent, false);

        return new ListViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ListViewHolder holder, int position)
    {
        if(imagePickerType == ImagePickerType.FOLDER_LIST_FOR_IMAGE)
        {
            final ImageFolder imageFolder = imageFolders.get(position);
            holder.folderTitle.setText(imageFolder.getFolderName());
            holder.folderPath.setText(UtilityTask.trimString(imageFolder.getFolderPath()));
            Glide.with(context)
                    .load(imageFolder.getFirstImagePath())
                    .into(holder.listItemIcon);
            holder.rootView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(listener!=null)
                    {
                        listener.onFilePickerListItemClicked(imageFolder.getFolderPath(), imagePickerType);
                    }
                }
            });
        }
        else if(imagePickerType == ImagePickerType.FILE_LIST_FOR_PDF)
        {
            final String pdfFilePath = pdfFilePaths.get(position);
            String pdfFileName = new File(pdfFilePath).getName();
            holder.folderPath.setText(UtilityTask.trimString(pdfFilePath));
            holder.folderTitle.setText(UtilityTask.trimString(pdfFileName));
            holder.listItemIcon.setImageDrawable(context.getResources().getDrawable(R.drawable.pdf_icon));
            holder.rootView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(listener!=null)
                    {
                        listener.onFilePickerListItemClicked(pdfFilePath, imagePickerType);
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount()
    {
        if(imagePickerType == ImagePickerType.FOLDER_LIST_FOR_IMAGE)
            return imageFolders.size();
        else if(imagePickerType == ImagePickerType.FILE_LIST_FOR_PDF)
            return pdfFilePaths.size();
        else return 0;
    }

    public void bindImageFolders(List<ImageFolder> folderList)
    {
        this.imageFolders = folderList;
        notifyDataSetChanged();
    }

    public void bindPDFFiles(List<String> pdfFilePaths)
    {
        this.pdfFilePaths = pdfFilePaths;
        this.filteredPdfFilePaths = pdfFilePaths;
        fileFilteringTask.setFiles(pdfFilePaths);
        fileFilteringTask.setFilteredFiles(filteredPdfFilePaths);
        fileSortingTask.setFiles(getFilesFromPaths(pdfFilePaths));
        notifyDataSetChanged();

    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    @Override
    public void onFileFiltered(List<String> filteredFiles)
    {
        this.pdfFilePaths = new ArrayList<>(filteredFiles);
        notifyDataSetChanged();
    }

    public FileFilteringTask getFileFilteringTask() {
        return fileFilteringTask;
    }

    private List<File> getFilesFromPaths(List<String> filePaths)
    {
        List<File> files = new ArrayList<>();
        for(String filePath : filePaths)
        {
            files.add(new File(filePath));
        }
        return files;
    }

    private List<String> getPathsFromFiles(List<File> files)
    {
        List<String> paths = new ArrayList<>();
        for(File file : files)
        {
            paths.add(file.getAbsolutePath());
        }
        return paths;
    }

    @Override
    public void onSortingFinished(List<File> sortedFiles)
    {
        bindPDFFiles(getPathsFromFiles(sortedFiles));
    }

    public FileSortingTask getFileSortingTask() {
        return fileSortingTask;
    }
}