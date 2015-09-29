package com.gifsart.studio.utils;

import android.content.Context;
import android.os.AsyncTask;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.VideoView;

import com.gifsart.studio.item.GalleryItem;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import pl.droidsonroids.gif.GifImageView;

/**
 * Created by Tigran on 8/6/15.
 */
public class GifImitation extends AsyncTask<Void, Integer, Void> {

    private Context context;
    private ImageView container;
    private ArrayList<GalleryItem> bitmaps;
    private int duration;
    private boolean play = false;
    private int k = 0;

    public GifImitation(Context context, ImageView container, ArrayList<GalleryItem> bitmaps, int duration) {

        this.container = container;
        this.bitmaps = bitmaps;
        this.duration = duration;
        this.context = context;
    }

    public void changeDuration(int duration) {
        this.duration = duration;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        play = true;
    }

    @Override
    protected Void doInBackground(Void... params) {
        while (play) {

            int i = 0;
            while (!bitmaps.get(k % bitmaps.size()).isSeleted()) {
                if (i == bitmaps.size())
                    break;
                i++;
                k++;
            }
            if (i != bitmaps.size())
                publishProgress(k % bitmaps.size());

            try {
                TimeUnit.MILLISECONDS.sleep(bitmaps.get(k % bitmaps.size()).getFrameDuration());
            } catch (InterruptedException e) {
                //e.printStackTrace();
            }
            k++;
        }

        return null;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        container.setImageBitmap(bitmaps.get(values[0]).getBitmap());

    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
        play = false;
    }

}
