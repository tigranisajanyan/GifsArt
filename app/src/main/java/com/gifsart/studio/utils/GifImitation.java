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
    private ViewGroup container;
    private ArrayList<GalleryItem> bitmaps;
    private int duration;
    private boolean play = false;
    private int k = 0;

    public GifImitation(Context context, ViewGroup container, ArrayList<GalleryItem> bitmaps, int duration) {

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
            try {
                TimeUnit.MILLISECONDS.sleep(duration);
            } catch (InterruptedException e) {
                //e.printStackTrace();
            }
            int i = 0;
            while (!bitmaps.get(k % bitmaps.size()).isSeleted()) {
                if (i == bitmaps.size())
                    break;
                i++;
                k++;
            }
            if (i != bitmaps.size())
                publishProgress(k % bitmaps.size());
            k++;
        }

        return null;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        ImageView imageView = (ImageView) container.getChildAt(0);
        VideoView videoView = (VideoView) container.getChildAt(1);
        GifImageView gifImageView = (GifImageView) container.getChildAt(2);

        if (bitmaps.get(values[0]).getType() == GalleryItem.Type.IMAGE) {
            videoView.setVisibility(View.GONE);
            imageView.setVisibility(View.VISIBLE);
            imageView.setImageBitmap(bitmaps.get(values[0]).getBitmap());
        }
        if (bitmaps.get(values[0]).getType() == GalleryItem.Type.VIDEO) {
            videoView.setVisibility(View.VISIBLE);
            imageView.setVisibility(View.GONE);
            videoView.setVideoPath(bitmaps.get(values[0]).getImagePath());
            videoView.start();
        }
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
        play = false;
    }

}
