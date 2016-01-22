package com.gifsart.studio.utils;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Tigran on 10/1/15.
 */
public enum Type implements Parcelable {
    IMAGE,
    GIF,
    VIDEO,
    NONE;


    public static final Creator<Type> CREATOR = new Creator<Type>() {
        @Override
        public Type createFromParcel(Parcel in) {
            return Type.values()[in.readInt()];
        }

        @Override
        public Type[] newArray(int size) {
            return new Type[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(ordinal());
    }
}
