package com.gifsart.studio.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Tigran on 1/15/16.
 */
public class FileCounterSingleton {

    private Context context;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private static FileCounterSingleton mInstance = null;
    private int currentIndex;

    private FileCounterSingleton(Context context) {
        this.context = context;
        currentIndex = 0;
        sharedPreferences = context.getSharedPreferences(GifsArtConst.SHARED_PREFERENCES, context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putInt("file_counter_index", currentIndex);
        editor.commit();
    }

    public static FileCounterSingleton getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new FileCounterSingleton(context);
        }
        return mInstance;
    }

    public int increaseIndex() {
        currentIndex++;
        putNewIndex();
        return currentIndex;
    }

    public void resetFileCounterIndex() {
        currentIndex = 0;
        putNewIndex();
    }

    private void putNewIndex() {
        editor.putInt("file_counter_index", currentIndex);
        editor.commit();
    }


}
