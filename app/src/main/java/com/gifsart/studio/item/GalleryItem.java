package com.gifsart.studio.item;

import android.graphics.Bitmap;

public class GalleryItem {

    private Bitmap bitmap;
    private String imagePath;
    private boolean isSeleted;
    private int width;
    private int height;
    private boolean isFile;
    private Type type;

    private int position;

    public GalleryItem() {

        this.imagePath = null;
        this.isSeleted = false;
        this.width = 0;
        this.height = 0;

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

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
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

    public enum Type {
        IMAGE,
        GIF,
        VIDEO
    }

}
