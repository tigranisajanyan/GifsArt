package com.gifsart.studio.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.widget.ImageView;

import com.gifsart.studio.item.MakeGifItem;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

/**
 * Created by Tigran on 8/6/15.
 */
public class GifImitation extends AsyncTask<Void, Bitmap, Void> {

    private Context context;
    private ImageView container;
    private ArrayList<MakeGifItem> makeGifItems;
    private int duration;
    private boolean play = false;
    private int k = 0;

    public GifImitation(Context context, ImageView container, ArrayList<MakeGifItem> makeGifItems, int duration) {

        this.container = container;
        this.makeGifItems = makeGifItems;
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

            if (makeGifItems.get(k % makeGifItems.size()).getType() == Type.IMAGE) {
                publishProgress(makeGifItems.get(k % makeGifItems.size()).getBitmap());
                try {
                    TimeUnit.MILLISECONDS.sleep(makeGifItems.get(k % makeGifItems.size()).getDuraton());
                } catch (InterruptedException e) {
                    //e.printStackTrace();
                }
                k++;
            } else if (makeGifItems.get(k % makeGifItems.size()).getType() == Type.GIF) {
                for (int i = 0; i < makeGifItems.get(k % makeGifItems.size()).getBitmaps().size(); i++) {
                    publishProgress(makeGifItems.get(k % makeGifItems.size()).getBitmaps().get(i));
                    try {
                        TimeUnit.MILLISECONDS.sleep(makeGifItems.get(k % makeGifItems.size()).getDuraton());
                    } catch (InterruptedException e) {
                        //e.printStackTrace();
                    }
                }
                k++;

            } else if ((makeGifItems.get(k % makeGifItems.size()).getType() == Type.VIDEO)) {
                for (int i = 0; i < makeGifItems.get(k % makeGifItems.size()).getBitmaps().size(); i++) {
                    publishProgress(makeGifItems.get(k % makeGifItems.size()).getBitmaps().get(i));
                    try {
                        TimeUnit.MILLISECONDS.sleep(makeGifItems.get(k % makeGifItems.size()).getDuraton());
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
