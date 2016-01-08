package com.gifsart.studio.item;

import android.graphics.Bitmap;

import com.gifsart.studio.activity.MakeGifActivity;
import com.gifsart.studio.clipart.Clipart;
import com.gifsart.studio.utils.Type;

import java.util.ArrayList;

/**
 * Created by Tigran on 10/1/15.
 */
public class GifItem {

    private Bitmap bitmap;
    private ArrayList<Bitmap> bitmaps = new ArrayList<>();
    private int duraton;
    private int currentDuration;
    private Type type = Type.NONE;
    private ArrayList<Clipart> cliparts;

    private boolean isSelected = true;

    private int maskPosition = 0;
    private int effectPosition = 0;
    private MakeGifActivity.SquareFitMode squareFitMode = MakeGifActivity.SquareFitMode.FIT_MODE_SQUARE;

    private String filePath;
    private ArrayList<String> filePaths;

    public GifItem() {

    }

    public GifItem(int duraton, Type type) {
        this.duraton = duraton;
        this.type = type;
    }

    public GifItem(int duraton, int currentDuration, Type type) {
        this.duraton = duraton;
        this.currentDuration = currentDuration;
        this.type = type;
    }

    public ArrayList<Bitmap> getBitmaps() {
        return bitmaps;
    }

    public void setBitmaps(ArrayList<Bitmap> bitmaps) {
        this.bitmaps = bitmaps;
    }

    public int getDuraton() {
        return duraton;
    }

    public void setDuraton(int duraton) {
        this.duraton = duraton;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }


    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public ArrayList<Clipart> getClipart() {
        return cliparts;
    }

    public void setClipart(ArrayList<Clipart> cliparts) {
        this.cliparts = cliparts;
    }


    public int getCurrentDuration() {
        return currentDuration;
    }

    public void setCurrentDuration(int currentDuration) {
        this.currentDuration = currentDuration;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setIsSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public ArrayList<String> getFilePaths() {
        return filePaths;
    }

    public void setFilePaths(ArrayList<String> filePaths) {
        this.filePaths = filePaths;
    }

}
