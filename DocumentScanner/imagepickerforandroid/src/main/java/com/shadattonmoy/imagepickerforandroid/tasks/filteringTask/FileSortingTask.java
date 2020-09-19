package com.shadattonmoy.imagepickerforandroid.tasks.filteringTask;

import java.io.File;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.shadattonmoy.imagepickerforandroid.constants.SortingType;

public class FileSortingTask {

    public interface Listener
    {
        void onSortingFinished(List<File> sortedFiles);
    }

    private List<File> files;
    private Listener listener;

    public FileSortingTask(List<File> files, Listener listener) {
        this.files = files;
        this.listener = listener;
    }

    public void sortFiles(final SortingType sortingType, final boolean ascending)
    {
        Collections.sort(files, new Comparator<File>() {
            @Override
            public int compare(File file1, File file2)
            {
                switch (sortingType)
                {
                    case FILE_TITLE:
                        if(ascending)
                            return file1.getName().compareTo(file2.getName());
                        else return -file1.getName().compareTo(file2.getName());
                    case FILE_SIZE:
                        if(ascending)
                            return Long.compare(file1.length(),file2.length());
                        else return -Long.compare(file1.length(),file2.length());
                    case FILE_LAST_MODIFIED_TIME:
                        if(ascending)
                            return Long.compare(file1.lastModified(),file2.lastModified());
                        else return -Long.compare(file1.lastModified(),file2.lastModified());
                }
                return 0;
            }
        });
        listener.onSortingFinished(files);
    }

    public void setFiles(List<File> files) {
        this.files = files;
    }
}

