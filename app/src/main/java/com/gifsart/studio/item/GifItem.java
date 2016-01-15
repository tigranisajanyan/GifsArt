package com.gifsart.studio.item;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

import com.gifsart.studio.activity.MakeGifActivity;
import com.gifsart.studio.clipart.Clipart;
import com.gifsart.studio.utils.Type;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by Tigran on 10/1/15.
 */
public class GifItem implements Parcelable {

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

    protected GifItem(Parcel in) {
        bitmap = in.readParcelable(Bitmap.class.getClassLoader());
        bitmaps = in.createTypedArrayList(Bitmap.CREATOR);
        duraton = in.readInt();
        currentDuration = in.readInt();
        isSelected = in.readByte() != 0;
        maskPosition = in.readInt();
        effectPosition = in.readInt();
        filePath = in.readString();
        filePaths = in.createStringArrayList();
    }

    public static final Creator<GifItem> CREATOR = new Creator<GifItem>() {
        @Override
        public GifItem createFromParcel(Parcel in) {
            return new GifItem(in);
        }

        @Override
        public GifItem[] newArray(int size) {
            return new GifItem[size];
        }
    };

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

    public boolean removeGifItem() {
        if (type == Type.IMAGE) {
            File file = new File(filePath);
            return file.delete();
        } else {
            boolean isDeleted = false;
            for (int i = 0; i < filePaths.size(); i++) {
                File file1 = new File(filePaths.get(i));
                isDeleted = file1.delete();
            }
            File file = new File(filePath);
            return isDeleted && file.delete();
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(bitmap, flags);
        dest.writeTypedList(bitmaps);
        dest.writeInt(duraton);
        dest.writeInt(currentDuration);
        dest.writeByte((byte) (isSelected ? 1 : 0));
        dest.writeInt(maskPosition);
        dest.writeInt(effectPosition);
        dest.writeString(filePath);
        dest.writeStringList(filePaths);
    }
}
