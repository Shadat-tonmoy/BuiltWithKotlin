package com.shadattonmoy.imagepickerforandroid.tasks.filteringTask;

import android.widget.Filter;
import android.widget.Filterable;

import java.util.ArrayList;
import java.util.List;

public class FileFilteringTask implements Filterable {

    public interface Listener{
        void onFileFiltered(List<String> filteredFiles);
    }

    private List<String> filteredFiles,files;
    private Listener listener;

    public FileFilteringTask(List<String> filteredFiles , List<String> files) {
        this.filteredFiles = filteredFiles ;
        this.files= files;
    }


    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String text = constraint.toString();
                if(text.isEmpty())
                {
                    filteredFiles = files;
                }
                else
                {
                    List<String> filteredFileList = new ArrayList<>();
                    for(String file:files)
                    {
                        if(file.toLowerCase().contains(text.toLowerCase()))
                            filteredFileList.add(file);
                    }
                    filteredFiles = filteredFileList ;
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = filteredFiles ;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                filteredFiles = (List<String>) results.values;
                if(listener!=null)
                    listener.onFileFiltered(filteredFiles);

            }
        };
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    public void setFilteredFiles(List<String> filteredFiles) {
        this.filteredFiles = filteredFiles;
    }

    public void setFiles(List<String> files) {
        this.files = files;
    }
}

