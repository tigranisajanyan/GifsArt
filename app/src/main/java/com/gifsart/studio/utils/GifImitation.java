package com.gifsart.studio.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.widget.ImageView;

import com.gifsart.studio.item.GifItem;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

/**
 * Created by Tigran on 8/6/15.
 */
public class GifImitation extends AsyncTask<Void, Bitmap, Void> {

    private Context context;
    private ImageView container;
    private ArrayList<GifItem> gifItems;
    private int duration;
    private boolean play = false;
    private int k = 0;

    public GifImitation(Context context, ImageView container, ArrayList<GifItem> gifItems, int duration) {

        this.container = container;
        this.gifItems = gifItems;
        this.duration = duration;
        this.context = context;
    }

    public void changeDuration(int duration) {
        this.duration = duration;
        for (int i = 0; i < gifItems.size(); i++) {
            gifItems.get(i).setDuraton(gifItems.get(i).getDuraton() * duration / 100);
        }
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        play = true;
    }

    @Override
    protected Void doInBackground(Void... params) {
        while (play) {

            if (gifItems.get(k % gifItems.size()).getType() == Type.IMAGE) {
                publishProgress(gifItems.get(k % gifItems.size()).getBitmap());
                try {
                    TimeUnit.MILLISECONDS.sleep(gifItems.get(k % gifItems.size()).getDuraton());
                } catch (InterruptedException e) {
                    //e.printStackTrace();
                }
                k++;
            } else if (gifItems.get(k % gifItems.size()).getType() == Type.GIF) {
                for (int i = 0; i < gifItems.get(k % gifItems.size()).getBitmaps().size(); i++) {
                    publishProgress(gifItems.get(k % gifItems.size()).getBitmaps().get(i));
                    try {
                        TimeUnit.MILLISECONDS.sleep(gifItems.get(k % gifItems.size()).getDuraton());
                    } catch (InterruptedException e) {
                        //e.printStackTrace();
                    }
                }
                k++;

            } else if ((gifItems.get(k % gifItems.size()).getType() == Type.VIDEO)) {
                for (int i = 0; i < gifItems.get(k % gifItems.size()).getBitmaps().size(); i++) {
                    publishProgress(gifItems.get(k % gifItems.size()).getBitmaps().get(i));
                    try {
                        TimeUnit.MILLISECONDS.sleep(gifItems.get(k % gifItems.size()).getDuraton());
                    } catch (InterruptedException e) {
                        //e.printStackTrace();
                    }
                }
                k++;
            }
            try {
                TimeUnit.MILLISECONDS.sleep(duration);
            } catch (InterruptedException e) {
                //e.printStackTrace();
            }


        }

        return null;
    }

    @Override
    protected void onProgressUpdate(Bitmap... values) {
        super.onProgressUpdate(values);
        container.setImageBitmap(values[0]);

    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
        play = false;
    }

}
