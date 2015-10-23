package com.gifsart.studio.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.AsyncTask;

import com.gifsart.studio.gifutils.GifUtils;
import com.gifsart.studio.item.GifItem;

import java.util.ArrayList;

import pl.droidsonroids.gif.GifDrawable;

/**
 * Created by Tigran on 10/22/15.
 */
public class AddMaskAsyncTask extends AsyncTask<Void, Void, Void> {

    private ArrayList<GifItem> gifItems = new ArrayList<>();
    private ArrayList<Bitmap> bitmapArrayList = new ArrayList<>();
    private Context context;
    private int resourceId;
    private MergeGifs mergeGifs;

    private int pos = 0;
    private int size = 0;

    public AddMaskAsyncTask(ArrayList<GifItem> gifItems, int resourceId, Context context) {
        this.gifItems = gifItems;
        this.resourceId = resourceId;
        this.context = context;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        bitmapArrayList = GifUtils.getGifFramesFromResources(context, resourceId);
        size = bitmapArrayList.size();

    }

    @Override
    protected Void doInBackground(Void... params) {

        for (int i = 0; i < gifItems.size(); i++) {
            if (gifItems.get(i).getType() == Type.IMAGE) {
                drawClipart(gifItems.get(i).getBitmap(), bitmapArrayList.get(pos % size));
                pos++;
            } else if (gifItems.get(i).getType() == Type.GIF) {
                for (int j = 0; j < gifItems.get(i).getBitmaps().size(); j++) {
                    drawClipart(gifItems.get(i).getBitmaps().get(j), bitmapArrayList.get(pos % size));
                    pos++;
                }
            } else if (gifItems.get(i).getType() == Type.VIDEO) {
                for (int j = 0; j < gifItems.get(i).getBitmaps().size(); j++) {
                    drawClipart(gifItems.get(i).getBitmaps().get(j), bitmapArrayList.get(pos % size));
                    pos++;
                }
            }
        }

        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        mergeGifs.Merged(true);
    }

    public void drawClipart(Bitmap mainFrame, Bitmap maskFrame) {
        Canvas canvas = new Canvas(mainFrame);
        Paint paint = new Paint();
        Rect originalRect = new Rect(0, 0, maskFrame.getWidth(), maskFrame.getHeight());
        Rect newRect = new Rect(0, 0, canvas.getWidth(), canvas.getHeight());
        canvas.drawBitmap(maskFrame, originalRect, newRect, paint);
    }

    public void setMergeGifsListener(MergeGifs mergeGifs) {
        this.mergeGifs = mergeGifs;
    }

    public interface MergeGifs {
        public void Merged(boolean done);
    }

}
