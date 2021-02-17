package com.shadattonmoy.imagepickerforandroid.model;

import java.io.File;
import java.util.List;

public class ImageFolder
{

    private String folderName,firstImagePath,folderPath;
    private List<String> imageFilePaths;

    public ImageFolder() {
    }

    public String getFirstImagePath() {
        return firstImagePath;
    }

    public void setFirstImagePath(String firstImagePath) {
        this.firstImagePath = firstImagePath;
    }

    public ImageFolder(String folderPath, String firstImagePath) {
        this.folderPath= folderPath;
        File folder = new File(folderPath);
        this.folderName = folder.getName();
        this.firstImagePath = firstImagePath;
    }

    public String getFolderName() {
        return folderName;
    }

    public void setFolderName(String folderName) {
        this.folderName = folderName;
    }

    public List<String> getImageFilePaths() {
        return imageFilePaths;
    }

    public void setImageFilePaths(List<String> imageFilePaths) {
        this.imageFilePaths = imageFilePaths;
    }

    public String getFolderPath() {
        return folderPath;
    }

    public void setFolderPath(String folderPath) {
        this.folderPath = folderPath;
    }
}
