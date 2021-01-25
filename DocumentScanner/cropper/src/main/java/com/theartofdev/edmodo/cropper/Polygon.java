package com.theartofdev.edmodo.cropper;


import androidx.annotation.NonNull;

public class Polygon {
    public float topLeftX = -1, topLeftY = -1, topRightX = -1, topRightY = -1, bottomLeftX = -1, bottomLeftY = -1, bottomRightX = -1, bottomRightY = -1;

    public Polygon() {
    }

    public Polygon(float topLeftX, float topLeftY, float topRightX, float topRightY, float bottomLeftX, float bottomLeftY, float bottomRightX, float bottomRightY) {
        this.topLeftX = topLeftX;
        this.topLeftY = topLeftY;
        this.topRightX = topRightX;
        this.topRightY = topRightY;
        this.bottomLeftX = bottomLeftX;
        this.bottomLeftY = bottomLeftY;
        this.bottomRightX = bottomRightX;
        this.bottomRightY = bottomRightY;
    }

    public float getTopLeftX() {
        return topLeftX;
    }

    public void setTopLeftX(float topLeftX) {
        this.topLeftX = topLeftX;
    }

    public float getTopLeftY() {
        return topLeftY;
    }

    public void setTopLeftY(float topLeftY) {
        this.topLeftY = topLeftY;
    }

    public float getTopRightX() {
        return topRightX;
    }

    public void setTopRightX(float topRightX) {
        this.topRightX = topRightX;
    }

    public float getTopRightY() {
        return topRightY;
    }

    public void setTopRightY(float topRightY) {
        this.topRightY = topRightY;
    }

    public float getBottomLeftX() {
        return bottomLeftX;
    }

    public void setBottomLeftX(float bottomLeftX) {
        this.bottomLeftX = bottomLeftX;
    }

    public float getBottomLeftY() {
        return bottomLeftY;
    }

    public void setBottomLeftY(float bottomLeftY) {
        this.bottomLeftY = bottomLeftY;
    }

    public float getBottomRightX() {
        return bottomRightX;
    }

    public void setBottomRightX(float bottomRightX) {
        this.bottomRightX = bottomRightX;
    }

    public float getBottomRightY() {
        return bottomRightY;
    }

    public void setBottomRightY(float bottomRightY) {
        this.bottomRightY = bottomRightY;
    }

    public void set(@NonNull Polygon src) {
        this.topLeftX = src.topLeftX;
        this.topLeftY = src.topLeftY;
        this.topRightX = src.topRightX;
        this.topRightY = src.topRightY;
        this.bottomLeftX = src.bottomLeftX;
        this.bottomLeftY = src.bottomLeftY;
        this.bottomRightX = src.bottomRightX;
        this.bottomRightY = src.bottomRightY;
    }

    @Override
    public String toString() {
        return "Polygon{" +
                "topLeftX=" + topLeftX +
                ", topLeftY=" + topLeftY +
                ", topRightX=" + topRightX +
                ", topRightY=" + topRightY +
                ", bottomLeftX=" + bottomLeftX +
                ", bottomLeftY=" + bottomLeftY +
                ", bottomRightX=" + bottomRightX +
                ", bottomRightY=" + bottomRightY +
                '}';
    }

    public boolean notSetYet()
    {
        return topLeftX == -1 && topLeftY == -1 && topRightX == -1 && topRightY == -1 && bottomLeftX == -1 && bottomLeftY == -1 && bottomRightX == -1 && bottomRightY == -1;
    }
}
