package com.gifsart.studio.gifutils;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;

import com.gifsart.studio.item.GifItem;
import com.gifsart.studio.utils.ThreadControl;
import com.gifsart.studio.utils.Type;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import jp.co.cyberagent.android.gpuimage.GPUImageView;

/**
 * Created by Tigran on 8/6/15.
 */
public class GifImitation extends AsyncTask<Void, Bitmap, Void> {

    private Context context;
    private GPUImageView container;
    private ArrayList<GifItem> gifItems;
    private int duration;
    private boolean play = false;
    private int k = 0;
    int index;

    ThreadControl tControl = new ThreadControl();

    int count = 0;
    int width = 0;
    int height = 0;

    public GifImitation(Context context, GPUImageView container, ArrayList<GifItem> gifItems, int duration) {

        this.container = container;
        this.gifItems = gifItems;
        this.duration = duration;
        this.context = context;
    }

    public void changeDuration(int duration) {
        this.duration = duration;
        for (int i = 0; i < gifItems.size(); i++) {
            gifItems.get(i).setCurrentDuration(gifItems.get(i).getDuraton() * duration / 10);
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
            index = k % gifItems.size();
            if (gifItems.get(index).getType() == Type.IMAGE && gifItems.get(index).isSelected()) {
                try {
                    //Pause work if control is paused.
                    tControl.waitIfPaused();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                //Stop work if control is cancelled.
                if (tControl.isCancelled()) {
                    break;
                }

                publishProgress(gifItems.get(index).getBitmap());
                try {
                    TimeUnit.MILLISECONDS.sleep(gifItems.get(index).getCurrentDuration());
                } catch (InterruptedException e) {
                    //e.printStackTrace();
                }
                ++count;
            } else if (gifItems.get(index).getType() == Type.GIF && gifItems.get(index).isSelected()) {
                for (int i = 0; i < gifItems.get(index).getBitmaps().size(); i++) {
                    try {
                        //Pause work if control is paused.
                        tControl.waitIfPaused();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    //Stop work if control is cancelled.
                    if (tControl.isCancelled()) {
                        break;
                    }

                    publishProgress(gifItems.get(index).getBitmaps().get(i));
                    try {
                        TimeUnit.MILLISECONDS.sleep(gifItems.get(index).getCurrentDuration());
                    } catch (InterruptedException e) {
                        //e.printStackTrace();
                    }
                    ++count;
                }
            } else if ((gifItems.get(index).getType() == Type.VIDEO) && gifItems.get(index).isSelected()) {
                for (int i = 0; i < gifItems.get(index).getBitmaps().size(); i++) {
                    try {
                        //Pause work if control is paused.
                        tControl.waitIfPaused();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    //Stop work if control is cancelled.
                    if (tControl.isCancelled()) {
                        break;
                    }

                    publishProgress(gifItems.get(index).getBitmaps().get(i));
                    try {
                        TimeUnit.MILLISECONDS.sleep(gifItems.get(index).getCurrentDuration());
                    } catch (InterruptedException e) {
                        //e.printStackTrace();
                    }
                    ++count;
                }
            }
            try {
                TimeUnit.MILLISECONDS.sleep(duration);
            } catch (InterruptedException e) {
                //e.printStackTrace();
            }
            k++;
            if (isCancelled()) break;
        }
        return null;
    }

    @Override
    protected void onProgressUpdate(Bitmap... values) {
        super.onProgressUpdate(values);
        Bitmap bitmap = values[0];
        if (width != bitmap.getWidth() || height != bitmap.getHeight()) {
            container.getGPUImage().deleteImage();
            width = bitmap.getWidth();
            height = bitmap.getHeight();
        }
        container.getGPUImage().setImage(bitmap);
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
        play = false;
    }

    public void onPause() {
        //No need to pause if we are getting destroyed
        //and will cancel thread control anyway.
        //Pause control.
        tControl.pause();
    }

    public void onResume() {
        tControl.resume();
    }

    public void showCurrentPosition(int position) {
        gifItems.get(position).setIsSelected(true);
        for (int i = 0; i < gifItems.size(); i++) {
            if (i != position) {
                gifItems.get(i).setIsSelected(false);
            }
        }
        index = position;
    }

    public void showAllPositions() {
        for (int i = 0; i < gifItems.size(); i++) {
            gifItems.get(i).setIsSelected(true);
        }
        k = 0;
    }

}
