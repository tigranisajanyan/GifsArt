package com.gifsart.studio.item;

import android.graphics.Bitmap;

import com.gifsart.studio.utils.Type;

import java.util.ArrayList;

/**
 * Created by Tigran on 10/1/15.
 */
public class MakeGifItem {

    private Bitmap bitmap;
    private ArrayList<Bitmap> bitmaps = new ArrayList<>();
    private int duraton;
    private Type type;

    public MakeGifItem() {

    }

    public MakeGifItem(int duraton, Type type) {
        this.duraton = duraton;
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

}
