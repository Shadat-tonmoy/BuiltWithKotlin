package com.shadattonmoy.imagepickerforandroid.model;

import android.net.Uri;

public class ImageFile
{
    private String displayName, pathString, relativePath;
    private long lastModified;
    private Uri imageFileUri;

    public ImageFile(String displayName, String pathString, String relativePath, long lastModified, Uri imageFileUri) {
        this.displayName = displayName;
        this.pathString = pathString;
        this.relativePath = relativePath;
        this.lastModified = lastModified;
        this.imageFileUri = imageFileUri;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getPathString() {
        return pathString;
    }

    public void setPathString(String pathString) {
        this.pathString = pathString;
    }

    public String getRelativePath() {
        return relativePath;
    }

    public void setRelativePath(String relativePath) {
        this.relativePath = relativePath;
    }

    public long getLastModified() {
        return lastModified;
    }

    public void setLastModified(long lastModified) {
        this.lastModified = lastModified;
    }

    public Uri getImageFileUri() {
        return imageFileUri;
    }

    public void setImageFileUri(Uri imageFileUri) {
        this.imageFileUri = imageFileUri;
    }

    @Override
    public String toString() {
        return "ImageFile{" +
                "displayName='" + displayName + '\'' +
                ", pathString='" + pathString + '\'' +
                ", relativePath='" + relativePath + '\'' +
                ", lastModified=" + lastModified +
                ", imageFileUri=" + imageFileUri +
                '}';
    }
}
