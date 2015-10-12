package com.gifsart.studio.gifutils;

import android.graphics.Bitmap;

import com.gifsart.studio.utils.GifsArtConst;
import com.gifsart.studio.utils.Utils;

import java.io.IOException;
import java.util.ArrayList;

import pl.droidsonroids.gif.GifDrawable;

/**
 * Created by Tigran on 10/7/15.
 */
public class GifUtils {

    public static ArrayList<Bitmap> getGifFrames(String gifPath) {
        ArrayList<Bitmap> frameBitmaps = new ArrayList<>();
        GifDrawable gifDrawable = null;
        try {
            gifDrawable = new GifDrawable(gifPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        for (int i = 0; i < gifDrawable.getNumberOfFrames(); i++) {
            frameBitmaps.add(Utils.scaleCenterCrop(gifDrawable.seekToFrameAndGet(i), GifsArtConst.GIF_FRAME_SIZE, GifsArtConst.GIF_FRAME_SIZE));
        }
        return frameBitmaps;
    }

    public static int getGifFrameDuration(String gifPath) {
        GifDrawable gifDrawable = null;
        try {
            gifDrawable = new GifDrawable(gifPath);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return gifDrawable.getFrameDuration(0);
    }
}
