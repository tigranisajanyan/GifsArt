package com.gifsart.studio.item;

import android.graphics.Bitmap;

import com.gifsart.studio.utils.Type;

public class GalleryItem {

    private Bitmap bitmap = null;
    private String imagePath = null;
    private boolean isSeleted = false;
    private int width = 0;
    private int height = 0;
    private boolean isFile = false;
    private Type type;
    private int frameDuration = 0;

    public GalleryItem() {

    }

    public GalleryItem(String imagePath) {
        this.imagePath = imagePath;
    }

    public GalleryItem(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public GalleryItem(Bitmap bitmap, String imagePath, boolean isSeleted, boolean isFile, int width, int height) {

        this.bitmap = bitmap;
        this.imagePath = imagePath;
        this.isSeleted = isSeleted;
        this.isFile = isFile;
        this.width = width;
        this.height = height;

    }

    public GalleryItem(Bitmap bitmap, String imagePath, boolean isSeleted, boolean isFile, int width, int height, Type type) {

        this.bitmap = bitmap;
        this.imagePath = imagePath;
        this.isSeleted = isSeleted;
        this.isFile = isFile;
        this.width = width;
        this.height = height;
        this.type = type;

    }

    public boolean isFile() {
        return isFile;
    }

    public void setIsFile(boolean isFile) {
        this.isFile = isFile;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public boolean isSeleted() {
        return isSeleted;
    }

    public void setIsSeleted(boolean isSeleted) {
        this.isSeleted = isSeleted;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public int getFrameDuration() {
        return frameDuration;
    }

    public void setFrameDuration(int frameDuration) {
        this.frameDuration = frameDuration;
    }

}
